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
public interface ApplicantValidDocumentRepository extends BaseRepository<ApplicantValidDocument, ApplicantValidDocumentId>{

	@Query(value="SELECT \r\n" + 
			"    dt.lang_code as document_type_lang_code,\r\n" + 
			"    dt.code as document_type_code,\r\n" + 
			"    dt.name as document_type_name,\r\n" + 
			"    dt.descr as document_type_descr,\r\n" + 
			"    dc.lang_code as document_cat_lang_code,\r\n" + 
			"    dc.code as document_cat_code,\r\n" + 
			"    dc.name as document_cat_name,\r\n" + 
			"    dc.descr as document_cat_descr,\r\n" + 
			"    avd.lang_code\r\n" + 
			"FROM\r\n" + 
			"    master.applicant_valid_document avd\r\n" + 
			"        JOIN\r\n" + 
			"    master.doc_category dc ON dc.code = avd.doccat_code\r\n" + 
			"        AND avd.is_active = dc.is_active\r\n" + 
			"        JOIN\r\n" + 
			"    master.doc_type dt ON dt.code = avd.doctyp_code\r\n" + 
			"        AND avd.is_active = dt.is_active\r\n" + 
			"        AND dc.lang_code = dt.lang_code\r\n" + 
			"WHERE\r\n" + 
			"    avd.apptyp_code = :applicantTypeCode\r\n" + 
			"        AND dt.lang_code IN :languages\r\n" + 
			"        AND dc.lang_code IN :languages\r\n" + 
			"        AND avd.is_active = TRUE",nativeQuery=true)
	List<Object[]> getDocumentCategoryAndTypesForApplicantCode(@Param("applicantTypeCode") String applicantTypeCode, @Param("languages") List<String> languages);
	
}
