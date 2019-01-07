package io.mosip.registration.service;

import java.util.List;

import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.mastersync.BlacklistedWordsDto;
import io.mosip.registration.dto.mastersync.LocationDto;
import io.mosip.registration.dto.mastersync.MasterReasonListDto;

/**
 * Interface to sync master data from server to client
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface MasterSyncService {

	/**
	 * Gets the master sync.
	 *
	 * @param masterSyncDetails the master sync details
	 * @return the master sync
	 */
	ResponseDTO getMasterSync(String masterSyncDetails);

	/**
	 * Find location by hierarchy code.
	 *
	 * @param hierarchyCode the hierarchy code
	 * @param langCode      the lang code
	 * @return the list
	 */
	List<LocationDto> findLocationByHierarchyCode(String hierarchyCode, String langCode);

	/**
	 * Find proviance by hierarchy code.
	 *
	 * @param code the code
	 * @return the list
	 */
	List<LocationDto> findProvianceByHierarchyCode(String code);
	
	/**
	 * Gets the all reasons.
	 *
	 * @param langCode the lang code
	 * @return the all reasons
	 */
	List<MasterReasonListDto> getAllReasonsList(String langCode);
	
	/**
	 * Gets the all black listed words.
	 *
	 * @param langCode the lang code
	 * @return the all black listed words
	 */
	List<BlacklistedWordsDto> getAllBlackListedWords(String langCode);

}
