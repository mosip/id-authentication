package io.mosip.registration.service.mapping.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.DEVICE_MAPPING_LOGGER_TITLE;
import static io.mosip.registration.constants.RegistrationConstants.MACHINE_MAPPING_ENTITY_ERROR_NO_RECORDS;
import static io.mosip.registration.constants.RegistrationConstants.MACHINE_MAPPING_ENTITY_SUCCESS_MESSAGE;
import static io.mosip.registration.constants.RegistrationConstants.MACHINE_MAPPING_LOGGER_TITLE;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dto.DeviceDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.UserMachineMappingDTO;
import io.mosip.registration.entity.RegCenterDevice;
import io.mosip.registration.entity.RegCentreMachineDevice;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.entity.id.RegCentreMachineDeviceId;
import io.mosip.registration.entity.id.UserMachineMappingID;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.mapping.MapMachineService;

/**
 * This service implementation updates the mapping of users and devices to the
 * Registration Center Machine
 * 
 * @author YASWANTH S
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Service
public class MapMachineServiceImpl extends BaseService implements MapMachineService {
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
		LOGGER.info(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
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
				user.setUpdBy(getUserIdFromSession());
				user.setUpdDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
				user.setIsActive(isActive);

				user.setIsActive(isActive);
				machineMappingDAO.update(user);
			} else {
				/* if user didn't exists */
				user = new UserMachineMapping();
				user.setUserMachineMappingId(userID);
				user.setIsActive(isActive);
				user.setCrBy(getUserIdFromSession());
				user.setCrDtime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
				user.setUpdBy(getUserIdFromSession());
				user.setUpdDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));

				machineMappingDAO.save(user);
			}
			/* create success response */
			setSuccessResponse(responseDTO, RegistrationConstants.MACHINE_MAPPING_SUCCESS_MESSAGE, null);

			LOGGER.info(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID, "Success Response created");
		} catch (RegBaseUncheckedException exception) {
			LOGGER.error(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID, "Error Response created" + ExceptionUtils.getStackTrace(exception));

			setErrorResponse(responseDTO, RegistrationConstants.MACHINE_MAPPING_ERROR_MESSAGE, null);
		}
		LOGGER.info(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
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
		LOGGER.info(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID, "View Method called");

		ResponseDTO responseDTO = new ResponseDTO();

		try {

			/* get center id */
			String centerID = getCenterId();
			/* get user list */
			List<UserDetail> userDetails = machineMappingDAO.getUsers(centerID);

			if (userDetails != null && !userDetails.isEmpty()) {
				/* create success response */
				setSuccessResponse(responseDTO, MACHINE_MAPPING_ENTITY_SUCCESS_MESSAGE,
						constructDTOs(getMacAddress(), getStationId(getMacAddress()), centerID, userDetails));
				LOGGER.info(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
						"View Method Success Response created");
			} else {
				setErrorResponse(responseDTO, MACHINE_MAPPING_ENTITY_ERROR_NO_RECORDS, null);
			}
		} catch (RegBaseUncheckedException | RegBaseCheckedException exception) {
			LOGGER.error(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
					"View Method Error " + exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			setErrorResponse(responseDTO, RegistrationConstants.DEVICE_MAPPING_ERROR_MESSAGE, null);
		}

		return responseDTO;
	}

	/**
	 * To prepare List of {@link UserMachineMappingDTO}
	 * 
	 * @param machineID
	 * @param stationID
	 * @param centreID
	 * @param userDetails
	 * @return
	 */
	private Map<String, Object> constructDTOs(String machineID, String stationID, String centreID,
			List<UserDetail> userDetails) {
		LOGGER.info(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID, "constructDTOs() method called");
		Map<String, Object> userDetailMap = new WeakHashMap<>();
		try {
			List<UserMachineMappingDTO> userMachineMappingDTOs = userDetails.stream().map(registrationUserDetail -> {
				UserMachineMappingDTO userMachineMappingDTO = null;
				if (registrationUserDetail != null) {
					userMachineMappingDTO = constructDTO(registrationUserDetail, machineID, stationID, centreID);
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
	private UserMachineMappingDTO constructDTO(UserDetail userDetail, String machineID, String stationID,
			String centreID) {
		LOGGER.info(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID, "constructDTO() method called");
		String userID = userDetail.getId();
		String userName = userDetail.getName();
		StringBuilder role = new StringBuilder();
		String roleCode = "";
		String status = RegistrationConstants.USER_IN_ACTIVE;

		if (!userDetail.getUserRole().isEmpty()) {
			/* List of roles with comma separated */
			userDetail.getUserRole().forEach(
					registrationUserRole -> role.append(registrationUserRole.getUserRoleID().getRoleCode() + ","));
			if (role.length() > 0) {
				roleCode = role.substring(0, role.lastIndexOf(","));
			}
		}
		if (!userDetail.getUserMachineMapping().isEmpty()) {
			for (UserMachineMapping userMachineMapping : userDetail.getUserMachineMapping()) {
				if (userMachineMapping.getUserMachineMappingId().getMachineID().equals(stationID)) {
					status = userMachineMapping.getIsActive() ? RegistrationConstants.USER_ACTIVE
							: RegistrationConstants.USER_IN_ACTIVE;
				}
			}
		}

		return new UserMachineMappingDTO(userID, userName, roleCode, status, centreID, stationID, machineID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.MapMachineService#getAllDeviceTypes()
	 */
	@Override
	public List<String> getAllDeviceTypes() {
		LOGGER.info(DEVICE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"getAllDeviceTypes() method is started");

		List<String> list = new ArrayList<>();

		try {
			machineMappingDAO.getAllDeviceTypes()
					.forEach(deviceType -> list.add(deviceType.getRegDeviceTypeId().getCode()));

			LOGGER.info(DEVICE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
					"getAllDeviceTypes() method is ended");
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.FETCH_DEVICE_TYPES_EXCEPTION,
					runtimeException.getMessage());
		}

		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.MapMachineService#getDeviceMappingList(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public Map<String, List<DeviceDTO>> getDeviceMappingList(String centerId, String machineId) {
		LOGGER.info(DEVICE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"getDeviceMappingList(String,String) method is strarted");

		Map<String, List<DeviceDTO>> map = new WeakHashMap<>();

		try {
			List<DeviceDTO> availableDeviceDTOs = new ArrayList<>();
			List<DeviceDTO> mappedDeviceDTOs = new ArrayList<>();

			// Get the devices that are mapped to the given machine
			List<RegCentreMachineDevice> devicesMapped = machineMappingDAO.getAllMappedDevices(centerId, machineId);

			for (RegCentreMachineDevice regCenterMachineDevice : devicesMapped) {
				DeviceDTO deviceDTO = new DeviceDTO();
				deviceDTO.setSerialNo(regCenterMachineDevice.getRegDeviceMaster().getSerialNum());
				deviceDTO
						.setManufacturerName(regCenterMachineDevice.getRegDeviceMaster().getRegDeviceSpec().getBrand());
				deviceDTO.setModelName(regCenterMachineDevice.getRegDeviceMaster().getRegDeviceSpec().getModel());
				deviceDTO.setDeviceType(regCenterMachineDevice.getRegDeviceMaster().getRegDeviceSpec()
						.getRegDeviceType().getRegDeviceTypeId().getCode());
				deviceDTO.setRegCenterId(regCenterMachineDevice.getRegCentreMachineDeviceId().getRegCentreId());
				deviceDTO.setDeviceId(regCenterMachineDevice.getRegCentreMachineDeviceId().getDeviceId());
				deviceDTO.setMachineId(regCenterMachineDevice.getRegCentreMachineDeviceId().getMachineId());

				mappedDeviceDTOs.add(deviceDTO);
			}

			// Get the devices (available devices) that are mapped to the given registration
			// center
			List<RegCenterDevice> availableDevices = machineMappingDAO.getAllValidDevicesByCenterId(centerId);
			for (RegCenterDevice regCenterDevice : availableDevices) {
				DeviceDTO deviceDTO = new DeviceDTO();
				deviceDTO.setSerialNo(regCenterDevice.getRegDeviceMaster().getSerialNum());
				deviceDTO.setManufacturerName(regCenterDevice.getRegDeviceMaster().getRegDeviceSpec().getBrand());
				deviceDTO.setModelName(regCenterDevice.getRegDeviceMaster().getRegDeviceSpec().getModel());
				deviceDTO.setDeviceType(regCenterDevice.getRegDeviceMaster().getRegDeviceSpec().getRegDeviceType()
						.getRegDeviceTypeId().getCode());
				deviceDTO.setRegCenterId(regCenterDevice.getRegCenterDeviceId().getRegCenterId());
				deviceDTO.setDeviceId(regCenterDevice.getRegCenterDeviceId().getDeviceId());

				availableDeviceDTOs.add(deviceDTO);
			}

			// Remove all the devices that are mapped to the given machine from the
			// available devices
			if (!mappedDeviceDTOs.isEmpty()) {
				availableDeviceDTOs.removeAll(mappedDeviceDTOs);
			}

			// Add the available devices and mapped devices to the map
			map.put(RegistrationConstants.ONBOARD_AVAILABLE_DEVICES, availableDeviceDTOs);
			map.put(RegistrationConstants.ONBOARD_MAPPED_DEVICES, mappedDeviceDTOs);

			LOGGER.info(DEVICE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
					"getDeviceMappingList(String,String) method is ended");
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.FETCH_DEVICE_MAPPING_EXCEPTION,
					runtimeException.getMessage());
		}

		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.MapMachineService#updateMappedDevice(java.util.
	 * List, java.util.List)
	 */
	@Override
	@Transactional
	public ResponseDTO updateMappedDevice(List<DeviceDTO> deletedList, List<DeviceDTO> addedList) {
		LOGGER.info(DEVICE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"getDeviceMappedDevice(List,List) method is strarted");

		ResponseDTO responseDTO = new ResponseDTO();

		try {
			// Delete the devices that are unmapped from the registration machine
			List<RegCentreMachineDevice> regCentreMachineDevices = new ArrayList<>();

			for (DeviceDTO unMappedDeviceDTO : deletedList) {
				RegCentreMachineDeviceId regCentreMachineDeviceId = new RegCentreMachineDeviceId();

				regCentreMachineDeviceId.setRegCentreId(unMappedDeviceDTO.getRegCenterId());
				regCentreMachineDeviceId.setMachineId(unMappedDeviceDTO.getMachineId());
				regCentreMachineDeviceId.setDeviceId(unMappedDeviceDTO.getDeviceId());
				RegCentreMachineDevice device = new RegCentreMachineDevice();
				device.setRegCentreMachineDeviceId(regCentreMachineDeviceId);

				regCentreMachineDevices.add(device);
			}

			machineMappingDAO.deleteUnMappedDevice(regCentreMachineDevices);

			// Save the devices that are mapped to the registration machine
			regCentreMachineDevices = new ArrayList<>();

			for (DeviceDTO mappedDeviceDTO : addedList) {
				RegCentreMachineDevice regCentreMachineDevice = new RegCentreMachineDevice();
				RegCentreMachineDeviceId regCentreMachineDeviceId = new RegCentreMachineDeviceId();

				regCentreMachineDeviceId.setRegCentreId(mappedDeviceDTO.getRegCenterId());
				regCentreMachineDeviceId.setMachineId(mappedDeviceDTO.getMachineId());
				regCentreMachineDeviceId.setDeviceId(mappedDeviceDTO.getDeviceId());
				regCentreMachineDevice.setRegCentreMachineDeviceId(regCentreMachineDeviceId);
				regCentreMachineDevice.setIsActive(true);
				regCentreMachineDevice.setCrBy(SessionContext.userContext().getUserId());
				regCentreMachineDevice.setCrDtime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));

				regCentreMachineDevices.add(regCentreMachineDevice);
			}
			machineMappingDAO.addedMappedDevice(regCentreMachineDevices);

			auditFactory.audit(AuditEvent.DEVICE_MAPPING_SUCCESS, Components.DEVICE_MAPPING,
					RegistrationConstants.APPLICATION_NAME, AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

			// Add the Success Response
			setSuccessResponse(responseDTO, RegistrationConstants.DEVICE_MAPPING_SUCCESS_MESSAGE, null);

			LOGGER.info(DEVICE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
					"getDeviceMappedDevice(List,List) method is ended");
		} catch (RuntimeException runtimeException) {
			// Add the Error Response
			setErrorResponse(responseDTO, RegistrationConstants.DEVICE_MAPPING_ERROR_MESSAGE, null);
			LOGGER.error(DEVICE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID, "Error Response created");
		}

		return responseDTO;
	}

}
