package io.mosip.registration.validator;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.OtpValidatorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@Component("otpValidator")
public class OTPValidator extends AuthenticationValidatorImplementation {

	@Autowired
	ServiceDelegateUtil serviceDelegateUtil;

	@Override
	public boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO) {
		boolean status = false;
		// Create Response to Return to UI layer
		ResponseDTO response = new ResponseDTO();
		SuccessResponseDTO successResponse;
		OtpValidatorResponseDTO otpValidatorResponseDto = null;

		// prepare request params to pass through URI
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.USERNAME_KEY, authenticationValidatorDTO.getUserId());
		requestParamMap.put(RegistrationConstants.OTP_GENERATED, authenticationValidatorDTO.getOtp());

		try {
			// Obtain otpValidatorResponseDto from service delegate util
			otpValidatorResponseDto = (OtpValidatorResponseDTO) serviceDelegateUtil
					.get(RegistrationConstants.OTP_VALIDATOR_SERVICE_NAME, requestParamMap);
			if (otpValidatorResponseDto != null && otpValidatorResponseDto.getStatus() != null
					&& otpValidatorResponseDto.getStatus().equalsIgnoreCase("success")) {

				// Create Success Response
				successResponse = new SuccessResponseDTO();
				successResponse.setCode(RegistrationConstants.ALERT_INFORMATION);
				successResponse.setMessage(RegistrationConstants.OTP_VALIDATION_SUCCESS_MESSAGE);
				response.setSuccessResponseDTO(successResponse);
				status = true;

			} else {
				status = false;
			}

		} catch (RegBaseCheckedException | HttpClientErrorException | HttpServerErrorException | SocketTimeoutException
				| ResourceAccessException exception) {
			status=false;

		}

		return status;
	}

}
