package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.MOSIPDeviceServiceDto;
import io.mosip.kernel.masterdata.entity.MOSIPDeviceService;

/**
 * 
 * @author Megha Tanga
 *
 */

public interface MOSIPDeviceServices {

	public MOSIPDeviceService createMOSIPDeviceService(MOSIPDeviceServiceDto dto);

}
