package io.mosip.preregistration.batchjobservices.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.batchjobservices.entity.DocumentEntity;

/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Repository("documentRespository")
public interface DocumentRespository extends BaseRepository<DocumentEntity, String> {
	
	/**
	 * @param preregId
	 * @return document entity based on given preId
	 */
	public List<DocumentEntity> findBypreregId(String preregId);

}
