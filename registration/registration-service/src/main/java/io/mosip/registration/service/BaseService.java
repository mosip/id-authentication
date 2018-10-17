package io.mosip.registration.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

public class BaseService {

	/**
	 * serviceDelegateUtil which processes the HTTPRequestDTO requests
	 */
	@Autowired
	protected ServiceDelegateUtil serviceDelegateUtil;

	/** Object for Logger. */
	protected static MosipLogger LOGGER;

	/**
	 * create error response
	 * 
	 * @return ResponseDTO returns the responseDTO after creating appropriate error
	 *         response and mapping to it
	 */
	protected ResponseDTO getErrorResponse(ResponseDTO response, final String message) {
		/** Create list of Error Response */
		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		/** Error response */
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();

		errorResponse.setCode(RegConstants.ALERT_ERROR);
		errorResponse.setMessage(message);
		
		Map<String, Object> otherAttributes = new HashMap<>();
		otherAttributes.put("registration", null);

		errorResponses.add(errorResponse);

		/** Adding list of error responses to response */
		response.setErrorResponseDTOs(errorResponses);
		return response;

	}

}
