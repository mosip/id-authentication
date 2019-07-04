package io.mosip.registration.service;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.DeviceTypes;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dao.UserOnboardDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.template.impl.NotificationServiceImpl;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

/**
 * This is a base class for service package. The common functionality across the 'services' classes are 
 * implemented in this class to inherit this property at the required extended classes. 
 * 
 */
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

	@Autowired
	private GlobalParamService globalParamService;

	/**
	 * create success response.
	 *
	 * @param responseDTO
	 *            the response DTO
	 * @param message
	 *            the message
	 * @param attributes
	 *            the attributes
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
	 * create error response.
	 *
	 * @param response
	 *            the response
	 * @param message
	 *            the message
	 * @param attributes
	 *            the attributes
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
	 * Get User Id using session context.
	 *
	 * @return user id
	 */
	public String getUserIdFromSession() {
		String userId = null;
		if (SessionContext.isSessionContextAvailable()) {
			userId = SessionContext.userId();
			if (userId.equals(RegistrationConstants.AUDIT_DEFAULT_USER)) {
				userId = RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM;
			}
		} else {
			userId = RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM;
		}
		return userId;
	}

	/**
	 * To check the device is valid or not.
	 *
	 * @param deviceType
	 *            the device type
	 * @param serialNo
	 *            the serial no
	 * @return true, if is valid device
	 */
	public boolean isValidDevice(DeviceTypes deviceType, String serialNo) {

		LOGGER.info("REGISTRATION - BASE SERVICE", APPLICATION_NAME, APPLICATION_ID, " isValidDevice Method called");

		return machineMappingDAO.isValidDevice(deviceType, serialNo);
	}

	/**
	 * Checks if is null.
	 *
	 * @param list
	 *            the list
	 * @return true, if is null
	 */
	public boolean isNull(List<?> list) {
		/* Check Whether the list is Null or not */
		return list == null;

	}

	/**
	 * Checks if is empty.
	 *
	 * @param list
	 *            the list
	 * @return true, if is empty
	 */
	public boolean isEmpty(List<?> list) {
		/* Check Whether the list is empty or not */
		return list.isEmpty();
	}

	/**
	 * Gets the station id.
	 *
	 * @param macAddress
	 *            the mac address
	 * @return the station id
	 */
	public String getStationId(String macAddress) {
		String stationId = null;
		try {
			/* Get Station ID */
			stationId = userOnboardDAO.getStationID(macAddress);
		} catch (RegBaseCheckedException baseCheckedException) {
			LOGGER.error("REGISTRATION_BASE_SERVICE", APPLICATION_NAME, APPLICATION_ID,
					baseCheckedException.getMessage() + ExceptionUtils.getStackTrace(baseCheckedException));

		}
		return stationId;
	}

	/**
	 * Gets the center id.
	 *
	 * @return the center id
	 */
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

	/**
	 * Gets the center id.
	 *
	 * @param stationId
	 *            the station id
	 * @return the center id
	 */
	public String getCenterId(String stationId) {
		String centerId = null;
		if (stationId != null) {
			try {
				/* Get Center ID */
				centerId = userOnboardDAO.getCenterID(stationId);
			} catch (RegBaseCheckedException baseCheckedException) {
				LOGGER.error("REGISTRATION_BASE_SERVICE", APPLICATION_NAME, APPLICATION_ID,
						baseCheckedException.getMessage() + ExceptionUtils.getStackTrace(baseCheckedException));

			}
		}
		return centerId;
	}

	/**
	 * Gets the mac address.
	 *
	 * @return the mac address
	 */
	public String getMacAddress() {
		/* Get Mac Address */
		return RegistrationSystemPropertiesChecker.getMachineId();

	}

	/**
	 * Get Global Param configuration value.
	 *
	 * @param key
	 *            the name
	 * @return value
	 */
	public String getGlobalConfigValueOf(String key) {

		ApplicationContext.getInstance();
		// Check application map
		if (ApplicationContext.map().isEmpty() || ApplicationContext.map().get(key)==null) {

			// Load Global params if application map is empty
			ApplicationContext.setApplicationMap(globalParamService.getGlobalParams());
		}

		// Get Value of global param
		return (String) ApplicationContext.map().get(key);
	}

	/**
	 * Conversion of Registration to Packet Status DTO.
	 *
	 * @param registration
	 *            the registration
	 * @return the packet status DTO
	 */
	public PacketStatusDTO packetStatusDtoPreperation(Registration registration) {
		PacketStatusDTO statusDTO = new PacketStatusDTO();
		statusDTO.setFileName(registration.getId());
		statusDTO.setPacketClientStatus(registration.getClientStatusCode());
		statusDTO.setPacketPath(registration.getAckFilename());
		statusDTO.setPacketServerStatus(registration.getServerStatusCode());
		statusDTO.setUploadStatus(registration.getFileUploadStatus());
		statusDTO.setPacketStatus(registration.getStatusCode());
		statusDTO.setSupervisorStatus(registration.getClientStatusCode());
		statusDTO.setSupervisorComments(registration.getClientStatusComments());

		try (FileInputStream fis = new FileInputStream(FileUtils.getFile(registration.getAckFilename().replace(
				RegistrationConstants.ACKNOWLEDGEMENT_FILE_EXTENSION, RegistrationConstants.ZIP_FILE_EXTENSION)))) {
			byte[] byteArray = new byte[(int) fis.available()];
			fis.read(byteArray);
			byte[] packetHash = HMACUtils.generateHash(byteArray);
			statusDTO.setPacketHash(HMACUtils.digestAsPlainText(packetHash));
			statusDTO.setPacketSize(BigInteger.valueOf(byteArray.length));

		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION_BASE_SERVICE", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
		}

		return statusDTO;
	}

	/*
	 * public static void setBaseGlobalMap(Map<String, Object> map) { applicationMap
	 * = map; }
	 * 
	 * public static Map<String, Object> getBaseGlobalMap() { return applicationMap;
	 * }
	 */

	/**
	 * Registration date conversion.
	 *
	 * @param timestamp
	 *            the timestamp
	 * @return the string
	 */
	protected String regDateConversion(Timestamp timestamp) {

		DateFormat dateFormat = new SimpleDateFormat(RegistrationConstants.EOD_PROCESS_DATE_FORMAT);
		Date date = new Date(timestamp.getTime());
		return dateFormat.format(date);
	}

}
