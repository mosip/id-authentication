/**
 * 
 *
 */

package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.MachineHistoryDto;

/**
 * This interface has abstract methods to fetch a Machine History Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

public interface MachineHistoryService {
	/**
	 * This abstract method to fetch machine history details for given Machine ID
	 * and language code
	 * 
	 * @param id
	 *            Machine id given by user
	 * @param langCode
	 *            Language code given by user
	 * @param effDateTime
	 *            Effective date and time given by user
	 * @return List<MachineHistoryDto> Return Machine History Detail for given
	 *         machine id and language code
	 *
	 */
	List<MachineHistoryDto> getMachineHistroyIdLangEffDTime(String id, String langCode, String effDateTime);

}
