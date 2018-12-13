package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.DeviceTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * This interface has abstract methods to save a Device Type Details
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
	 *            input from user
	 * @return CodeAndLanguageCodeID returning code and LanguageCode
	 * @throws MasterDataServiceException
	 *             if any error occurred while saving Device Type
	 * 
	 */
	public CodeAndLanguageCodeID createDeviceTypes(RequestDto<DeviceTypeDto> deviceTypes);

}
