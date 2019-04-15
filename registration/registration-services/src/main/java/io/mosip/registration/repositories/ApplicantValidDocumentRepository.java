package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.ApplicantValidDocument;
import io.mosip.registration.entity.id.ApplicantValidDocumentID;

/**
 * Interface for {@link ApplicantValidDocument} 
 * 
 * @author Brahmananda Reddy
 *
 */

public interface ApplicantValidDocumentRepository extends BaseRepository<ApplicantValidDocument, ApplicantValidDocumentID> {

	List<ApplicantValidDocument> findByIsActiveTrueAndDocumentCategoryCodeAndDocumentCategoryLangCode(String docCategoryCode,
			String langCode);

	List<ApplicantValidDocument> findByValidDocumentAppTypeCodeAndDocumentCategoryCode(
			String applicantType, String docCategoryCode);
	
}
