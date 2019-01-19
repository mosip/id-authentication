package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.ValidDocument;
import io.mosip.kernel.masterdata.entity.id.ValidDocumentID;

/**
 * Repository for valid document.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Repository
public interface ValidDocumentRepository extends BaseRepository<ValidDocument, ValidDocumentID> {

	/**
	 * Method to find valid document based on code provided.
	 * 
	 * @param code
	 *            the document category code.
	 * @return list of valid document.
	 */
	@Query("FROM ValidDocument WHERE docCategoryCode=?1 AND (isDeleted is null OR isDeleted = false)")
	List<ValidDocument> findByDocCategoryCode(String code);

	/**
	 * Method to find valid document based on code provided.
	 * 
	 * @param code
	 *            the document type code.
	 * @return list of valid document.
	 */
	@Query("FROM ValidDocument WHERE docTypeCode=?1 AND (isDeleted is null OR isDeleted = false)")
	List<ValidDocument> findByDocTypeCode(String code);

	/**
	 * Method to delete valid document based on document category and type codes
	 * provided.
	 * 
	 * @param deletedDateTime
	 *            the Date and time of deletion.
	 * @param docCategoryCode
	 *            the document category code.
	 * @param docTypeCode
	 *            the document type code.
	 * @param updatedBy
	 *            the caller of deletion
	 * 
	 * @return the number of rows affected.
	 */
	@Modifying
	@Query("UPDATE ValidDocument v SET v.updatedBy=?4,v.isDeleted =true , v.deletedDateTime = ?1 WHERE v.docCategoryCode =?2 and v.docTypeCode =?3 and (v.isDeleted is null or v.isDeleted =false)")
	int deleteValidDocument(LocalDateTime deletedDateTime, String docCategoryCode, String docTypeCode,
			String updatedBy);
}
