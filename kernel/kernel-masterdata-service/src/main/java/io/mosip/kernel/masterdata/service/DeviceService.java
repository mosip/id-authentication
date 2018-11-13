package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.DeviceDto;
import io.mosip.kernel.masterdata.dto.DeviceLangCodeDtypeDto;

/**
 * This interface has abstract methods to fetch a Device Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

public interface DeviceService {
	
	/**
	 * This abstract method to fetch all Devices details
	 * 
	 * @return Returning all Devices Details
	 *
	 */
	List<DeviceDto> getDeviceAll();
	
	
	List<DeviceDto> getDeviceLangCode(String langCode);
	
	List<DeviceLangCodeDtypeDto> getDeviceLangCodeAndDeviceType(String langCode, String devideTypeCode);
}

