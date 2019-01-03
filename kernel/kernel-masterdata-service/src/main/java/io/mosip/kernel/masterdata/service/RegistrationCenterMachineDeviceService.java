package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDeviceDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ResponseRrgistrationCenterMachineDeviceDto;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineDeviceID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * The RegistrationCenterMachineDeviceService interface provides method to
 * perform operation on Registration center, Machine and Device. It performs
 * mapping in database for registration center id, machine id and device id.
 * 
 * @author Bal Vikash Sharma
 * @author Srinivasan
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
	 * @throws MasterDataServiceException
	 *             if any error occurs while mapping registration center id, machine
	 *             id and device id. Like if registration center id, machine id or
	 *             device id is not valid or not present in database.
	 * 
	 */
	public ResponseRrgistrationCenterMachineDeviceDto createRegistrationCenterMachineAndDevice(
			RequestDto<RegistrationCenterMachineDeviceDto> requestDto);

	/**
	 * This method deletes data from the database. It updates the flag isDeleted to
	 * true to signify that the data is deleted
	 * 
	 * @param regCenterId
	 *            - Registration center id
	 * @param machineId
	 *            - Machine Id
	 * @param deviceId
	 *            - Device Id
	 * @return {@link RegistrationCenterMachineDeviceID}
	 */
	public RegistrationCenterMachineDeviceID deleteRegistrationCenterMachineAndDevice(String regCenterId,
			String machineId, String deviceId);
}
