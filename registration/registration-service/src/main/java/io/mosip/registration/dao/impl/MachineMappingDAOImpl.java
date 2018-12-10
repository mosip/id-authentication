package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.MACHINE_MAPPING_LOGGER_TITLE;
import static io.mosip.registration.exception.RegistrationExceptionConstants.REG_USER_MACHINE_MAP_CENTER_MACHINE_CODE;
import static io.mosip.registration.exception.RegistrationExceptionConstants.REG_USER_MACHINE_MAP_CENTER_USER_MACHINE_CODE;
import static io.mosip.registration.exception.RegistrationExceptionConstants.REG_USER_MACHINE_MAP_MACHINE_MASTER_CODE;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.entity.CenterMachine;
import io.mosip.registration.entity.DeviceType;
import io.mosip.registration.entity.MachineMaster;
import io.mosip.registration.entity.RegCenterDevice;
import io.mosip.registration.entity.RegCentreMachineDevice;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.entity.UserMachineMappingID;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.CenterMachineRepository;
import io.mosip.registration.repositories.DeviceMasterRepository;
import io.mosip.registration.repositories.DeviceTypeRepository;
import io.mosip.registration.repositories.MachineMasterRepository;
import io.mosip.registration.repositories.RegistrationCenterDeviceRepository;
import io.mosip.registration.repositories.RegistrationCenterMachineDeviceRepository;
import io.mosip.registration.repositories.RegistrationUserDetailRepository;
import io.mosip.registration.repositories.UserMachineMappingRepository;

/**
 * This DAO implementation of {@link MachineMappingDAO}
 * 
 * @author YASWANTH S
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Repository
public class MachineMappingDAOImpl implements MachineMappingDAO {

	/**
	 * logger for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(MachineMappingDAOImpl.class);
	@Autowired
	private DeviceTypeRepository deviceTypeRepository;
	/**
	 * registrationCenterDeviceRepository instance creation using autowired
	 * annotation
	 */
	@Autowired
	private RegistrationCenterDeviceRepository registrationCenterDeviceRepository;
	/**
	 * 
	 */
	@Autowired
	private RegistrationCenterMachineDeviceRepository registrationCenterMachineDeviceRepository;

	/**
	 * centerMachineRepository instance creation using autowired annotation
	 */
	@Autowired
	private CenterMachineRepository centerMachineRepository;

	/**
	 * machineMasterRepository instance creation using autowired annotation
	 */
	@Autowired
	private MachineMasterRepository machineMasterRepository;

	/**
	 * machineMappingRepository instance creation using autowired annotation
	 */
	@Autowired
	private UserMachineMappingRepository machineMappingRepository;

	/**
	 * userDetailRepository instance creation using autowired annotation
	 */
	@Autowired
	private RegistrationUserDetailRepository userDetailRepository;
	
	/**
	 * deviceMasterRepository instance creation using autowired annotation
	 */
	@Autowired
	private DeviceMasterRepository deviceMasterRepository;

	/*
	 * (non-Javadoc) Getting station id based on mac address
	 * 
	 * @see
	 * io.mosip.registration.dao.MachineMappingDAO#getStationID(java.lang.String)
	 */
	@Override
	public String getStationID(String macAddress) throws RegBaseCheckedException {

		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"getStationID() macAddress --> " + macAddress);

		try {
			MachineMaster machineMaster = machineMasterRepository.findByMacAddress(macAddress);

			if (machineMaster != null && machineMaster.getId() != null) {
				return machineMaster.getId();
			} else {
				throw new RegBaseCheckedException(REG_USER_MACHINE_MAP_MACHINE_MASTER_CODE.getErrorCode(),
						REG_USER_MACHINE_MAP_MACHINE_MASTER_CODE.getErrorMessage());
			}
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.MACHINE_MAPPING_STATIONID_RUN_TIME_EXCEPTION,
					runtimeException.getMessage());
		}
	}

	/*
	 * (non-Javadoc) Getting center id based on station id
	 * 
	 * @see
	 * io.mosip.registration.dao.MachineMappingDAO#getCenterID(java.lang.String)
	 */
	@Override
	public String getCenterID(String stationID) throws RegBaseCheckedException {

		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"getCenterID() stationID --> " + stationID);

		try {
			CenterMachine centerMachine = centerMachineRepository.findByCenterMachineIdId(stationID);
			if (centerMachine != null && centerMachine.getCenterMachineId().getCentreId() != null) {
				return centerMachine.getCenterMachineId().getCentreId();
			} else {
				throw new RegBaseCheckedException(REG_USER_MACHINE_MAP_CENTER_MACHINE_CODE.getErrorCode(),
						REG_USER_MACHINE_MAP_CENTER_MACHINE_CODE.getErrorMessage());
			}
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.MACHINE_MAPPING_CENTERID_RUN_TIME_EXCEPTION,
					runtimeException.getMessage());
		}
	}

	/*
	 * (non-Javadoc) get list of users belongs to the center id
	 * 
	 * @see io.mosip.registration.dao.MachineMappingDAO#getUsers(java.lang.String)
	 */
	@Override
	public List<RegistrationUserDetail> getUsers(String ceneterID) throws RegBaseCheckedException {
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"getUsers() ceneterID -> " + ceneterID);
		try {
			List<RegistrationUserDetail> registrationUserDetail = userDetailRepository
					.findByRegistrationCenterUserRegistrationCenterUserIdRegcntrIdAndIsActiveTrueAndStatusCodeNotLikeAndIdNotLike(
							ceneterID, RegistrationConstants.BLACKLISTED,
							SessionContext.getInstance().getUserContext().getUserId());
			if (!registrationUserDetail.isEmpty()) {
				return registrationUserDetail;
			} else {
				throw new RegBaseCheckedException(REG_USER_MACHINE_MAP_CENTER_USER_MACHINE_CODE.getErrorCode(),
						REG_USER_MACHINE_MAP_CENTER_USER_MACHINE_CODE.getErrorMessage());
			}
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.MACHINE_MAPPING_USERLIST_RUN_TIME_EXCEPTION,
					runtimeException.getMessage());
		}
	}

	/*
	 * (non-Javadoc) Save user to UserMachineMapping
	 * 
	 * @see
	 * io.mosip.registration.dao.MachineMappingDAO#save(io.mosip.registration.entity
	 * .UserMachineMapping)
	 */
	@Override
	public String save(UserMachineMapping user) {
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID, "DAO save method called");

		try {
			// create new mapping
			machineMappingRepository.save(user);
			LOGGER.debug("REGISTRATION - USER CLIENT MACHINE MAPPING", APPLICATION_NAME, APPLICATION_ID,
					"DAO save method ended");

			return RegistrationConstants.MACHINE_MAPPING_CREATED;
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.MACHINE_MAPPING_RUN_TIME_EXCEPTION,
					runtimeException.getMessage());
		}
	}

	/*
	 * (non-Javadoc) update user to UserMachineMapping
	 * 
	 * @see
	 * io.mosip.registration.dao.MachineMappingDAO#update(io.mosip.registration.
	 * entity.UserMachineMapping)
	 */
	@Override
	public String update(UserMachineMapping user) {
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID, "DAO update method called");

		try {
			// update user details in user mapping
			machineMappingRepository.update(user);
			LOGGER.debug("REGISTRATION - USER CLIENT MACHINE MAPPING", APPLICATION_NAME, APPLICATION_ID,
					"DAO update method ended");

			return RegistrationConstants.MACHINE_MAPPING_UPDATED;
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.MACHINE_MAPPING_RUN_TIME_EXCEPTION,
					runtimeException.getMessage());
		}
	}

	/*
	 * (non-Javadoc) find user from UserMachineMappingID
	 * 
	 * @see
	 * io.mosip.registration.dao.MachineMappingDAO#findByID(io.mosip.registration.
	 * entity.UserMachineMapping)
	 */
	@Override
	public UserMachineMapping findByID(UserMachineMappingID userID) {
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID, "DAO findByID method called");

		UserMachineMapping machineMapping = null;

		try {
			// find the user
			machineMapping = machineMappingRepository.findById(UserMachineMapping.class, userID);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.MACHINE_MAPPING_RUN_TIME_EXCEPTION,
					runtimeException.getMessage());
		}
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID, "DAO findByID method ended");

		return machineMapping;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.MachineMappingDAO#getAllDeviceTypes()
	 */
	@Override
	public List<DeviceType> getAllDeviceTypes() {
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				" getAllDeviceTypes() method is started");

		return deviceTypeRepository.findByIsActiveTrue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.MachineMappingDAO#getAllValidDevicesByCenterId(java
	 * .lang.String)
	 */
	@Override
	public List<RegCenterDevice> getAllValidDevicesByCenterId(String centerId) {
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"  getAllDeviceBasedOnCenterId method has started");

		return registrationCenterDeviceRepository
				.findByRegCenterDeviceIdRegCenterIdAndIsActiveTrueAndRegDeviceMasterValidityEndDtimesGreaterThanEqual(
						centerId, Timestamp.valueOf(LocalDate.now().atStartOfDay()));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.MachineMappingDAO#getAllMappedDevices(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public List<RegCentreMachineDevice> getAllMappedDevices(String centerId, String machineId) {
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"  getAllMappedDevices method excecution has started");

		return registrationCenterMachineDeviceRepository
				.findByRegCentreMachineDeviceIdRegCentreIdAndRegCentreMachineDeviceIdMachineId(centerId, machineId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.MachineMappingDAO#deleteUnMappedDevice(java.util.
	 * List)
	 */
	@Override
	public void deleteUnMappedDevice(List<RegCentreMachineDevice> regCentreMachineDevices) {
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"  deleteUnMappedDevice method excecution has started");

		registrationCenterMachineDeviceRepository.deleteAll(regCentreMachineDevices);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.MachineMappingDAO#addedMappedDevice(java.util.List)
	 */
	@Override
	public void addedMappedDevice(List<RegCentreMachineDevice> regCentreMachineDevices) {
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"  addedMappedDevice method excecution has started");

		registrationCenterMachineDeviceRepository.saveAll(regCentreMachineDevices);
	}


	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.MachineMappingDAO#isValidDevice(java.lang.String, java.lang.String, java.sql.Timestamp)
	 */
	@Override
	public boolean isValidDevice(String serialNumber, String deviceType,Timestamp currentDate) {
		
		LOGGER.debug("REGISTRATION - COMMON REPOSITORY ", APPLICATION_NAME, APPLICATION_ID, " isValidDevice DAO Method called");
		
		Long regDeviceMaster = deviceMasterRepository.countBySerialNumberAndNameAndIsActiveTrueAndValidityEndDtimesGreaterThan(deviceType,
				serialNumber,currentDate);	

		return regDeviceMaster != 0 ? true : false;
	}
	
	 	
}