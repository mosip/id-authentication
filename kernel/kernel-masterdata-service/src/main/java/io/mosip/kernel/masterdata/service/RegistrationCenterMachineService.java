package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ResponseRrgistrationCenterMachineDto;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * The RegistrationCenterMachineService interface provides method to perform
 * operation on Registration center and Machine. It performs mapping in database
 * for registration center id and machine id.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
public interface RegistrationCenterMachineService {

	/**
	 * @param requestDto
	 *            contains {@link RegistrationCenterMachineDto} which must contain
	 *            registration center id and machine id.
	 * @return response object which contains registration center id and machine id.
	 * @throws MasterDataServiceException
	 *             if any error occurs while mapping registration center id and
	 *             machine id. Like if registration center id or machine id is not
	 *             valid or not present in database.
	 */
	public ResponseRrgistrationCenterMachineDto createRegistrationCenterAndMachine(
			RequestDto<RegistrationCenterMachineDto> requestDto);
}
