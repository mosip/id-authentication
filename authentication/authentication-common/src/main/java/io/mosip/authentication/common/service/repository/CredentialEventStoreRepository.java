package io.mosip.authentication.common.service.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.CredentialEventStore;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * The Interface CredentialEventStoreRepository.
 * @author Loganathan Sekar
 */
@Repository
public interface CredentialEventStoreRepository extends BaseRepository<CredentialEventStore, String>
{

	/**
	 * Find new or failed events.
	 *
	 * @param pageable the pageable
	 * @return the page
	 */
	@Query(value = "SELECT * from credential_event_store where status_code in ('NEW', 'FAILED')" // then, try processing old entries
			, nativeQuery = true)
	Page<CredentialEventStore> findNewOrFailedEvents(Pageable pageable);
	
	@Query(value = "SELECT  MAX(crDTimes) from CredentialEventStore where statusCode = :statusCode")
	Optional<LocalDateTime> findMaxCrDTimesByStatusCode(@Param("statusCode")String statusCode);

	Optional<CredentialEventStore> findTop1ByCredentialTransactionIdOrderByCrDTimesDesc(String requestId);
}
