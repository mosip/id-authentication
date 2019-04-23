package io.mosip.authentication.common.service.integration;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.integration.dto.OtpGeneratorRequestDto;
import io.mosip.authentication.common.service.integration.dto.OtpGeneratorResponseDto;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * OTPManager handling with OTP-Generation and OTP-Validation.
 * 
 * @author Rakesh Roshan
 * @author Dinesh Karuppiah.T
 */
@Component
public class OTPManager {

	private static final String KER_OTP_KEY_NOT_EXISTS_CODE = "KER-OTV-005";

	/** The Constant RESPONSE. */
	private static final String RESPONSE = "response";

	/** The Constant STATUS. */
	private static final String STATUS = "status";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SESSION_ID";

	/** The Constant VALIDATION_UNSUCCESSFUL. */
	private static final String VALIDATION_UNSUCCESSFUL = "VALIDATION_UNSUCCESSFUL";

	/** The Constant OTP_EXPIRED. */
	private static final String OTP_EXPIRED = "OTP_EXPIRED";

	/** The Constant STATUS_SUCCESS. */
	private static final String STATUS_SUCCESS = "success";

	/** The Constant STATUS_FAILURE. */
	private static final String STATUS_FAILURE = "failure";

	/** The Constant USER_BLOCKED. */
	private static final String USER_BLOCKED = "USER_BLOCKED";

	/** The rest helper. */
	@Autowired
	private RestHelper restHelper;

	/** The rest request factory. */
	@Autowired
	private RestRequestFactory restRequestFactory;

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(OTPManager.class);

	/**
	 * Generate OTP with information of {@link MediaType } and OTP generation
	 * time-out
	 *
	 * @param otpKey the otp key
	 * @return String(otp)
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@SuppressWarnings("unchecked")
	public String generateOTP(String otpKey) throws IdAuthenticationBusinessException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey(otpKey);
		RestRequestDTO restRequestDTO = null;
		String response = null;
		try {
			restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE,
					RestRequestFactory.createRequest(otpGeneratorRequestDto), ResponseWrapper.class);
			ResponseWrapper<OtpGeneratorResponseDto> otpGeneratorResponsetDto = restHelper.requestSync(restRequestDTO);
			response = (String) ((Map<String, Object>) otpGeneratorResponsetDto.getResponse()).get("otp");
			logger.info(SESSION_ID, this.getClass().getSimpleName(), "generateOTP",
					"otpGeneratorResponsetDto " + response);

		} catch (RestServiceException e) {

			Optional<Object> responseBody = e.getResponseBody();
			if (responseBody.isPresent()) {
				Map<String, Object> res = (Map<String, Object>) responseBody.get();
				String status = res.get(RESPONSE) instanceof Map
						? (String) ((Map<String, Object>) res.get(RESPONSE)).get(STATUS)
						: null;
				String message = res.get(RESPONSE) instanceof Map
						? (String) ((Map<String, Object>) res.get(RESPONSE)).get("message")
						: null;
				if (status != null && status.equalsIgnoreCase(STATUS_FAILURE)
						&& message.equalsIgnoreCase(USER_BLOCKED)) {
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.BLOCKED_OTP_GENERATE);

				}
			} else {
				// FIXME Could not validate OTP -OTP - Request could not be processed. Please
				// try again
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR);
			}

			logger.error(SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());

		} catch (IDDataValidationException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, e);
		}
		return response;
	}

	/**
	 * Validate method for OTP Validation.
	 *
	 * @param pinValue the pin value
	 * @param otpKey   the otp key
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@SuppressWarnings("unchecked")
	public boolean validateOtp(String pinValue, String otpKey) throws IdAuthenticationBusinessException {
		boolean isValidOtp = false;
		try {
			RestRequestDTO restreqdto = restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE,
					null, Map.class);
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("key", otpKey);
			params.add("otp", pinValue);
			restreqdto.setParams(params);
			Map<String, Object> otpvalidateresponsedto = restHelper.requestSync(restreqdto);
			isValidOtp = Optional.ofNullable((Map<String, Object>) otpvalidateresponsedto.get(RESPONSE))
					.filter(res -> res.containsKey(STATUS)).map(res -> String.valueOf(res.get(STATUS)))
					.filter(status -> status.equalsIgnoreCase(STATUS_SUCCESS)).isPresent();
		} catch (RestServiceException e) {
			logger.error(SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode() + e.getErrorText(),
					e.getResponseBodyAsString().orElse(""));

			Optional<Object> responseBody = e.getResponseBody();
			if (responseBody.isPresent()) {
				Map<String, Object> res = (Map<String, Object>) responseBody.get();
				Object status = res.get(RESPONSE) instanceof Map ? ((Map<String, Object>) res.get(RESPONSE)).get(STATUS)
						: null;
				Object message = res.get(RESPONSE) instanceof Map
						? ((Map<String, Object>) res.get(RESPONSE)).get("message")
						: null;
				if (Objects.nonNull(status) && ((String) status).equalsIgnoreCase(STATUS_FAILURE)) {
					if (status instanceof String && message instanceof String) {
						throwOtpException((String) message);
					}
				} else {
					throwKeyNotFound(e);
				}
			}
		} catch (IDDataValidationException e) {
			logger.error(SESSION_ID, this.getClass().getSimpleName(), "Inside validateOtp", null);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		}
		return isValidOtp;
	}

	/**
	 * Throws KeyNotFound Exception when otp key is Invalid
	 * 
	 * @param e
	 * @throws IdAuthenticationBusinessException
	 */
	private void throwKeyNotFound(RestServiceException e) throws IdAuthenticationBusinessException {
		Optional<String> errorCode = e.getResponseBodyAsString().flatMap(this::getErrorCode);
		// Do not throw server error for OTP not generated, throw invalid OTP error
		// instead
		if (errorCode.filter(code -> code.equalsIgnoreCase(KER_OTP_KEY_NOT_EXISTS_CODE)).isPresent()) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_OTP);
		}
		throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
	}

	private void throwOtpException(String message) throws IdAuthenticationBusinessException {
		if (message.equalsIgnoreCase(USER_BLOCKED)) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.BLOCKED_OTP_VALIDATE);
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
				logger.error(SESSION_ID, this.getClass().getSimpleName(), "Error parsing response body", null);
			}
			return res;
		}).map(map -> map.get("errors")).filter(obj -> obj instanceof List).flatMap(obj -> ((List) obj).stream()
				.filter(obj1 -> obj1 instanceof Map).map(map1 -> (((Map) map1).get("errorCode"))).findAny())
				.map(String::valueOf);
	}

}
