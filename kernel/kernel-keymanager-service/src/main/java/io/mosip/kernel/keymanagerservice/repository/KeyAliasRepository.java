package io.mosip.kernel.keymanagerservice.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.keymanagerservice.entity.KeyAlias;

/**
 * This interface extends BaseRepository which provides with the methods for
 * several CRUD operations.
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Repository
public interface KeyAliasRepository extends BaseRepository<KeyAlias, String> {

	List<KeyAlias> findByApplicationId(String applicationId);

	List<KeyAlias> findByApplicationIdAndReferenceId(String applicationId, String referenceId);

	List<KeyAlias> findByApplicationIdAndReferenceIdAndKeyGenerationTimeLessThanEqual(String applicationId,
			String referenceId, LocalDateTime keyGenerationTime);
}
