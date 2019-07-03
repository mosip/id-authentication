package io.mosip.kernel.masterdata.service;


import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.dto.MachineRegistrationCenterDto;
import io.mosip.kernel.masterdata.dto.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.MachineResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.MachineExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.id.IdAndLanguageCodeID;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;

/**
 * This interface provides methods to do CRUD operations on Machine details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

public interface MachineService {

	/**
	 * This abstract method to fetch machine details for given Machine ID and
	 * language code
	 * 
	 * @param id
	 *            Machine Id given by user
	 * @param langCode
	 *            Language code given by user
	 * @return MachineResponseDto Machine Detail for given machine id and language
	 *         code
	 * @throws MasterDataServiceException
	 *             if any error occurs while retrieving Machine Details
	 * @throws DataNotFoundException
	 *             if no Machine found
	 *
	 */
	public MachineResponseDto getMachine(String id, String langCode);

	/**
	 * This abstract method to fetch all machines details
	 * 
	 * @return MachineResponseDto Returning all Machines Details
	 * @throws MasterDataServiceException
	 *             if any error occurs while retrieving Machine Details
	 * @throws DataNotFoundException
	 *             if no Machine found
	 *
	 */

	public MachineResponseDto getMachineAll();

	/**
	 * This abstract method to fetch machine details for given language code
	 * 
	 * @param langCode
	 *            Language code given by user
	 * @return MachineResponseDto Machine Detail for given machine id and language
	 *         code
	 * @throws MasterDataServiceException
	 *             if any error occurs while retrieving Machine Details
	 * @throws DataNotFoundException
	 *             if no Machine found
	 *
	 */
	public MachineResponseDto getMachine(String langCode);

	/**
	 * Abstract method to save Machine Details to the Database
	 * 
	 * @param machine
	 *            machine DTO
	 * 
	 * @return IdResponseDto returning machine id which is inserted successfully
	 *         {@link IdResponseDto}
	 * 
	 * @throws MasterDataServiceException
	 *             if any error occurred while saving Machine
	 */
	public IdAndLanguageCodeID createMachine(MachineDto machine);

	/**
	 * Abstract method to update Machine Details to the Database
	 * 
	 * @param machine
	 *            machine DTO
	 * 
	 * @return IdResponseDto returning machine id which is updated successfully
	 *         {@link IdResponseDto}
	 * @throws RequestException
	 *             if Machine not Found
	 * 
	 * @throws MasterDataServiceException
	 *             if any error occurred while updating Machine
	 * 
	 */
	public IdAndLanguageCodeID updateMachine(MachineDto machine);

	/**
	 * Abstract method to delete Machine Details to the Database
	 * 
	 * @param id
	 *            machine id
	 * 
	 * @return IdResponseDto returning machine id which is updated successfully
	 *         {@link IdResponseDto}
	 * @throws RequestException
	 *             if Machine not Found
	 * 
	 * @throws MasterDataServiceException
	 *             if any error occurred while updating Machine
	 * 
	 */
	public IdResponseDto deleteMachine(String id);

	/**
	 * Fetch all Machines which are mapped with the given registration center
	 * 
	 * @throws MasterDataServiceException
	 *             if any error occurred while updating Machine
	 * 
	 * @param regCenterId
	 *            Registration center id as String
	 * @return MachineRegistrationCenterDto response object which contain the list
	 *         of machins those are mapped with the given registration center
	 *         {@link RegistrationCenterMachineID}
	 */
	public PageDto<MachineRegistrationCenterDto> getMachinesByRegistrationCenter(String regCenterId, int page, int size, String orderBy, String direction);

	
	public PageResponseDto<MachineExtnDto> searchMachine(SearchDto dto);
}
