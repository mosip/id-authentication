package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.ValidDocument;
import io.mosip.registration.entity.id.ValidDocumentID;

/**
 * 
 * @author Brahmananda Reddy
 *
 */

public interface ValidDocumentRepository extends BaseRepository<ValidDocument, ValidDocumentID> {
	
	List<ValidDocument> findByIsActiveTrueAndDocCategoryCodeAndLangCode(String docCategoryCode, String langCode);
	
}
