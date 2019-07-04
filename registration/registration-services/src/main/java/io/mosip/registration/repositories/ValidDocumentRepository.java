package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.ValidDocument;
import io.mosip.registration.entity.id.ValidDocumentID;

/**
 * The Interface ValidDocumentRepository.
 *
 * @author Brahmananda Reddy
 */

public interface ValidDocumentRepository extends BaseRepository<ValidDocument, ValidDocumentID> {
	
	/**
	 * Find by is active true and doc category code.
	 *
	 * @param docCategoryCode the doc category code
	 * @return the list
	 */
	List<ValidDocument> findByIsActiveTrueAndDocCategoryCode(String docCategoryCode);
	
	List<ValidDocument> findAllByIsActiveTrue();
	
}
