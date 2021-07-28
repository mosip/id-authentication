package io.mosip.authentication.common.service.repository;

import java.time.LocalDateTime;
import java.util.List;
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
	
	/**
	 * Find max cr D times by status code.
	 *
	 * @param statusCode the status code
	 * @return the optional
	 */
	@Query(value = "SELECT  MAX(crDTimes) from CredentialEventStore where statusCode = :statusCode")
	Optional<LocalDateTime> findMaxCrDTimesByStatusCode(@Param("statusCode")String statusCode);

	/**
	 * Find top 1 by credential transaction id order by cr D times desc.
	 *
	 * @param requestId the request id
	 * @return the optional
	 */
	Optional<CredentialEventStore> findTop1ByCredentialTransactionIdOrderByCrDTimesDesc(String requestId);
	
	/**
	 * Find credential transaction id by credential transaction id order by cr D times desc.
	 *
	 * @param requestIds the request ids
	 * @return the list
	 */
	@Query(value = "SELECT DISTINCT(credentialTransactionId) FROM CredentialEventStore WHERE credentialTransactionId IN (:requestIds)")
	List<String> findDistictCredentialTransactionIdsInList(@Param("requestIds") List<String> requestIds);
}
