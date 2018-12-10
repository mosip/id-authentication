package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDeviceDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ResponseRrgistrationCenterMachineDeviceDto;

/**
 * The RegistrationCenterMachineDeviceService interface provides method to
 * perform operation on Registration center, Machine and Device. It performs
 * mapping in database for registration center id, machine id and device id.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
public interface RegistrationCenterMachineDeviceService {

	/**
	 * This method saves registration center id, machine id and device id into
	 * database.
	 * 
	 * @param requestDto
	 *            contains {@link RegistrationCenterMachineDeviceDto} which must
	 *            contains registration center id, machine id and device id.
	 * 
	 * @return ResponseRrgistrationCenterMachineDeviceDto contains registration
	 *         center id, machine id and device id.
	 *         
	 *         
	 */
	public ResponseRrgistrationCenterMachineDeviceDto createRegistrationCenterMachineAndDevice(
			RequestDto<RegistrationCenterMachineDeviceDto> requestDto);
}
