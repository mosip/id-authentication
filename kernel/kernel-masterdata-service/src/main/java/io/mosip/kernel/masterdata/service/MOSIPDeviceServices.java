package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.MOSIPDeviceServiceDto;
import io.mosip.kernel.masterdata.dto.MOSIPDeviceServiceExtDto;

/**
 * 
 * @author Megha Tanga
 *
 */

public interface MOSIPDeviceServices {

	public MOSIPDeviceServiceExtDto createMOSIPDeviceService(MOSIPDeviceServiceDto dto);

}
