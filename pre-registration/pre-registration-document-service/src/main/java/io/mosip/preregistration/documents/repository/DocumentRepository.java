/* 
 * 
 * Copyright
 */
package io.mosip.preregistration.documents.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.documents.entity.DocumentEntity;

/**
 * This repository interface is used to define the JPA methods for Document
 * service.
 * 
 * @author Rajath KR
 * @author Tapaswini Bahera
 * @author Jagadishwari S
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */
@Repository("documentRepository")
@Transactional
public interface DocumentRepository extends BaseRepository<DocumentEntity, String> {
	/**
	 * @param preregId
	 * @return true or false for a preId
	 */
	public boolean existsByPreregId(String preregId);

	/**
	 * @param preId
	 * @return all the documents for a preId
	 */
	List<DocumentEntity> findBypreregId(String preId);

	/**
	 * @param documentId
	 * @return the document for a document Id
	 */
	DocumentEntity findBydocumentId(int documentId);

	/**
	 * @param preId
	 * @param catCode
	 * @return the document for a preId and Document category
	 */
	DocumentEntity findSingleDocument(@Param("preId") String preId, @Param("catCode") String catCode);

	/**
	 * @param documentId
	 * @return the number of records deleted for a document Id
	 */
	public int deleteAllBydocumentId(Integer documentId);

	/**
	 * @param preregId
	 * @return the number of records deleted for a pre Id
	 */
	public int deleteAllBypreregId(String preregId);

}
