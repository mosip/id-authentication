package io.mosip.kernel.masterdata.service;


import io.mosip.kernel.masterdata.dto.DeviceTypeRequestDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeResponseDto;

public interface DeviceTypeService {
	
	public DeviceTypeResponseDto addDeviceType(DeviceTypeRequestDto deviceTypes);

}


