package io.mosip.authentication.common.service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.HotlistCache;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import org.springframework.cache.annotation.Cacheable;

/**
 * The Interface HotlistCacheRepository.
 * 
 * @author Manoj SP
 */
@Repository
public interface HotlistCacheRepository extends BaseRepository<HotlistCache, String> {

	/**
	 * Fetch a hotlist record by IdHash and IdType.
	 * Cached to reduce DB hits for frequently requested values.
	 */
	@Cacheable(value = "hotlistCache", key = "#idHash + ':' + #idType")
	@QueryHints(@QueryHint(name = "jakarta.persistence.query.readOnly", value = "true"))
	@Query("SELECT h FROM HotlistCache h WHERE h.idHash = :idHash AND h.idType = :idType")
	Optional<HotlistCache> findByIdHashAndIdType(@Param("idHash") String idHash, @Param("idType") String idType);

	/**
	 * Find by status.
	 *
	 * @param status the status
	 * @param expiryDTimes the expirydtimes
	 * @return the list
	 */
	@QueryHints(@QueryHint(name = "jakarta.persistence.query.readOnly", value = "true"))
	@Query("SELECT h FROM HotlistCache h WHERE h.status = :status AND h.expiryDTimes = :expiryDTimes")
	List<HotlistCache> findByStatusAndExpiryDTimes(@Param("status") String status,
												   @Param("expiryDTimes") LocalDateTime expiryDTimes);

	/**
	 * Find by expiry timestamp less than current timestamp.
	 *
	 * @param currentTimestamp the current timestamp
	 * @param status the status
	 * @return the list
	 */
	@QueryHints(@QueryHint(name = "jakarta.persistence.query.readOnly", value = "true"))
	@Query("SELECT h FROM HotlistCache h WHERE h.expiryDTimes < :currentTimestamp AND h.status = :status")
	List<HotlistCache> findByExpiryDTimesLessThanAndStatus(@Param("currentTimestamp") LocalDateTime currentTimestamp,
														   @Param("status") String status);

    /**
     * Batch fetch for multiple IdHashes to avoid N+1 queries.
     */
    @Cacheable(value = "hotlistCacheBatch", key = "#idType + ':' + #idHashes.hashCode()")
    @QueryHints(@QueryHint(name = "jakarta.persistence.query.readOnly", value = "true"))
    @Query("SELECT h FROM HotlistCache h WHERE h.idHash IN :idHashes AND h.idType = :idType")
    List<HotlistCache> findAllByIdHashInAndIdType(@Param("idHashes") List<String> idHashes,
                                                  @Param("idType") String idType);
	
}
