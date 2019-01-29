package io.mosip.registration.service;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.DeviceTypes;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dao.UserOnboardDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.template.impl.NotificationServiceImpl;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@Service
public class BaseService {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(NotificationServiceImpl.class);

	/**
	 * serviceDelegateUtil which processes the HTTPRequestDTO requests
	 */
	@Autowired
	protected ServiceDelegateUtil serviceDelegateUtil;

	@Autowired
	private MachineMappingDAO machineMappingDAO;

	@Autowired
	private UserOnboardDAO userOnboardDAO;

	/**
	 * create error response
	 * 
	 * @return ResponseDTO returns the responseDTO after creating appropriate error
	 *         response and mapping to it
	 */
	protected ResponseDTO getErrorResponse(final ResponseDTO response, final String message) {

		/** Create list of Error Response */
		List<ErrorResponseDTO> errorResponses = (response.getErrorResponseDTOs() != null)
				? response.getErrorResponseDTOs()
				: new LinkedList<>();

		/** Error response */
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();

		errorResponse.setCode(RegistrationConstants.ERROR);
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
	 * @return ResponseDTO returns the responseDTO after creating appropriate
	 *         success response and mapping to it
	 */
	public ResponseDTO setSuccessResponse(ResponseDTO responseDTO, String message, Map<String, Object> attributes) {

		/** Success Response */
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
	protected ResponseDTO setErrorResponse(final ResponseDTO response, final String message,
			final Map<String, Object> attributes) {

		/** Create list of Error Response */
		List<ErrorResponseDTO> errorResponses = (response.getErrorResponseDTOs() != null)
				? response.getErrorResponseDTOs()
				: new LinkedList<>();

		/** Error response */
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();

		errorResponse.setCode(RegistrationConstants.ERROR);
		errorResponse.setMessage(message);

		errorResponse.setOtherAttributes(attributes);

		errorResponses.add(errorResponse);

		/** Adding list of error responses to response */
		response.setErrorResponseDTOs(errorResponses);
		return response;

	}

	/**
	 * Get User Id using session context
	 * 
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

	/**
	 * To check the device is valid or not
	 * 
	 * @param deviceType
	 * @param deviceProvider
	 * @param serialNo
	 * @return
	 * @throws ParseException
	 */
	public boolean isValidDevice(DeviceTypes deviceType, String serialNo) {

		LOGGER.debug("REGISTRATION - BASE SERVICE", APPLICATION_NAME, APPLICATION_ID, " isValidDevice Method called");

		return machineMappingDAO.isValidDevice(deviceType, serialNo);
	}

	public boolean isNull(List list) {
		/* Check Whether the list is Null or not */
		return list == null;

	}

	public boolean isEmpty(List list) {
		/* Check Whether the list is empty or not */
		return list.isEmpty();
	}

	public String getStationId(String macAddress) {
		String stationId = null;
		try {
			/* Get Station ID */
			stationId = userOnboardDAO.getStationID(macAddress);
		} catch (RegBaseCheckedException baseCheckedException) {
			LOGGER.error("REGISTRATION_BASE_SERVICE", APPLICATION_NAME, APPLICATION_ID,
					baseCheckedException.getMessage());

		}
		return stationId;
	}

	public String getCenterId() {
		/* Initialize Center Id */
		String centerId = null;

		/* Get Station ID */
		String stationId = getStationId(getMacAddress());

		if (stationId != null) {
			/* Get Center Id */
			centerId = getCenterId(stationId);
		}

		return centerId;
	}

	public String getCenterId(String stationId) {
		String centerId = null;
		if (stationId != null) {
			try {
				/* Get Center ID */
				centerId = userOnboardDAO.getCenterID(stationId);
			} catch (RegBaseCheckedException baseCheckedException) {
				LOGGER.error("REGISTRATION_BASE_SERVICE", APPLICATION_NAME, APPLICATION_ID,
						baseCheckedException.getMessage());

			}
		}
		return centerId;
	}

	public String getMacAddress() {
		/* Get Mac Address */
		return RegistrationSystemPropertiesChecker.getMachineId();

	}

}
