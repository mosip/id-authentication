package org.mosip.auth.service.integration;

import org.mosip.auth.core.constant.RestServicesConstants;
import org.mosip.auth.core.dto.indauth.OtpGeneratorRequestDto;
import org.mosip.auth.core.dto.indauth.OtpGeneratorResponseDto;
import org.mosip.auth.core.exception.IDDataValidationException;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.exception.RestServiceException;
import org.mosip.auth.core.factory.RestRequestFactory;
import org.mosip.auth.core.util.RestUtil;
import org.mosip.auth.core.util.dto.RestRequestDTO;
import org.mosip.auth.service.integration.dto.OTPValidateResponseDTO;
import org.mosip.kernel.core.logging.MosipLogger;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.mosip.kernel.core.logging.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

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
		otpGeneratorRequestDto.setOtpKey(otpKey);
		OtpGeneratorResponseDto otpGeneratorResponsetDto = null;
		RestRequestDTO restRequestDTO = null;

		try {
			restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE,
					otpGeneratorRequestDto, OtpGeneratorResponseDto.class);
		} catch (IDDataValidationException e) {
			LOGGER.error("NA", "NA", e.getErrorCode(), e.getErrorText());
			e.printStackTrace();
		}

		try {
			otpGeneratorResponsetDto = RestUtil.requestSync(restRequestDTO);
		} catch (RestServiceException e) {
			LOGGER.error("NA", "NA", e.getErrorCode(), e.getErrorText());
		}

		String response = otpGeneratorResponsetDto.getOtp();
		if (response == null || response.isEmpty()) {
			LOGGER.error("NA", "NA", "NA", "OTP is null or empty");
		}
		return response;
	}

	public boolean validateOtp(String pinValue, String OtpKey) {
		boolean isValidOtp = false;
		OTPValidateResponseDTO validResponseDto = new OTPValidateResponseDTO();
		try {
			RestRequestDTO restreqdto = restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE,
					validResponseDto, OTPValidateResponseDTO.class);

			validResponseDto = RestUtil.requestSync(restreqdto);
		} catch (RestServiceException | IDDataValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (validResponseDto != null) {
			isValidOtp = true;
		} else {
			isValidOtp = false;
		}
		return isValidOtp;
	}

	public String keyGeneration(String refId) {
		// TODO Auto-generated method stub
		return null;
	}

}
