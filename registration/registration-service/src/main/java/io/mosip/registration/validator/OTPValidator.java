package io.mosip.registration.validator;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

public class OTPValidator extends AuthenticationValidatorImplementation {

	@Autowired
	ServiceDelegateUtil serviceDelegateUtil;

	@Override
	public boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO) {
		return false;/*
		boolean status = false;
		// Create Response to Return to UI layer
		ResponseDTO response = new ResponseDTO();
		SuccessResponseDTO successResponse;
		OtpValidatorResponseDTO otpValidatorResponseDto = null;

		// prepare request params to pass through URI
		Map<String, String> requestParamMap = new HashMap<>();
		requestParamMap.put(RegistrationConstants.USERNAME_KEY, authenticationValidatorDTO.getUserId());
		requestParamMap.put(RegistrationConstants.OTP_GENERATED, authenticationValidatorDTO.getOtp());

		try {
			// Obtain otpValidatorResponseDto from service delegate util
			otpValidatorResponseDto = (OtpValidatorResponseDto) serviceDelegateUtil
					.get(RegistrationConstants.OTP_VALIDATOR_SERVICE_NAME, requestParamMap);
			if (otpValidatorResponseDto != null && otpValidatorResponseDto.getStatus() != null
					&& otpValidatorResponseDto.getStatus().equalsIgnoreCase("true")) {

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
	*/}

}
