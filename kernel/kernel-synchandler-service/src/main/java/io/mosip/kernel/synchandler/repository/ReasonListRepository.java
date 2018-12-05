package io.mosip.kernel.synchandler.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.synchandler.entity.ReasonList;

/**
 * 
 * @author Srinivasan
 *
 */
public interface ReasonListRepository extends BaseRepository<ReasonList, String> {
	@Query("FROM ReasonList WHERE createdDateTime > ?1 OR updatedDateTime > ?1  OR deletedDateTime > ?1")
	List<ReasonList> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);
}
