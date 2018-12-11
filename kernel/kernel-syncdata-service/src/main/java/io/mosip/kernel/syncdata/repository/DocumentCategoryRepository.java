package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.DocumentCategory;
import io.mosip.kernel.syncdata.entity.id.CodeAndLanguageCodeID;

/**
 * @author Neha
 * @since 1.0.0
 *
 */
@Repository
public interface DocumentCategoryRepository extends BaseRepository<DocumentCategory, CodeAndLanguageCodeID> {

	/**
	 * Get all DocumentCategory types
	 *
	 * @return {@link List<DocumentCategory>}
	 */
	public List<DocumentCategory> findAllByIsDeletedFalse(Class<DocumentCategory> entityClass);

	/**
	 * Get all Document category of a specific language using language code
	 * 
	 * @param langCode
	 * @return {@link List<DocumentCategory>}
	 */
	List<DocumentCategory> findAllByLangCodeAndIsDeletedFalse(String langCode);

	/**
	 * Get Document Category by specific id and language code
	 * 
	 * @param code
	 * @param langCode
	 * @return {@linkplain DocumentCategory}
	 */
	DocumentCategory findByCodeAndLangCodeAndIsDeletedFalse(String code, String langCode);

	@Query("FROM DocumentCategory WHERE createdDateTime > ?1 OR updatedDateTime > ?1  OR deletedDateTime > ?1")
	List<DocumentCategory> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);
}
