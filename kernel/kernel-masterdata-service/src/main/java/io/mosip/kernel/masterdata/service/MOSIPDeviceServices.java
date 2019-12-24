package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.MOSIPDeviceServiceDto;
import io.mosip.kernel.masterdata.dto.MOSIPDeviceServiceExtDto;
import io.mosip.kernel.masterdata.dto.MOSIPDeviceServicePUTDto;

/**
 * 
 * @author Megha Tanga
 *
 */

public interface MOSIPDeviceServices {

	public MOSIPDeviceServiceExtDto createMOSIPDeviceService(MOSIPDeviceServiceDto dto);

	public String updateMOSIPDeviceService(MOSIPDeviceServicePUTDto dto);

}
