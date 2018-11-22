package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;

import static io.mosip.registration.constants.RegistrationConstants.REGISTRATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.MACHINE_MAPPING_ENTITY_ERROR_NO_RECORDS;
import static io.mosip.registration.constants.RegistrationConstants.MACHINE_MAPPING_ENTITY_SUCCESS_MESSAGE;
import static io.mosip.registration.constants.RegistrationConstants.MACHINE_MAPPING_LOGGER_TITLE;
import static io.mosip.registration.constants.RegistrationConstants.DEVICE_MAPPING_LOGGER_TITLE;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dto.DeviceDTO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.UserMachineMappingDTO;
import io.mosip.registration.entity.DeviceType;
import io.mosip.registration.entity.RegCenterDevice;
import io.mosip.registration.entity.RegCentreMachineDevice;
import io.mosip.registration.entity.RegCentreMachineDeviceId;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.entity.UserMachineMappingID;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.MapMachineService;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

/**
 * User Client Machine Mapping Service
 * 
 * @author Yaswanth S
 * @since 1.0.0
 *
 */
@Service
public class MapMachineServiceImpl implements MapMachineService {
	/**
	 * instance of {@code instance of AuditFactory}
	 */
	@Autowired
	AuditFactory auditFactory;

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(MapMachineServiceImpl.class);

	@Autowired
	private MachineMappingDAO machineMappingDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.MapMachineService#saveOrUpdate(io.mosip.
	 * registration.dto.UserMachineMappingDTO)
	 */
	@Override
	public ResponseDTO saveOrUpdate(UserMachineMappingDTO userMachineMappingDTO) {
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"Service saveOrUpdate method called");

		// find user
		UserMachineMappingID userID = new UserMachineMappingID();
		userID.setUserID(userMachineMappingDTO.getUserID());
		userID.setCentreID(userMachineMappingDTO.getCentreID());
		userID.setMachineID(userMachineMappingDTO.getStationID());

		boolean isActive = userMachineMappingDTO.getStatus()
				.equalsIgnoreCase(RegistrationConstants.MACHINE_MAPPING_ACTIVE);

		/* create response */
		ResponseDTO responseDTO = new ResponseDTO();

		/* Interacting with DAO layer */

		try {
			/* find user */
			UserMachineMapping user = machineMappingDAO.findByID(userID);

			if (user != null) {
				/* if user already exists */
				user.setUpdBy(SessionContext.getInstance().getUserContext().getUserId());
				user.setUpdDtimes(new Timestamp(System.currentTimeMillis()));
				user.setIsActive(isActive);

				user.setIsActive(isActive);
				machineMappingDAO.update(user);
			} else {
				/* if user didn't exists */
				user = new UserMachineMapping();
				user.setUserMachineMappingId(userID);
				user.setIsActive(isActive);
				user.setCrBy(SessionContext.getInstance().getUserContext().getUserId());
				user.setCrDtime(new Timestamp(System.currentTimeMillis()));
				user.setUpdBy(SessionContext.getInstance().getUserContext().getUserId());
				user.setUpdDtimes(new Timestamp(System.currentTimeMillis()));

				machineMappingDAO.save(user);
			}
			/* create success response */
			SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
			successResponseDTO.setCode(RegistrationConstants.MACHINE_MAPPING_CODE);
			successResponseDTO.setInfoType(RegistrationConstants.ALERT_INFORMATION);
			successResponseDTO.setMessage(RegistrationConstants.MACHINE_MAPPING_SUCCESS_MESSAGE);
			responseDTO.setSuccessResponseDTO(successResponseDTO);
			LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID, "Success Response created");
		} catch (RegBaseUncheckedException exception) {
			responseDTO = getErrorResponse(responseDTO, RegistrationConstants.MACHINE_MAPPING_ERROR_MESSAGE);
			LOGGER.error(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID, "Error Response created");

		}
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"Service saveOrUpdate method ended");

		return responseDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.MapMachineService#view()
	 */
	@Override
	public ResponseDTO view() {
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID, "View Method called");

		ResponseDTO responseDTO = new ResponseDTO();

		try {
			/* get mac address */
			String machineID = RegistrationSystemPropertiesChecker.getMachineId();
			/* get station ID */
			String stationID = machineMappingDAO.getStationID(machineID);
			/* get center id */
			String centerID = machineMappingDAO.getCenterID(stationID);
			/* get user list */
			List<RegistrationUserDetail> registrationUserDetails = machineMappingDAO.getUsers(centerID);

			if (registrationUserDetails != null && !registrationUserDetails.isEmpty()) {
				/* create success response */
				SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
				successResponseDTO.setCode(RegistrationConstants.MACHINE_MAPPING_CODE);
				successResponseDTO.setMessage(MACHINE_MAPPING_ENTITY_SUCCESS_MESSAGE);
				successResponseDTO
						.setOtherAttributes(constructDTOs(machineID, stationID, centerID, registrationUserDetails));

				responseDTO.setSuccessResponseDTO(successResponseDTO);
				LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
						"View Method Success Response created");
			} else {
				getErrorResponse(responseDTO, MACHINE_MAPPING_ENTITY_ERROR_NO_RECORDS);
			}
		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			responseDTO = getErrorResponse(responseDTO, regBaseUncheckedException.getMessage());
			LOGGER.error(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
					"View() Method Error " + regBaseUncheckedException.getMessage());
		} catch (RegBaseCheckedException regBaseCheckedException) {
			responseDTO = getErrorResponse(responseDTO, regBaseCheckedException.getMessage());
			LOGGER.error(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
					"Exception Method Response created" + regBaseCheckedException.getMessage());
		}

		return responseDTO;
	}

	/**
	 * To prepare List of {@link UserMachineMappingDTO}
	 * 
	 * @param machineID
	 * @param stationID
	 * @param centreID
	 * @param registrationUserDetails
	 * @return
	 */
	private Map<String, Object> constructDTOs(String machineID, String stationID, String centreID,
			List<RegistrationUserDetail> registrationUserDetails) {
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID, "constructDTOs() method called");
		Map<String, Object> userDetailMap = new HashMap<>();
		try {
			List<UserMachineMappingDTO> userMachineMappingDTOs = registrationUserDetails.stream()
					.map(registrationUserDetail -> {
						UserMachineMappingDTO userMachineMappingDTO = null;
						if (registrationUserDetail != null) {
							userMachineMappingDTO = constructDTO(registrationUserDetail, machineID, stationID,
									centreID);
						}
						return userMachineMappingDTO;
					}).collect(Collectors.toList());
			userDetailMap.put(RegistrationConstants.USER_MACHINE_MAPID, userMachineMappingDTOs);
		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
					"Exception in preparing DTO " + regBaseUncheckedException.getMessage());
		}
		return userDetailMap;
	}

	/**
	 * To prepare the {@link UserMachineMappingDTO}
	 * 
	 * @param machineID
	 * @param stationID
	 * @param centreID
	 * @param registrationUserDetails
	 * @return
	 */
	private UserMachineMappingDTO constructDTO(RegistrationUserDetail registrationUserDetail, String machineID,
			String stationID, String centreID) {
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID, "constructDTO() method called");
		String userID = registrationUserDetail.getId();
		String userName = registrationUserDetail.getName();
		StringBuilder role = new StringBuilder();
		String roleCode = "";
		String status = RegistrationConstants.USER_IN_ACTIVE;

		if (!registrationUserDetail.getUserRole().isEmpty()) {
			/* List of roles with comma separated */
			registrationUserDetail.getUserRole().forEach(registrationUserRole -> role
					.append(registrationUserRole.getRegistrationUserRoleID().getRoleCode() + ","));
			if (role.length() > 0) {
				roleCode = role.substring(0, role.lastIndexOf(","));
			}
		}
		if (!registrationUserDetail.getUserMachineMapping().isEmpty()) {
			for (UserMachineMapping userMachineMapping : registrationUserDetail.getUserMachineMapping()) {
				if (userMachineMapping.getUserMachineMappingId().getMachineID().equals(stationID)) {
					status = userMachineMapping.getIsActive() ? RegistrationConstants.USER_ACTIVE
							: RegistrationConstants.USER_IN_ACTIVE;
				}
			}
		}

		return new UserMachineMappingDTO(userID, userName, roleCode, status, centreID, stationID, machineID);
	}

	/**
	 * Common method to prepare error response
	 * 
	 * @param response
	 * @param message
	 * @return
	 */
	private ResponseDTO getErrorResponse(ResponseDTO response, final String message) {
		/* Create list of Error Response */
		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		/* Error response */
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(RegistrationConstants.MACHINE_MAPPING_CODE);
		errorResponse.setInfoType(RegistrationConstants.ALERT_ERROR);
		errorResponse.setMessage(message);

		errorResponses.add(errorResponse);

		/* Assing list of error responses to response */
		response.setErrorResponseDTOs(errorResponses);
		return response;

	}

	/**
	 * Get all active device names
	 * 
	 * @return list of device names
	 */
	@Override
	public List<String> getAllDeviceTypes() {
		LOGGER.debug(DEVICE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"getAllDeviceTypes() method is started");

		List<String> list = new ArrayList<>();

		for (DeviceType deviceType : machineMappingDAO.getAllDeviceTypes()) {
			list.add(deviceType.getRegDeviceTypeId().getCode());
		}
		LOGGER.debug(DEVICE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"getAllDeviceTypes() method is ended");

		return list;

	}

	/**
	 * it fetches the list of devices mapped to the Machine and the list of devices
	 * available to the Registration Center (already mapped devices to the requested
	 * machine will be excluded)
	 * 
	 * @param centerID
	 *            primary key of Regcenter table
	 * @param machineId
	 *            primary key of machinemaster table
	 * @return list of {@link DeviceDTO}
	 * 
	 */
	@Override
	public Map<String, List<DeviceDTO>> getDeviceMappingList(String centerId, String machineId) {
		LOGGER.debug(DEVICE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"getDeviceMappingList(String,String) method is strarted");
		Map<String, List<DeviceDTO>> map = null;
		map = new HashMap<>();
		try {
			List<DeviceDTO> availableDeviceDtoList = null;
			List<DeviceDTO> mappedDeviceDtoList = null;
			List<RegCentreMachineDevice> mappedList = null;
			List<RegCenterDevice> availableDevicesList = null;

			availableDeviceDtoList = new ArrayList<>();
			mappedDeviceDtoList = new ArrayList<>();
			mappedList = machineMappingDAO.getAllMappedDevices(centerId, machineId);

			for (RegCentreMachineDevice regCenterMachineDevice : mappedList) {
				DeviceDTO mappedDeviceDto = new DeviceDTO();
				mappedDeviceDto.setSerialNo(regCenterMachineDevice.getRegDeviceMaster().getSerialNumber());
				mappedDeviceDto
						.setManufacturerName(regCenterMachineDevice.getRegDeviceMaster().getRegDeviceSpec().getBrand());
				mappedDeviceDto.setModelName(regCenterMachineDevice.getRegDeviceMaster().getRegDeviceSpec().getModel());
				mappedDeviceDto.setDeviceType(regCenterMachineDevice.getRegDeviceMaster().getRegDeviceSpec()
						.getRegDeviceType().getRegDeviceTypeId().getCode());
				mappedDeviceDto.setRegCenterId(regCenterMachineDevice.getRegCentreMachineDeviceId().getRegCentreId());
				mappedDeviceDto.setDeviceId(regCenterMachineDevice.getRegCentreMachineDeviceId().getDeviceId());
				mappedDeviceDto.setMachineId(regCenterMachineDevice.getRegCentreMachineDeviceId().getMachineId());

				mappedDeviceDtoList.add(mappedDeviceDto);
			}

			availableDevicesList = machineMappingDAO.getAllDeviceBasedOnCenterId(centerId);
			for (RegCenterDevice regCenterDevice : availableDevicesList) {
				DeviceDTO availableDeviceDto = new DeviceDTO();
				availableDeviceDto.setSerialNo(regCenterDevice.getRegDeviceMaster().getSerialNumber());
				availableDeviceDto
						.setManufacturerName(regCenterDevice.getRegDeviceMaster().getRegDeviceSpec().getBrand());
				availableDeviceDto.setModelName(regCenterDevice.getRegDeviceMaster().getRegDeviceSpec().getModel());
				availableDeviceDto.setDeviceType(regCenterDevice.getRegDeviceMaster().getRegDeviceSpec()
						.getRegDeviceType().getRegDeviceTypeId().getCode());
				availableDeviceDto.setRegCenterId(regCenterDevice.getRegCenterDeviceId().getRegCenterId());
				availableDeviceDto.setDeviceId(regCenterDevice.getRegCenterDeviceId().getDeviceId());

				availableDeviceDtoList.add(availableDeviceDto);
			}
			if (!mappedDeviceDtoList.isEmpty()) {
				availableDeviceDtoList.removeAll(mappedDeviceDtoList);
			}

			map.put(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES, availableDeviceDtoList);
			map.put(RegistrationConstants.ONBOARD_MAPPED_DEVICES, mappedDeviceDtoList);

			LOGGER.debug(DEVICE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
					"getDeviceMappingList(String,String) method is ended");

		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error(DEVICE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
					regBaseUncheckedException.getMessage());
		}

		return map;

	}

	/**
	 * it deletes the un-mapped devices and saves the newly mapped devices
	 * 
	 * 
	 * @param deletedList
	 *            for un mapping the devices
	 * 
	 * @param addedLIst
	 *            for saving the mapped devices
	 * 
	 * 
	 */
	@Override
	public ResponseDTO updateMappedDevice(List<DeviceDTO> deletedList, List<DeviceDTO> addedList) {
		LOGGER.debug(DEVICE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"getDeviceMappedDevice(List,List) method is strarted");
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			for (DeviceDTO unMappedDeviceDTO : deletedList) {
				RegCentreMachineDeviceId regCentreMachineDeviceId = new RegCentreMachineDeviceId();
				regCentreMachineDeviceId.setRegCentreId(unMappedDeviceDTO.getRegCenterId());
				regCentreMachineDeviceId.setMachineId(unMappedDeviceDTO.getMachineId());
				regCentreMachineDeviceId.setDeviceId(unMappedDeviceDTO.getDeviceId());
				machineMappingDAO.deleteUnMappedDevice(regCentreMachineDeviceId);
			}
			for (DeviceDTO mappedDeviceDTO : addedList) {
				RegCentreMachineDevice regCentreMachineDevice = new RegCentreMachineDevice();
				RegCentreMachineDeviceId regCentreMachineDeviceId = new RegCentreMachineDeviceId();
				regCentreMachineDeviceId.setRegCentreId(mappedDeviceDTO.getRegCenterId());
				regCentreMachineDeviceId.setMachineId(mappedDeviceDTO.getMachineId());
				regCentreMachineDeviceId.setDeviceId(mappedDeviceDTO.getDeviceId());
				regCentreMachineDevice.setRegCentreMachineDeviceId(regCentreMachineDeviceId);
				regCentreMachineDevice.setIsActive(true);
				regCentreMachineDevice.setCrBy(SessionContext.getInstance().getUserContext().getUserId());
				regCentreMachineDevice.setCrDtime(new Timestamp(new Date().getTime()));
				machineMappingDAO.addedMappedDevice(regCentreMachineDevice);
			}

			auditFactory.audit(AuditEvent.DEVICE_MAPPING_SUCCESS, AppModule.DEVICE_MAPPING,
					"Device mapped successfully", REGISTRATION_ID, "refIdType");
			SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
			successResponseDTO.setCode(RegistrationConstants.DEVICE_MAPPING_SUCCESS_CODE);
			successResponseDTO.setInfoType(RegistrationConstants.ALERT_INFORMATION);
			successResponseDTO.setMessage(RegistrationConstants.DEVICE_MAPPING_SUCCESS_MESSAGE);
			responseDTO.setSuccessResponseDTO(successResponseDTO);
			LOGGER.debug(DEVICE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
					"getDeviceMappedDevice(List,List) method is ended");
		} catch (RegBaseUncheckedException regBaseUncheckedException) {

			responseDTO = buildErrorRespone(responseDTO, RegistrationConstants.DEVICE_MAPPING_ERROR_MESSAGE);
			LOGGER.error(DEVICE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID, "Error Response created");

		}
		return responseDTO;
	}

	private ResponseDTO buildErrorRespone(ResponseDTO response, final String message) {
		/* Create list of Error Response */
		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		/* Error response */
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(RegistrationConstants.DEVICE_MAPPING_ERROR_CODE);
		errorResponse.setInfoType(RegistrationConstants.ALERT_ERROR);
		errorResponse.setMessage(message);

		errorResponses.add(errorResponse);

		/* Adding list of error responses to response */
		response.setErrorResponseDTOs(errorResponses);
		return response;

	}

}
