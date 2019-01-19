package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.getresponse.MachineHistoryResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.MachineHistory;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

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
	 * @return MachineHistoryResponseDto Return Machine History Detail for given
	 *         machine id and language code
	 *
	 */
	MachineHistoryResponseDto getMachineHistroyIdLangEffDTime(String id, String langCode, String effDateTime);

	/**
	 * Abstract method to save Machine History to the Database
	 * 
	 * @param entityHistory
	 *            machine History entity 
	 * 
	 * @return IdResponseDto returning machine History id which is inserted successfully
	 *         {@link IdResponseDto}
	 * 
	 * @throws MasterDataServiceException
	 *             if any error occurred while saving Machine History
	 */
	IdResponseDto createMachineHistory(MachineHistory entityHistory);

}
