package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.DeviceLangCodeResponseDto;
import io.mosip.kernel.masterdata.dto.DeviceResponseDto;

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
	public DeviceResponseDto getDeviceLangCode(String langCode);

	public DeviceLangCodeResponseDto getDeviceLangCodeAndDeviceType(String langCode, String devideTypeCode);

}
