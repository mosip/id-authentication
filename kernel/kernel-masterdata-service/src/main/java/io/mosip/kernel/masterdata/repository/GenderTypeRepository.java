package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
import io.mosip.kernel.masterdata.entity.Gender;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

/**
 * Repository class for fetching gender data
 * 
 * @author Urvil Joshi
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Repository
public interface GenderTypeRepository extends BaseRepository<Gender, CodeAndLanguageCodeID> {

	@Query("FROM Gender WHERE langCode =?1 and (isDeleted is null or isDeleted =false)")
	List<Gender> findGenderByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(String langCode);

	
	/**
	 * Update Gender Type by code provided.
	 * 
	 * @param code
	 *            code the gender type code.
	 * @param deletedDateTime
	 *            metadata Deleted Date time
	 * @return rows modified
	 */
	@Modifying
	@Query("UPDATE Gender g SET g.isDeleted =true , g.deletedDateTime = ?2 WHERE g.code =?1 and (g.isDeleted is null or g.isDeleted =false)")
	int deleteGenderType(String code, LocalDateTime deletedDateTime);

	/**
	 * Get Gender Type by code provided.
	 * 
	 * @param code
	 *            the gender type code.
	 * @return list of {@link DocumentCategory}.
	 */
	@Query("FROM Gender WHERE code =?1 AND langCode =?2 AND (isDeleted is null OR isDeleted = false)")
	Gender findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(String code, String langCode);
}
