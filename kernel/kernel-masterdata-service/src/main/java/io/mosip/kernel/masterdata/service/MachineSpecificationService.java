package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.MachineSpecificationDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.MachineSpecificationExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.id.IdAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;

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
	public IdAndLanguageCodeID createMachineSpecification(MachineSpecificationDto machineSpecification);

	/**
	 * Abstract method to update Machine Specification Details to the Database
	 * 
	 * @param machineSpecification
	 *            machineSpecification DTO
	 * 
	 * @return IdResponseDto Machine Specification ID which is successfully updated
	 *         {@link IdResponseDto}
	 * 
	 * @throws RequestException
	 *             if Machine Specification not Found
	 * @throws MasterDataServiceException
	 *             if any error occurred while updating Machine Specification
	 *
	 */
	public IdAndLanguageCodeID updateMachineSpecification(MachineSpecificationDto machineSpecification);

	/**
	 * Abstract method to delete Machine Specification Details to the Database
	 * 
	 * @param id
	 *            machineSpecification id
	 * 
	 * @return IdResponseDto Machine Specification ID which is successfully deleted
	 *         {@link IdResponseDto}
	 * 
	 * @throws RequestException
	 *             if Machine Specification not Found
	 * @throws MasterDataServiceException
	 *             if any error occurred while deleting Machine Specification
	 * 
	 */
	public IdResponseDto deleteMachineSpecification(String id);

	/**
	 * Method to get all machine specifications.
	 * 
	 * @param pageNumber
	 *            the page number
	 * @param pageSize
	 *            the size of each page
	 * @param sortBy
	 *            the attributes by which it should be ordered
	 * @param orderBy
	 *            the order to be used
	 * 
	 * @return the response i.e. pages containing the machine specifications
	 */
	public PageDto<MachineSpecificationExtnDto> getAllMachineSpecfication(int pageNumber, int pageSize, String sortBy,
			String orderBy);

}
