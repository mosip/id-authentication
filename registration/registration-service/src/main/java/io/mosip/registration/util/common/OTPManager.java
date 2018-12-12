package io.mosip.registration.util.common;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.OtpGeneratorRequestDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@Component
public class OTPGenerator {
	
	@Autowired
	ServiceDelegateUtil serviceDelegateUtil;

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
                getErrorResponse(response, RegistrationConstants.OTP_GENERATION_ERROR_MESSAGE);
			}

		} catch (RegBaseCheckedException | HttpClientErrorException | HttpServerErrorException | SocketTimeoutException
				| ResourceAccessException exception) {
			// create Error Response
			 getErrorResponse(response, RegistrationConstants.OTP_GENERATION_ERROR_MESSAGE);

		}

		return response;

	}
	
	private static ResponseDTO getErrorResponse(ResponseDTO response, final String message) {
		// Create list of Error Response
		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<ErrorResponseDTO>();

		// Error response
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();

		errorResponse.setCode(RegistrationConstants.ALERT_ERROR);
		errorResponse.setMessage(message);
		errorResponses.add(errorResponse);

		// Assing list of error responses to response
		response.setErrorResponseDTOs(errorResponses);
		return response;

	}
}
