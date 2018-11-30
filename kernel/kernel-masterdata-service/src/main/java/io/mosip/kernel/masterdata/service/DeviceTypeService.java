package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.DeviceTypeRequestDto;
import io.mosip.kernel.masterdata.dto.postresponse.PostResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

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
	 * Abstract method to save Device Type Details to the Database
	 * 
	 * @param deviceTypes
	 * 
	 * @return {@link PostResponseDto}
	 */
	public CodeAndLanguageCodeID createDeviceTypes(DeviceTypeRequestDto deviceTypes);

}
