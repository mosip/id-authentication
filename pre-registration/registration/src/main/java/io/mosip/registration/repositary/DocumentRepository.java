package io.mosip.registration.repositary;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.entity.DocumentEntity;

/**
 * Document Repository
 * 
 * @author M1043008
 *
 */
@Repository("documentRepositoery")
@Transactional
public interface DocumentRepository extends BaseRepository<DocumentEntity, String> {

	public void deleteAllByPreregId(String preregId);

	public boolean existsByPreregId(String preregId);

	List<DocumentEntity> findBypreregId(String preId);
}
