package io.mosip.registration.dao.impl;

import java.util.List;

import io.mosip.registration.entity.RegisteredDeviceMaster;

public interface RegisteredDeviceDAO {

	/**
	 * This method is used to get all the registered device
	 * 
	 * @return List of Registered device
	 */
	List<RegisteredDeviceMaster> getRegisteredDevices();
	
	/**
	 * This method is used to get registered device with particular device code
	 * 
	 * @return List of Registered device
	 */
	List<RegisteredDeviceMaster> getRegisteredDevices(String deviceCode);
	
	

}
