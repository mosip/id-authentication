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
 * The Interface MasterSyncDao.
 *
 * @author Sreekar Chukka
 * @since 1.0.0
 */
public interface MasterSyncDao {

	/**
	 * Gets the master sync status.
	 *
	 * @param synccontrol
	 *            the synccontrol
	 * @return the master sync status
	 */
	public SyncControl syncJobDetails(String synccontrol);

	/**
	 * inserting master sync data into the database using entity.
	 *
	 * @param masterSyncDto
	 *            the master sync dto
	 * @return the string
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

	List<Language> getActiveLanguages();

	List<Gender> getGenders();

	List<DocumentCategory> getDocumentCategory();


	List<Location> getLocationDetails();
	

}
