package io.mosip.kernel.masterdata.service;


import io.mosip.kernel.masterdata.dto.MachineDetailDto;
import io.mosip.kernel.masterdata.dto.MachineDetailResponseDto;

/**
 * This interface has abstract methods to fetch a Machine Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

public interface MachineDetailService {
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
	MachineDetailDto getMachineDetailIdLang(String id, String langCode);

	/**
	 * This abstract method to fetch all machines details
	 * 
	 * @return Returning all Machines Details
	 *
	 */
	MachineDetailResponseDto getMachineDetailAll();
}
