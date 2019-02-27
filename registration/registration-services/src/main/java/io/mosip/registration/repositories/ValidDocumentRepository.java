package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.ValidDocument;

/**
 * 
 * @author Brahmananda Reddy
 *
 */

public interface ValidDocumentRepository extends BaseRepository<ValidDocument, String> {

	List<ValidDocument> findByIsActiveTrueAndDocCategoryCodeAndLangCode(String docCategoryCode, String langCode);

	List<ValidDocument> findByDocCategoryCodeAndLangCode(String docCategoryCode, String langCode);

	List<ValidDocument> findByApplicantTypeAndDocCategoryCodeAndLangCode(String applicantType, String docCategoryCode,
			String langCode);
}
