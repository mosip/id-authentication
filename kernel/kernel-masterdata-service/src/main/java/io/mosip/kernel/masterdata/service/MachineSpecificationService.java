package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.MachineSpecificationDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * This interface has abstract methods to fetch and save Machine Specification
 * Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public interface MachineSpecificationService {

	/**
	 * Function to save Machine Specification Details to the Database
	 * 
	 * @param machineSpecification
	 *            input from user
	 * 
	 * @return IdResponseDto Machine Specification ID which is successfully inserted
	 *         {@link IdResponseDto}
	 * @throws MasterDataServiceException
	 *             if any error occurred while saving Device
	 */
	public IdResponseDto createMachineSpecification(RequestDto<MachineSpecificationDto> machineSpecification);

}
