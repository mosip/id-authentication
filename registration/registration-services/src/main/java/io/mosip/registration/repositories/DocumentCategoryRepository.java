package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.DocumentCategory;

/**
 * Interface for {@link DocumentCategory} 
 * 
 * @author Brahmananda Reddy
 *
 */
public interface DocumentCategoryRepository extends BaseRepository<DocumentCategory, String> {

	List<DocumentCategory> findByLangCode(String langCode);

}
