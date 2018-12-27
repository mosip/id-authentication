package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
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
	 * Update gender type by code and langcode
	 * 
	 * @param code
	 *            code to be updated
	 * @param langCode
	 *            lang code to be updated
	 * @param genderName
	 *            genderName to be updated
	 * @param isActive
	 *            mapping is active or not
	 * @param updatedDateTime
	 *            upated timestamp
	 * @param updatedBy
	 *            updating iser
	 */
	@Modifying
	@Query("UPDATE Gender g SET g.code =?1 , g.langCode = ?2 ,g.genderName=?3, g.isActive=?4 ,g.updatedDateTime=?5, g.updatedBy=?6 WHERE g.code =?1 and g.langCode=?2 and (g.isDeleted is null or g.isDeleted =false)")
	int updateGenderType(String code, String langCode, String genderName, Boolean isActive,
			LocalDateTime updatedDateTime, String updatedBy);
}
