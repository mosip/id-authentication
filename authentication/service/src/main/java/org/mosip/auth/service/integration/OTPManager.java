package org.mosip.auth.service.integration;

import java.util.Optional;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.constant.RestServicesConstants;
import org.mosip.auth.core.exception.IDDataValidationException;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.exception.RestServiceException;
import org.mosip.auth.core.util.dto.RestRequestDTO;
import org.mosip.auth.service.factory.RestRequestFactory;
import org.mosip.auth.service.helper.RestHelper;
import org.mosip.auth.service.integration.dto.OTPValidateResponseDTO;
import org.mosip.auth.service.integration.dto.OtpGeneratorRequestDto;
import org.mosip.auth.service.integration.dto.OtpGeneratorResponseDto;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

	private MosipLogger logger;

	private OTPValidateResponseDTO otpvalidateresponsedto;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

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
