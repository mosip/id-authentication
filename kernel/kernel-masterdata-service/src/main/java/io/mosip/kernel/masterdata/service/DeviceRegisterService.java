package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.DeviceRegisterDto;
import io.mosip.kernel.masterdata.dto.DeviceRegisterResponseDto;

public interface DeviceRegisterService {
	public DeviceRegisterResponseDto registerDevice(DeviceRegisterDto request);
}
