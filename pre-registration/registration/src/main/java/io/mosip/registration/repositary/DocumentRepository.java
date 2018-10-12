package io.mosip.registration.repositary;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.entity.DocumentEntity;



@Repository("documentRepositoery")
public interface DocumentRepository extends BaseRepository<DocumentEntity, String> {

	
	List<DocumentEntity> findBypreregId(String preId);
}
