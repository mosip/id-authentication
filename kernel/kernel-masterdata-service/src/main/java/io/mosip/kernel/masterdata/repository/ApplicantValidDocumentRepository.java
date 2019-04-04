/**
 * 
 */
package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.ApplicantValidDocument;
import io.mosip.kernel.masterdata.entity.id.ApplicantValidDocumentId;

/**
 * @author Bal Vikash Sharma
 *
 */
public interface ApplicantValidDocumentRepository
		extends BaseRepository<ApplicantValidDocument, ApplicantValidDocumentId> {

	@Query(value = "SELECT dt.lang_code as document_type_lang_code, dt.code as document_type_code, dt.name as document_type_name, dt.descr as document_type_descr, dc.lang_code as document_cat_lang_code, dc.code as document_cat_code, dc.name as document_cat_name, dc.descr as document_cat_descr, avd.lang_code FROM master.applicant_valid_document avd JOIN master.doc_category dc ON dc.code = avd.doccat_code AND avd.is_active = dc.is_active JOIN master.doc_type dt ON dt.code = avd.doctyp_code AND avd.is_active = dt.is_active AND dc.lang_code = dt.lang_code WHERE avd.apptyp_code = :applicantTypeCode AND dt.lang_code IN :languages AND dc.lang_code IN :languages AND avd.is_active = TRUE", nativeQuery = true)
	List<Object[]> getDocumentCategoryAndTypesForApplicantCode(@Param("applicantTypeCode") String applicantTypeCode,
			@Param("languages") List<String> languages);

}
