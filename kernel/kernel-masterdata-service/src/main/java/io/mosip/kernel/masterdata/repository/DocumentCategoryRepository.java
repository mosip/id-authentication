package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.DocumentCategory;

/**
 * @author Neha
 * @since 1.0.0
 *
 */
@Repository
public interface DocumentCategoryRepository extends BaseRepository<DocumentCategory, String> {

	/**
	 * Get all DocumentCategory types
	 *
	 * @return {@link List<DocumentCategory>}
	 */
	public List<DocumentCategory> findAllByIsActiveTrueAndIsDeletedFalse(Class<DocumentCategory> entityClass);
	
	/**
	 * Get all Document category of a specific language using language code
	 * 
	 * @param langCode
	 * @return {@link List<DocumentCategory>}
	 */
	List<DocumentCategory> findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(String langCode);

	/**
	 * Get Document Category by specific id and language code
	 * 
	 * @param code
	 * @param langCode
	 * @return {@linkplain DocumentCategory}
	 */
	DocumentCategory findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(String code, String langCode);

}
