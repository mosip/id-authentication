/* 
 * 
 * Copyright
 */
package io.mosip.preregistration.documents.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.s3.model.AmazonS3Exception;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.PacketNotFoundException;

/**
 * This repository interface is used to define the JPA methods for Document
 * service.
 * 
 * @author Rajath KR
 * @author Tapaswini Behera
 * @author Jagadishwari S
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */
@Repository("documentRepository")
@Transactional(rollbackOn= {Exception.class})
public interface DocumentRepository extends BaseRepository<DocumentEntity, String> {
	/**
	 * @param preregId
	 *            pass preRegistrationId
	 * @return true or false for a preRegistrationId
	 */
	public boolean existsByPreregId(String preregId);

	/**
	 * @param preId
	 *            pass preRegistrationId
	 * @return all the documents for a preRegistrationId
	 */
	List<DocumentEntity> findBypreregId(String preId);

	/**
	 * @param documentId
	 *            pass documentId
	 * @return the document for a document Id
	 */
	DocumentEntity findBydocumentId(String documentId);

	/**
	 * @param preId
	 *            pass preRegistrationId
	 * @param catCode
	 *            pass category code
	 * @return the document for a preId and Document category
	 */
	DocumentEntity findSingleDocument(@Param("preId") String preId, @Param("catCode") String catCode);

	/**
	 * @param documentId
	 *            pass documentId
	 * @return the number of records deleted for a document Id
	 */
	public int deleteAllBydocumentId(String documentId);

	/**
	 * @param preregId
	 *            pass preRegistrationId
	 * @return the number of records deleted for a preRegistrationId
	 */
	public int deleteAllBypreregId(String preregId);

}
