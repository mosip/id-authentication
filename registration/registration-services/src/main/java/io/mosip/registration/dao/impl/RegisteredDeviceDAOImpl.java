package io.mosip.registration.dao.impl;

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
		// TODO Auto-generated method stub
		return registeredDeviceRepository.findAll();
	}

	@Override
	public List<RegisteredDeviceMaster> getRegisteredDevices(String deviceCode) {
		// TODO Auto-generated method stub
		return registeredDeviceRepository.findAllByIsActiveTrueAndDeviceId(deviceCode);
	}

}
