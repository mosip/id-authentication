package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

/**
 * @author Neha
 * @author Bal Vikash Sharma
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
	public List<DocumentCategory> findAllByIsDeletedFalse(Class<DocumentCategory> entityClass);

	/**
	 * Get all Document category of a specific language using language code
	 * 
	 * @param langCode
	 *            is the language code present in database
	 * @return list of {@link DocumentCategory}
	 */
	List<DocumentCategory> findAllByLangCodeAndIsDeletedFalse(String langCode);

	/**
	 * Get Document Category by specific id and language code
	 * 
	 * @param code
	 *            for document category
	 * @param langCode
	 *            is the language code present in database
	 * @return object of {@link DocumentCategory}
	 */
	DocumentCategory findByCodeAndLangCodeAndIsDeletedFalse(String code, String langCode);

}
