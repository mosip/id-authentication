package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDeviceDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ResponseRrgistrationCenterMachineDeviceDto;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
public interface RegistrationCenterMachineDeviceService {

	public ResponseRrgistrationCenterMachineDeviceDto mapRegistrationCenterMachineAndDevice(
			RequestDto<RegistrationCenterMachineDeviceDto> requestDto);
}
