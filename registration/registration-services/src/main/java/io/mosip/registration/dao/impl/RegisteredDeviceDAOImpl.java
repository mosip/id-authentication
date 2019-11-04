package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.entity.RegisteredDeviceMaster;
import io.mosip.registration.repositories.RegisteredDeviceRepository;

@Repository
public class RegisteredDeviceDAOImpl implements RegisteredDeviceDAO {

	
	/** instance of {@link RegisteredDeviceRepository} */
	@Autowired
	private RegisteredDeviceRepository registeredDeviceRepository;
	
	/** instance of {@link Logger} */
	private static final Logger LOGGER = AppConfig.getLogger(RegisteredDeviceDAOImpl.class);

	@Override
	public List<RegisteredDeviceMaster> getRegisteredDevices() {
		LOGGER.info("REGISTRATION-PACKET_DEVICE_VALIDATION", APPLICATION_NAME, APPLICATION_ID,
				"fetching all the registered devices");

		return registeredDeviceRepository.findAll();
	}

	@Override
	public List<RegisteredDeviceMaster> getRegisteredDevices(String deviceCode) {
		
		LOGGER.info("REGISTRATION-PACKET_DEVICE_VALIDATION", APPLICATION_NAME, APPLICATION_ID,
				"fetching the device with device code : "+ deviceCode);

		return registeredDeviceRepository.findAllByIsActiveTrueAndDeviceId(deviceCode);
	}

}
