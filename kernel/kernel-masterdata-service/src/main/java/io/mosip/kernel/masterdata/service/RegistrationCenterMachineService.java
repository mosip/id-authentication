package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ResponseRrgistrationCenterMachineDto;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
public interface RegistrationCenterMachineService {

	public ResponseRrgistrationCenterMachineDto saveRegistrationCenterAndMachine(
			RequestDto<RegistrationCenterMachineDto> requestDto);
}
