package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.ValidDocument;
import io.mosip.registration.entity.id.ValidDocumentId;

/**
 * 
 * @author Brahmananda Reddy
 *
 */

public interface ValidDocumentRepository extends BaseRepository<ValidDocument, ValidDocumentId> {

	List<ValidDocument> findByIsActiveTrueAndDocumentCategoryCodeAndDocumentCategoryLangCode(String docCategoryCode,
			String langCode);

	List<ValidDocument> findByValidDocumentIdAppTypeCodeAndDocumentCategoryCodeAndLangCode(
			String applicantType, String docCategoryCode, String langCode);
	
}
