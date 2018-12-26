package io.mosip.kernel.masterdata.repository;

import java.util.List;

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
	List<ValidDocument> findByDocCategoryCode(String code);

	/**
	 * Method to find valid document based on code provided.
	 * 
	 * @param code
	 *            the document type code.
	 * @return list of valid document.
	 */
	List<ValidDocument> findByDocTypeCode(String code);
}
