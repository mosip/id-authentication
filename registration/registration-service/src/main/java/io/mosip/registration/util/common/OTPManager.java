package io.mosip.registration.util.common;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.OtpGeneratorRequestDTO;
import io.mosip.registration.dto.OtpValidatorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.BaseService;


@Component
public class OTPManager extends BaseService{
	
	public ResponseDTO getOTP(final String key) {
		// Create Response to return to UI layer
		ResponseDTO response = new ResponseDTO();
		OtpGeneratorRequestDTO otpGeneratorRequestDto = new OtpGeneratorRequestDTO();
		SuccessResponseDTO successResponse = null;

		// prepare otpGeneratorRequestDto with specified key(EO Username) obtained from
		otpGeneratorRequestDto.setKey(key);

		try {
			// obtain otpGeneratorResponseDto from serviceDelegateUtil
			@SuppressWarnings("unchecked")
			HashMap<String, String> responseMap = (HashMap<String, String>) serviceDelegateUtil
					.post(RegistrationConstants.OTP_GENERATOR_SERVICE_NAME, otpGeneratorRequestDto);
			if (responseMap != null && responseMap.get("otp") != null) {

				// create Success Response
				successResponse = new SuccessResponseDTO();
				successResponse.setCode(RegistrationConstants.ALERT_INFORMATION);
				successResponse.setMessage(
						RegistrationConstants.OTP_GENERATION_SUCCESS_MESSAGE + responseMap.get("otp"));

				response.setSuccessResponseDTO(successResponse);
			} else {
				 // create Error Response
                setErrorResponse(response, RegistrationConstants.OTP_GENERATION_ERROR_MESSAGE,null);
			}

		} catch (RegBaseCheckedException | HttpClientErrorException | HttpServerErrorException | SocketTimeoutException
				| ResourceAccessException exception) {
			// create Error Response
			 setErrorResponse(response, RegistrationConstants.OTP_GENERATION_ERROR_MESSAGE,null);

		} catch(IllegalStateException exception) {
			setErrorResponse(response, RegistrationConstants.CONNECTION_ERROR, null);
			
		}

		return response;

	}
	
	
	
	public boolean validateOTP(String userId, String otp) {

		boolean status = false;
		OtpValidatorResponseDTO otpValidatorResponseDto = null;

		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.USERNAME_KEY, userId);
		requestParamMap.put(RegistrationConstants.OTP_GENERATED, otp);

		try {
			// Obtain otpValidatorResponseDto from service delegate util
			otpValidatorResponseDto = (OtpValidatorResponseDTO) serviceDelegateUtil
					.get(RegistrationConstants.OTP_VALIDATOR_SERVICE_NAME, requestParamMap);
			if (otpValidatorResponseDto != null && otpValidatorResponseDto.getStatus() != null
					&& RegistrationConstants.OTP_VALIDATION_SUCCESS.equals(otpValidatorResponseDto.getStatus())) {

				status = true;

			} else {
				status = false;
			}

		} catch (RegBaseCheckedException | HttpClientErrorException | HttpServerErrorException | SocketTimeoutException
				| ResourceAccessException exception) {
			status = false;
		}

		return status;

	}
}
