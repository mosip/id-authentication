package io.mosip.authentication.common.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
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
	@Query(value = "SELECT * from credential_event_store where status_code in ('NEW', 'FAILED')", nativeQuery = true)
	Page<CredentialEventStore> findNewOrFailedEvents(Pageable pageable);
}
