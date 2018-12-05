package io.mosip.registration.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

public class BaseService {

	/**
	 * serviceDelegateUtil which processes the HTTPRequestDTO requests
	 */
	@Autowired
	protected ServiceDelegateUtil serviceDelegateUtil;

	/**
	 * create error response
	 * 
	 * @return ResponseDTO returns the responseDTO after creating appropriate error
	 *         response and mapping to it
	 */
	protected ResponseDTO getErrorResponse(final ResponseDTO response, final String message) {

		/** Create list of Error Response */
		List<ErrorResponseDTO> errorResponses = (response.getErrorResponseDTOs() != null) ? response.getErrorResponseDTOs() : new LinkedList<>();

		/** Error response */
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();

		errorResponse.setCode(RegistrationConstants.ALERT_ERROR);
		errorResponse.setMessage(message);

		Map<String, Object> otherAttributes = new HashMap<>();
		otherAttributes.put("registration", null);

		errorResponses.add(errorResponse);

		/** Adding list of error responses to response */
		response.setErrorResponseDTOs(errorResponses);
		return response;

	}
	
	/**
	 * create success response
	 * 
	 * @return ResponseDTO returns the responseDTO after creating appropriate success
	 *         response and mapping to it
	 */
	public ResponseDTO setSuccessResponse(ResponseDTO responseDTO, String message, Map<String, Object> attributes) {

		/**Success Response */
		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
		
		successResponseDTO.setMessage(message);
		successResponseDTO.setCode(RegistrationConstants.ALERT_INFORMATION);

		/** Adding attributes to success response */
		successResponseDTO.setOtherAttributes(attributes);
		
		responseDTO.setSuccessResponseDTO(successResponseDTO);
		return responseDTO;
	}
	
	/**
	 * create error response
	 * 
	 * @return ResponseDTO returns the responseDTO after creating appropriate error
	 *         response and mapping to it
	 */
	protected ResponseDTO setErrorResponse(final ResponseDTO response, final String message, final Map<String,Object> attributes) {

		/** Create list of Error Response */
		List<ErrorResponseDTO> errorResponses = (response.getErrorResponseDTOs() != null) ? response.getErrorResponseDTOs() : new LinkedList<>();

		/** Error response */
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();

		errorResponse.setCode(RegistrationConstants.ALERT_ERROR);
		errorResponse.setMessage(message);

		errorResponse.setOtherAttributes(attributes);

		errorResponses.add(errorResponse);

		/** Adding list of error responses to response */
		response.setErrorResponseDTOs(errorResponses);
		return response;

	}

	/**
	 * Get User Id using session context
	 * @return user id
	 */
	protected String getUserIdFromSession() {

		String userId = null;
		UserContext userContext = SessionContext.getInstance().getUserContext();
		if (userContext != null) {
			userId = userContext.getUserId();
		}
		return userId;
	}

	

	

}
