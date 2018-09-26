package org.mosip.auth.service.integration;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.constant.RestServicesConstants;
import org.mosip.auth.core.exception.IDDataValidationException;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.exception.RestServiceException;
import org.mosip.auth.core.util.dto.RestRequestDTO;
import org.mosip.auth.service.factory.RestRequestFactory;
import org.mosip.auth.service.integration.dto.OTPValidateResponseDTO;
import org.mosip.auth.service.integration.dto.OtpGeneratorRequestDto;
import org.mosip.auth.service.integration.dto.OtpGeneratorResponseDto;
import org.mosip.auth.service.util.RestUtil;
import org.mosip.kernel.core.logging.MosipLogger;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.mosip.kernel.core.logging.factory.MosipLogfactory;
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
	RestRequestFactory restRequestFactory;

	private MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
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
	public String generateOTP(String otpKey) {

		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey(otpKey);
		OtpGeneratorResponseDto otpGeneratorResponsetDto = null;
		RestRequestDTO restRequestDTO = null;

		try {
			restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE,
					otpGeneratorRequestDto, OtpGeneratorResponseDto.class);
		} catch (IDDataValidationException e) {
			LOGGER.error("NA", "NA", e.getErrorCode(), e.getErrorText());
			e.printStackTrace();
		}

		String response = null;

		try {
			otpGeneratorResponsetDto = RestUtil.requestSync(restRequestDTO);
			System.out.println(otpGeneratorResponsetDto);
			response = otpGeneratorResponsetDto.getOtp();
			LOGGER.info("NA", "NA", "NA", "otpGeneratorResponsetDto " + response);
			if (response == null || response.isEmpty()) {
				LOGGER.error("NA", "NA", "NA", "OTP is null or empty");
			} else {
				LOGGER.error("NA", "NA", "NA", response);
			}

		} catch (RestServiceException e) {
			LOGGER.error("NA", "NA", e.getErrorCode(), e.getErrorText());
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
		OTPValidateResponseDTO validResponseDto = new OTPValidateResponseDTO();
		try {
			System.err.println("restRequestFactory  " + restRequestFactory);
			RestRequestDTO restreqdto = restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE,
					null, OTPValidateResponseDTO.class);
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("key", otpKey);
			params.add("otp", pinValue);
			restreqdto.setParams(params);

			System.err.println(restreqdto);
			validResponseDto = RestUtil.requestSync(restreqdto);
			if (validResponseDto.getStatus().equalsIgnoreCase("true")) {
				isValidOtp = true;
				LOGGER.info("validateOtp", "Inside Validate OTP", "NA", "NA");
			}
		} catch (RestServiceException | IDDataValidationException e) {
			LOGGER.error("NA", "NA", e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.ID_OTPVALIDATION_REQUEST_FAILED,
					e);
		}

		return isValidOtp;
	}

}
