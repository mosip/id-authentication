package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

/**
 * @author Neha
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Repository
public interface DocumentCategoryRepository extends BaseRepository<DocumentCategory, CodeAndLanguageCodeID> {

	/**
	 * Get all DocumentCategory types
	 *
	 * @param entityClass
	 *            is class of type DocumentCategory
	 * @return list of {@link DocumentCategory}
	 */
	List<DocumentCategory> findAllByIsDeletedFalseOrIsDeletedIsNull(Class<DocumentCategory> entityClass);

	/**
	 * Get all Document category of a specific language using language code
	 * 
	 * @param langCode
	 *            is the language code present in database
	 * @return list of {@link DocumentCategory}
	 */
	@Query("FROM DocumentCategory WHERE langCode =?1 AND (isDeleted is null OR isDeleted = false)")
	List<DocumentCategory> findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(String langCode);

	/**
	 * Get Document Category by specific id and language code
	 * 
	 * @param code
	 *            for document category
	 * @param langCode
	 *            is the language code present in database
	 * @return object of {@link DocumentCategory}
	 */
	@Query("FROM DocumentCategory WHERE code =?1 AND langCode =?2 AND (isDeleted is null OR isDeleted = false)")
	DocumentCategory findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(String code, String langCode);

	/**
	 * Delete Document Category based on code provided.
	 * 
	 * @param deletedDateTime
	 *            the Date and time of deletion.
	 * @param code
	 *            the document category code.
	 * @param updatedBy
	 *            the updatedby user name.
	 * @return the integer.
	 */
	@Modifying
	@Query("UPDATE DocumentCategory d SET d.updatedBy=?3,d.isDeleted =true , d.deletedDateTime = ?1 WHERE d.code =?2 and (d.isDeleted is null or d.isDeleted =false)")
	int deleteDocumentCategory(LocalDateTime deletedDateTime, String code, String updatedBy);
}
