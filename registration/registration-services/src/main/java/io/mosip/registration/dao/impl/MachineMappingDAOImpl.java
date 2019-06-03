package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.MACHINE_MAPPING_LOGGER_TITLE;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.DeviceTypes;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.entity.MachineMaster;
import io.mosip.registration.entity.RegDeviceMaster;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.DeviceMasterRepository;
import io.mosip.registration.repositories.MachineMasterRepository;
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

		LOGGER.info(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"getStationID() macAddress --> " + macAddress);

		try {
			MachineMaster machineMaster = machineMasterRepository.findByIsActiveTrueAndMacAddress(macAddress);

			if (machineMaster != null && machineMaster.getRegMachineSpecId().getId() != null) {
				return machineMaster.getRegMachineSpecId().getId();
			} else {
				return null;
			}
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.MACHINE_MAPPING_STATIONID_RUN_TIME_EXCEPTION,
					runtimeException.getMessage());
		}
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.MachineMappingDAO#isValidDevice(java.lang.String,
	 * java.lang.String, java.sql.Timestamp)
	 */
	@Override
	public boolean isValidDevice(DeviceTypes deviceType, String serialNo) {

		LOGGER.info("REGISTRATION - COMMON REPOSITORY ", APPLICATION_NAME, APPLICATION_ID,
				" isValidDevice DAO Method called");

		return deviceMasterRepository.countBySerialNumAndNameAndIsActiveTrueAndValidityEndDtimesGreaterThan(serialNo,
				deviceType.getDeviceType(), Timestamp.valueOf(DateUtils.getUTCCurrentDateTime())) > 0 ? true : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.MachineMappingDAO#getUserMappingDetails(java.lang.
	 * String)
	 */
	@Override
	public List<UserMachineMapping> getUserMappingDetails(String machineId) {
		return machineMappingRepository.findByIsActiveTrueAndUserMachineMappingIdMachineID(machineId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.MachineMappingDAO#getDevicesMappedToRegCenter(java.
	 * lang.String)
	 */
	@Override
	public List<RegDeviceMaster> getDevicesMappedToRegCenter(String langCode) {
		LOGGER.info(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"Fetch all the devices mapped to the registration center");

		return deviceMasterRepository.findByRegMachineSpecIdLangCode(langCode);
	}

	@Override
	public boolean isExists(String userId) {
		LOGGER.info(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID, "checking exists or not");

		return machineMappingRepository.findByUserMachineMappingIdUserID(userId) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.MachineMappingDAO#getMachineByName(java.lang.
	 * String)
	 */
	@Override
	public MachineMaster getMachineByName(String name) {
		LOGGER.info(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"Fetching Machine Master details based on name");

		return machineMasterRepository.findByIsActiveTrueAndName(name);
	}

}