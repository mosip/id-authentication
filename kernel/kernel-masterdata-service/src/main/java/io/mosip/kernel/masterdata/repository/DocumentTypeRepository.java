package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.DocumentType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

/**
 * 
 * @author Uday Kumar
 * @author Ritesh Sinha.
 * @since 1.0.0
 *
 */
@Repository
public interface DocumentTypeRepository extends BaseRepository<DocumentType, CodeAndLanguageCodeID> {

	/**
	 * Get Document Type by specific code and language code
	 * 
	 * @param code
	 *            the document category code.
	 * @param langCode
	 *            the language code.
	 * @return object of {@link DocumentType}.
	 */
	@Query(value = "select dt.code, dt.name, dt.descr , dt.lang_code , dt.is_active ,dt.cr_by ,dt.cr_dtimes ,dt.upd_by ,dt.upd_dtimes ,dt.is_deleted ,dt.del_dtimes from master.valid_document vd , master.doc_type dt , master.doc_category dc where vd.doctyp_code = dt.code and vd.doccat_code = dc.code and dc.code = ?1 and dc.lang_code = ?2 and (dt.is_deleted = false or dt.is_deleted is null)", nativeQuery = true)
	List<DocumentType> findByCodeAndLangCodeAndIsDeletedFalse(String code, String langCode);

	/**
	 * Get Document Type by specific code and language code
	 * 
	 * @param code
	 *            the document type code.
	 * @param langCode
	 *            the language code.
	 * @return object of {@link DocumentType}.
	 */
	@Query("FROM DocumentType WHERE code =?1 AND langCode =?2 AND (isDeleted is null OR isDeleted = false)")
	DocumentType findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(String code, String langCode);

	/**
	 * Delete Document Type based on code provided.
	 * 
	 * @param deletedDateTime
	 *            the Date and time of deletion.
	 * @param code
	 *            the document type code.
	 * @param updatedBy
	 *            the updatedby user name.
	 * @return the Integer.
	 */
	@Modifying
	@Query("UPDATE DocumentType d SET d.updatedBy=?3,d.isDeleted =true , d.deletedDateTime = ?1 WHERE d.code =?2 and (d.isDeleted is null or d.isDeleted =false)")
	int deleteDocumentType(LocalDateTime deletedDateTime, String code, String updatedBy);
}
