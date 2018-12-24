package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.MachineSpecificationDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * This interface provides methods to do CRUD operations on MachineSpecification
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public interface MachineSpecificationService {

	/**
	 * Abstract method to save Machine Specification Details to the Database
	 * 
	 * @param machineSpecification
	 *            machineSpecification DTO
	 * 
	 * @return IdResponseDto Machine Specification ID which is successfully inserted
	 *         {@link IdResponseDto}
	 * @throws MasterDataServiceException
	 *             if any error occurred while saving Machine Specification
	 */
	public IdResponseDto createMachineSpecification(RequestDto<MachineSpecificationDto> machineSpecification);
	
	/**
	 * Abstract method to update Machine Specification Details to the Database
	 * 
	 * @param machineSpecification
	 *            machineSpecification DTO
	 * 
	 * @return IdResponseDto Machine Specification ID which is successfully updated
	 *         {@link IdResponseDto}
	 * @throws MasterDataServiceException
	 *             if any error occurred while updating Machine Specification
	 */
	public IdResponseDto updateMachineSpecification(RequestDto<MachineSpecificationDto> machineSpecification);
	/**
	 * Abstract method to update Machine Specification Details to the Database
	 * 
	 * @param id
	 *            machineSpecification id
	 * 
	 * @return IdResponseDto Machine Specification ID which is successfully deleted
	 *         {@link IdResponseDto}
	 * @throws MasterDataServiceException
	 *             if any error occurred while deleting Machine Specification
	 */
	public IdResponseDto deleteMachineSpecification(String id);

}
