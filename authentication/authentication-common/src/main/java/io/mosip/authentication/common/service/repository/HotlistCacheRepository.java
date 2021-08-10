package io.mosip.authentication.common.service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.HotlistCache;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * The Interface HotlistCacheRepository.
 * 
 * @author Manoj SP
 */
@Repository
public interface HotlistCacheRepository extends BaseRepository<HotlistCache, String> {
	
	Optional<HotlistCache> findByIdHashAndIdType(String idHash, String idType);

	/**
	 * Find by status.
	 *
	 * @param status the status
	 * @param isDeleted the is deleted
	 * @return the list
	 */
	List<HotlistCache> findByStatusAndExpiryDTimes(String status, LocalDateTime expiryDTimes);

	/**
	 * Find by expiry timestamp less than current timestamp.
	 *
	 * @param currentTimestamp the current timestamp
	 * @param isDeleted the is deleted
	 * @return the list
	 */
	List<HotlistCache> findByExpiryDTimesLessThanAndStatus(LocalDateTime currentTimestamp, String status);
	
}
