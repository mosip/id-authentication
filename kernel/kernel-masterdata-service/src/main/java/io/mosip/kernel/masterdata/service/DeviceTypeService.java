package io.mosip.kernel.masterdata.service;


import java.util.List;

import io.mosip.kernel.masterdata.dto.DeviceTypeResponseDto;
import io.mosip.kernel.masterdata.entity.DeviceType;

public interface DeviceTypeService {
	
	public DeviceTypeResponseDto addDeviceType(List<DeviceType> deviceTypeList);

}


