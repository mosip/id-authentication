package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenterType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

/**
 * Interface for RegistrationCenterType Repository.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationCenterTypeRepository
		extends BaseRepository<RegistrationCenterType, CodeAndLanguageCodeID> {
	/**
	 * Set isDeleted to false of the registration center type based on code
	 * provided.
	 * 
	 * @param deletedDateTime
	 *            the Date and time of deletion.
	 * @param code
	 *            the registration center type code.
	 * @return the number of rows whose isDeleted is set to true.
	 */
	@Modifying
	@Query("UPDATE RegistrationCenterType r SET r.isDeleted =true , r.deletedDateTime = ?1 WHERE r.code =?2 and (r.isDeleted is null or r.isDeleted =false)")
	int deleteRegistrationCenterType(LocalDateTime deletedDateTime, String code);

	/**
	 * This method triggers query to fetch the list of registration center types
	 * based on code.
	 * 
	 * @param code
	 *            the code against the registration center types needs to be
	 *            fetched.
	 * @return the list of registration center types.
	 */
	List<RegistrationCenterType> findByCode(String code);

	/**
	 * This method triggers query to fetch the list of registration center types
	 * based on code.
	 * 
	 * @param code
	 *            the code against which it needs to be fetched.
	 * @return the list of registration center types.
	 */
	@Query("FROM RegistrationCenterType WHERE code =?1 AND (isDeleted is null OR isDeleted = false)")
	List<RegistrationCenterType> findByCodeAndIsDeletedFalseOrIsDeletedIsNull(String code);

	/**
	 * This method triggers query to fetch registration center type based on code
	 * and language code.
	 * 
	 * @param code
	 *            the code against which the registration center types need to be
	 *            fetched.
	 * @param langCode
	 *            the language code against which registration center types needs to
	 *            be fetched.
	 * @return the registration center types.
	 */
	@Query("FROM RegistrationCenterType WHERE code =?1 AND langCode =?2 AND (isDeleted is null OR isDeleted = false)")
	RegistrationCenterType findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(String code, String langCode);
}
