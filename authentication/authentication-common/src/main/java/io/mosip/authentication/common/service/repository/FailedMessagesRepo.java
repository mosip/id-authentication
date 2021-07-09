package io.mosip.authentication.common.service.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.FailedMessageEntity;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * The Interface FailedMessagesRepo.
 * 
 * @author Loganathan Sekar
 */
@Repository
public interface FailedMessagesRepo extends BaseRepository<FailedMessageEntity, String>{
	
	/**
	 * Find new failed messages.
	 *
	 * @param pageable the pageable
	 * @return the page
	 */
	@Query(value = "SELECT * from failed_message_store where status_code in ('NEW', 'FAILED')" // then, try processing old entries
			, nativeQuery = true)
	Page<FailedMessageEntity> findNewFailedMessages(Pageable pageable);

	/**
	 * Find first by topic and crDtimes greater than and status code order by publishedOnDtimes desc.
	 *
	 * @param topic the topic
	 * @param minDTime the min D time
	 * @param statusCode the status code
	 * @return the optional
	 */
	Optional<FailedMessageEntity> findFirstByTopicAndCrDTimesGreaterThanAndStatusCodeOrderByPublishedOnDtimesDesc(String topic, LocalDateTime minDTime, String statusCode);

}
