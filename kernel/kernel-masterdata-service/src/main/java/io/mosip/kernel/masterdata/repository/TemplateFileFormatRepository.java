package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.TemplateFileFormat;

/**
 * @author Neha Sinha
 * @since 1.0.0
 * 
 */
@Repository
public interface TemplateFileFormatRepository extends BaseRepository<TemplateFileFormat, String> {

	/**
	 * Get TemplateFileFormat by specific id and language code
	 * 
	 * @param code
	 *            for TemplateFileFormat
	 * @param langCode
	 *            is the language code present in database
	 * @return object of {@link TemplateFileFormat}
	 */
	@Query("FROM TemplateFileFormat WHERE code =?1 AND langCode =?2 AND (isDeleted is null OR isDeleted = false)")
	TemplateFileFormat findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(String code, String langCode);

	/**
	 * Delete TemplateFileFormat based on code provided.
	 * 
	 * @param deletedDateTime
	 *            the Date and time of deletion.
	 * @param code
	 *            the document category code.
	 * @return the integer.
	 */
	@Modifying
	@Query("UPDATE TemplateFileFormat t SET t.updatedBy = ?3, t.isDeleted = true , t.deletedDateTime = ?1 WHERE t.code =?2 and (t.isDeleted is null or t.isDeleted = false)")
	int deleteTemplateFileFormat(LocalDateTime deletedDateTime, String code, String updatedBy);
}
