package io.mosip.authentication.service.integration;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

	@Autowired
	private RestHelper restHelper;

	@Autowired
	private RestRequestFactory restRequestFactory;

	private static Logger logger = IdaLogger.getLogger(OTPManager.class);

	private OTPValidateResponseDTO otpvalidateresponsedto;

	/**
	 * Generate OTP with information of
	 * {@link RestServiceContants.OTP_GENERATE_SERVICE} , {@link HttpMethod.POST},
	 * {@link MediaType } and OTP generation time-out
	 * 
	 * @param otpKey
	 * @return String(otp)
	 * @throws IdAuthenticationBusinessException
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
		} catch (RestServiceException | IDDataValidationException e) {
			logger.error("NA", "NA", e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.KERNEL_OTP_GENERATION_REQUEST_FAILED, e);
		}
		return response;
	}

	/**
	 * Validate method for OTP Validation
	 * 
	 * @param pinValue
	 * @param otpKey
	 * @return
	 * @throws IdAuthenticationBusinessException
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
					.filter(status -> status.equalsIgnoreCase("true")).isPresent();
		} catch (RestServiceException | IDDataValidationException e) {
			logger.error("NA", "NA", e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.KERNEL_OTP_VALIDATION_REQUEST_FAILED, e);
		}
		return isValidOtp;
	}

}
