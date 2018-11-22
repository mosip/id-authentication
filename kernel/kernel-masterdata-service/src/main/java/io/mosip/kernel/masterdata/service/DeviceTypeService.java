package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.DeviceTypeRequestDto;
import io.mosip.kernel.masterdata.dto.PostResponseDto;

/**
 * This interface has abstract methods to save a Device Type Details to the
 * database table
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public interface DeviceTypeService {
	/**
	 * Function to save Device Type Details to the Database
	 * 
	 * @param deviceTypes
	 * 
	 * @return {@link DeviceTypeResponseDto}
	 */
	public PostResponseDto addDeviceTypes(DeviceTypeRequestDto deviceTypes);

}
