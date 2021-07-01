package io.mosip.authentication.common.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.CredentialEventStore;
import io.mosip.authentication.common.service.entity.FailedMessageEntity;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

@Repository
public interface FailedMessagesRepo extends BaseRepository<FailedMessageEntity, String>{
	
	@Query(value = "SELECT * from failed_messages_store where status_code in ('NEW')" // then, try processing old entries
			, nativeQuery = true)
	Page<CredentialEventStore> findNewFailedMessages(Pageable pageable);

}
