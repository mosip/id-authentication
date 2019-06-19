package io.mosip.registration.service.sync;

import java.util.List;

import io.mosip.registration.dto.IndividualTypeDto;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.mastersync.BiometricAttributeDto;
import io.mosip.registration.dto.mastersync.BlacklistedWordsDto;
import io.mosip.registration.dto.mastersync.DocumentCategoryDto;
import io.mosip.registration.dto.mastersync.GenderDto;
import io.mosip.registration.dto.mastersync.LocationDto;
import io.mosip.registration.dto.mastersync.ReasonListDto;

/**
 * It makes call to the external 'MASTER Sync' services to download the master data which are relevant to center specific by passing the 
 * center id or mac address or machine id. Once download the data, it stores the information into the DB for further processing.  
 * If center remapping found from the sync response object, it invokes this 'CenterMachineReMapService' object to initiate the center remapping related activities.   
 * During the process, the required informations are updated into the audit table for further tracking.  
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface MasterSyncService {

	/**
	 * It invokes the Master Sync service to download the required information from external services if the system is online.  
	 * Once download, the data would be updated into the DB for further process.  
	 *
	 * @param masterSyncDetails the master sync details
	 * @param triggerPoint from where the call has been initiated [Either : user or system]  
	 * @return success or failure status as Response DTO. 
	 */
	ResponseDTO getMasterSync(String masterSyncDetails,String triggerPoint);

	/**
	 * It invokes the external 'Master Sync' service to download the required center specific information from MOSIP server if the system is online.  
	 * Once download, the data would be updated into the DB for further process.  
	 *
	 * @param masterSyncDetails
	 *            the master sync details
	 * @param triggerPoint
	 *            from where the call has been initiated [Either : user or system] 
	 * @param keyIndex
	 *            This is the key index provided by the MOSIP server post submission of local TPM public key. Based on this key the MOSIP server would identify the 
	 *            client and send the sync response accordingly. 
	 * @return the master sync
	 * 			  Success or failure status is wrapped in ResponseDTO. 
	 */
	ResponseDTO getMasterSync(String masterSyncDetails, String triggerPoint, String keyIndex);

	/**
	 * Find location or region by hierarchy code.   
	 *
	 * @param hierarchyCode the hierarchy code
	 * @param langCode      the lang code
	 * @return the list holds the Location data to be displayed in the UI.  
	 */
	List<LocationDto> findLocationByHierarchyCode(String hierarchyCode, String langCode);

	/**
	 * Find proviance by hierarchy code.
	 *
	 * @param code the code
	 * @param langCode the lang code
	 * @return the list holds the Province data to be displayed in the UI.  
	 */
	List<LocationDto> findProvianceByHierarchyCode(String code,String langCode);
	
	/**
	 * Gets all the reasons for rejection that to be selected during EOD approval process.
	 *
	 * @param langCode the lang code
	 * @return the all reasons
	 */
	List<ReasonListDto> getAllReasonsList(String langCode);
	
	/**
	 * Gets all the black listed words that shouldn't be allowed while capturing demographic information from user. 
	 *
	 * @param langCode the lang code
	 * @return the all black listed words
	 */
	List<BlacklistedWordsDto> getAllBlackListedWords(String langCode);
	
	/**
	 * Gets all the document categories from db that to be displayed in the UI dropdown.
	 *
	 * @param docCode the doc code
	 * @param langCode the lang code
	 * @return all the document categories
	 */
	List<DocumentCategoryDto> getDocumentCategories(String docCode,String langCode);
	
	/**
	 * Gets the gender details.
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
	
	/**
	 * Gets the biometric type.
	 *
	 * @param langCode the lang code
	 * @return the biometric type
	 */
	List<BiometricAttributeDto> getBiometricType(String langCode);

<<<<<<< HEAD
=======

>>>>>>> 5aaf99b205fef882a905d8281eff1e30fc011d34
}
