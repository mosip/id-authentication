package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.dto.mastersync.MasterDataResponseDto;
import io.mosip.registration.entity.BiometricAttribute;
import io.mosip.registration.entity.BlacklistedWords;
import io.mosip.registration.entity.DocumentCategory;
import io.mosip.registration.entity.DocumentType;
import io.mosip.registration.entity.Gender;
import io.mosip.registration.entity.IndividualType;
import io.mosip.registration.entity.Language;
import io.mosip.registration.entity.Location;
import io.mosip.registration.entity.ReasonCategory;
import io.mosip.registration.entity.ReasonList;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.entity.ValidDocument;

/**
 * This class is used to store the master data details like Location, gender,Registration center, 
 * Document types,category etc., in the respective Databases. This class is also used to fetch any 
 * of the master data details from the DB.
 *
 * @author Sreekar Chukka
 * @since 1.0.0
 */
public interface MasterSyncDao {

	/**
	 * This method is used to fetch a job detail of a paticular job id.
	 *
	 * @param synccontrol
	 *            the {@link SyncControl} entity
	 * @return the master sync status
	 */
	public SyncControl syncJobDetails(String synccontrol);

	/**
	 * All the master data such as Location, gender,Registration center, Document types,category etc., 
	 * will be saved in the DB(These details will be getting from the MasterSync service)
	 *
	 * @param masterSyncDto
	 *            All the master details will be available in the {@link MasterDataResponseDto}
	 * @return the string
	 * 			- Returns the Success or Error response
	 */
	public String save(MasterDataResponseDto masterSyncDto);

	/**
	 * Find location by lang code.
	 *
	 * @param hierarchyCode
	 *            the hierarchy code
	 * @param langCode
	 *            the lang code
	 * @return the list
	 */
	List<Location> findLocationByLangCode(String hierarchyCode, String langCode);

	/**
	 * Find location by parent loc code.
	 *
	 * @param parentLocCode
	 *            the parent loc code
	 * @param langCode
	 *            the lang code
	 * @return the list
	 */
	List<Location> findLocationByParentLocCode(String parentLocCode, String langCode);

	/**
	 * Gets the all reason catogery.
	 *
	 * @param langCode
	 *            the lang code
	 * @return the all reason catogery
	 */
	List<ReasonCategory> getAllReasonCatogery(String langCode);

	/**
	 * Gets the reason list.
	 *
	 * @param langCode
	 *            the lang code
	 * @param reasonCat
	 *            the reason cat
	 * @return the reason list
	 */
	List<ReasonList> getReasonList(String langCode, List<String> reasonCat);

	/**
	 * Gets the black listed words.
	 *
	 * @param langCode
	 *            the lang code
	 * @return the black listed words
	 */
	List<BlacklistedWords> getBlackListedWords(String langCode);

	/**
	 * Gets the Document Categories.
	 *
	 * @param docCode
	 *            the doc code
	 * @param langCode
	 *            the lang code
	 * @return the document categories
	 */
	List<DocumentType> getDocumentTypes(List<String> docCode, String langCode);

	/**
	 * Gets the gender dtls.
	 *
	 * @param langCode
	 *            the lang code
	 * @return the gender dtls
	 */
	List<Gender> getGenderDtls(String langCode);

	/**
	 * Gets the valid documets.
	 *
	 * @param docCategoryCode
	 *            the doc category code
	 * @return the valid documets
	 */
	List<ValidDocument> getValidDocumets(String docCategoryCode);

	/**
	 * Gets the individul type.
	 *
	 * @param code
	 *            the code
	 * @param langCode
	 *            the lang code
	 * @return the individul type
	 */
	List<IndividualType> getIndividulType(String code, String langCode);

	/**
	 * Get All the Active Sync JOBS
	 * 
	 * @return active sync jobs
	 */
	List<SyncJobDef> getSyncJobs();

	/**
	 * Gets the biometric type.
	 *
	 * @param biometricType
	 *            the biometricType
	 * @param langCode
	 *            the lang code
	 * @return the biometric type
	 */
	List<BiometricAttribute> getBiometricType(String langCode, List<String> biometricType);

	/**
	 * Get all the active languages
	 * @return List of {@link Language}
	 */
	List<Language> getActiveLanguages();

	/**
	 * Get all the active genders
	 * @return list of active {@link Gender}
	 */
	List<Gender> getGenders();

	/**
	 * Get all the active document category from the DB
	 * @return List of active {@link DocumentCategory}
	 */
	List<DocumentCategory> getDocumentCategory();


	/**
	 * Get all the active locations from the DB
	 * @return list of active {@link Location}
	 */
	List<Location> getLocationDetails();
	

}
