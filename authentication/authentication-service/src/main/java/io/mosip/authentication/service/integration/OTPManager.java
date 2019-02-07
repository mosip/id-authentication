package io.mosip.authentication.service.integration;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.integration.dto.OTPValidateResponseDTO;
import io.mosip.authentication.service.integration.dto.OtpGeneratorRequestDto;
import io.mosip.authentication.service.integration.dto.OtpGeneratorResponseDto;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * OTPManager handling with OTP-Generation and OTP-Validation.
 * 
 * @author Rakesh Roshan
 */
@Component
public class OTPManager {

	private static final String VALIDATION_UNSUCCESSFUL = "VALIDATION_UNSUCCESSFUL";

	private static final String OTP_EXPIRED = "OTP_EXPIRED";

	private static final String STATUS_SUCCESS = "success";

	private static final String STATUS_FAILURE = "failure";

	private static final String USER_BLOCKED = "USER_BLOCKED";

	@Autowired
	private RestHelper restHelper;

	@Autowired
	private RestRequestFactory restRequestFactory;

	private static Logger logger = IdaLogger.getLogger(OTPManager.class);

	private OTPValidateResponseDTO otpvalidateresponsedto;

	/**
	 * Generate OTP with information of
	 * {@link MediaType } and OTP generation time-out
	 *
	 * @param otpKey the otp key
	 * @return String(otp)
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public String generateOTP(String otpKey) throws IdAuthenticationBusinessException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey(otpKey);
		OtpGeneratorResponseDto otpGeneratorResponsetDto = null;
		RestRequestDTO restRequestDTO = null;
		String response = null;
		try {
			restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE,
					otpGeneratorRequestDto, OtpGeneratorResponseDto.class);
			otpGeneratorResponsetDto = restHelper.requestSync(restRequestDTO);
			response = otpGeneratorResponsetDto.getOtp();
			logger.info("NA", "NA", "NA", "otpGeneratorResponsetDto " + response);

		} catch (RestServiceException e) {

			Optional<Object> responseBody = e.getResponseBody();
			if (responseBody.isPresent()) {
				otpGeneratorResponsetDto = (OtpGeneratorResponseDto) responseBody.get();
				String status = otpGeneratorResponsetDto.getStatus();
				String message = otpGeneratorResponsetDto.getMessage();
				if (status != null && status.equalsIgnoreCase(STATUS_FAILURE)
						&& message.equalsIgnoreCase(USER_BLOCKED)) {
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.BLOCKED_OTP_TO_GENERATE);

				}
			} else {
				// FIXME Could not validate OTP -OTP - Request could not be processed. Please
				// try again
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR);
			}

			logger.error("NA", "NA", e.getErrorCode(), e.getErrorText());

		} catch (IDDataValidationException e) {
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.KERNEL_OTP_GENERATION_REQUEST_FAILED, e);
		}
		return response;
	}

	/**
	 * Validate method for OTP Validation.
	 *
	 * @param pinValue the pin value
	 * @param otpKey the otp key
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public boolean validateOtp(String pinValue, String otpKey) throws IdAuthenticationBusinessException {
		boolean isValidOtp = false;
		try {
			RestRequestDTO restreqdto = restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE,
					null, OTPValidateResponseDTO.class);
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("key", otpKey);
			params.add("otp", pinValue);
			restreqdto.setParams(params);
			otpvalidateresponsedto = restHelper.requestSync(restreqdto);
			isValidOtp = Optional.ofNullable(otpvalidateresponsedto).map(OTPValidateResponseDTO::getStatus)
					.filter(status -> status.equalsIgnoreCase(STATUS_SUCCESS)).isPresent();
		} catch (RestServiceException e) {
			logger.error("NA", "NA", e.getErrorCode() + e.getErrorText(), e.getResponseBodyAsString().orElse(""));

			Optional<Object> responseBody = e.getResponseBody();
			if (responseBody.isPresent()) {
				otpvalidateresponsedto = (OTPValidateResponseDTO) responseBody.get();
				String status = otpvalidateresponsedto.getStatus();
				String message = otpvalidateresponsedto.getMessage();
				if (status != null) {
					if (status.equalsIgnoreCase(STATUS_FAILURE)) {
						throwOtpException(message);
					}
				} else {
					throwKeyNotFound(e);
				}
			}
		} catch (IDDataValidationException e) {
			logger.error("NA", "NA", "Inside validateOtp", null);
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.KERNEL_OTP_VALIDATION_REQUEST_FAILED, e);
		}
		return isValidOtp;
	}

	private void throwKeyNotFound(RestServiceException e) throws IdAuthenticationBusinessException {
		Optional<String> errorCode = e.getResponseBodyAsString().flatMap(this::getErrorCode);
		// Do not throw server error for OTP not generated, throw invalid OTP error
		// instead
		if (errorCode
				.filter(code -> code.equals(
						IdAuthenticationErrorConstants.VAL_KEY_NOT_FOUND_OTP_NOT_GENERATED.getErrorCode()))
				.isPresent()) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_OTP);
		}
		throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR);
	}

	private void throwOtpException(String message) throws IdAuthenticationBusinessException {
		if (message.equalsIgnoreCase(USER_BLOCKED)) {
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.BLOCKED_OTP_TO_VALIDATE);
		} else if (message.equalsIgnoreCase(OTP_EXPIRED)) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.EXPIRED_OTP);
		} else if (message.equalsIgnoreCase(VALIDATION_UNSUCCESSFUL)) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_OTP);
		} else {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR);
		}
	}

	/**
	 * Gets the error code.
	 *
	 * @param resBody the res body
	 * @return the error code
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Optional<String> getErrorCode(String resBody) {
		return Optional.of(resBody).map(str -> {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> res = null;
			try {
				res = mapper.readValue(str, Map.class);
			} catch (IOException e) {
				logger.error("NA", "NA", "Error parsing response body", null);
			}
			return res;
		}).map(map -> map.get("errors")).filter(obj -> obj instanceof List).flatMap(obj -> ((List) obj).stream()
				.filter(obj1 -> obj1 instanceof Map).map(map1 -> (((Map) map1).get("errorCode"))).findAny())
				.map(String::valueOf);
	}

}
