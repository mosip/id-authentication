package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.MachineDetailResponseDto;
import io.mosip.kernel.masterdata.dto.MachineDetailResponseIdDto;
import io.mosip.kernel.masterdata.dto.MachineRequestDto;
import io.mosip.kernel.masterdata.dto.MachineSpecIdAndId;

/**
 * This interface has abstract methods to fetch a Machine Details
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
	 * @return Machine Detail for given machine id and language code
	 *
	 */
	MachineDetailResponseIdDto getMachineDetailIdLang(String id, String langCode);

	/**
	 * This abstract method to fetch all machines details
	 * 
	 * @return Returning all Machines Details
	 *
	 */
	MachineDetailResponseDto getMachineDetailAll();

	/**
	 * This abstract method to fetch machine details for given language code
	 * 
	 * @param langCode
	 *            Language code given by user
	 * @return Machine Detail for given machine id and language code
	 *
	 */
	MachineDetailResponseDto getMachineDetailLang(String langCode);
	/**
	 * Abstract method to save Machine Details to the Database
	 * 
	 * @param machine
	 * 
	 * @return {@link MachineSpecIdAndId}
	 */
	
	 public MachineSpecIdAndId saveMachine(MachineRequestDto machine);

}
