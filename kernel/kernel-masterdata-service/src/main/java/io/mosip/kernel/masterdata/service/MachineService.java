package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.MachineResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * This interface has abstract methods to save and fetch a Machine Details
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
	MachineResponseDto getMachine(String id, String langCode);

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

	MachineResponseDto getMachineAll();

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
	MachineResponseDto getMachine(String langCode);

	/**
	 * Abstract method to save Machine Details to the Database
	 * 
	 * @param machine
	 *            input from user
	 * 
	 * @return IdResponseDto returning machine id which is inserted successfully
	 *         {@link IdResponseDto}
	 * 
	 * @throws MasterDataServiceException
	 *             if any error occurred while saving Machine
	 */
	public IdResponseDto createMachine(RequestDto<MachineDto> machine);

}
