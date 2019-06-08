package io.mosip.registration.service.sync;

import java.util.List;

import io.mosip.registration.dto.IndividualTypeDto;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.mastersync.BlacklistedWordsDto;
import io.mosip.registration.dto.mastersync.DocumentCategoryDto;
import io.mosip.registration.dto.mastersync.GenderDto;
import io.mosip.registration.dto.mastersync.LocationDto;
import io.mosip.registration.dto.mastersync.ReasonListDto;

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
	 * @param triggerPoint the trigger point
	 * @return the master sync
	 */
	ResponseDTO getMasterSync(String masterSyncDetails,String triggerPoint);

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
	 * @param langCode the lang code
	 * @return the list
	 */
	List<LocationDto> findProvianceByHierarchyCode(String code,String langCode);
	
	/**
	 * Gets the all reasons.
	 *
	 * @param langCode the lang code
	 * @return the all reasons
	 */
	List<ReasonListDto> getAllReasonsList(String langCode);
	
	/**
	 * Gets the all black listed words.
	 *
	 * @param langCode the lang code
	 * @return the all black listed words
	 */
	List<BlacklistedWordsDto> getAllBlackListedWords(String langCode);
	
	/**
	 * Gets all the document categories.
	 *
	 * @param docCode the doc code
	 * @param langCode the lang code
	 * @return all the document categories
	 */
	List<DocumentCategoryDto> getDocumentCategories(String docCode,String langCode);
	
	/**
	 * Gets the gender dtls.
	 *
	 * @param langCode the lang code
	 * @return the gender dtls
	 */
	List<GenderDto> getGenderDtls(String langCode);
	
	/**
	 * Gets the individual type.
	 *
	 * @param code the code
	 * @param langCode the lang code
	 * @return the individual type
	 */
	List<IndividualTypeDto> getIndividualType(String code,String langCode);

}
