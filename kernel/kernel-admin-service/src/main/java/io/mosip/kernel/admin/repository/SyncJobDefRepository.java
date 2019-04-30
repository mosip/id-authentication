package io.mosip.kernel.admin.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.admin.entity.SyncJobDef;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;


/**
 * The SyncJobDefRepository which handles CRUD operation for {@link SyncJobDef}.
 */
@Repository
public interface SyncJobDefRepository extends BaseRepository<SyncJobDef, String> {

	
	/**
	 * Find latest by last updated time and current time stamp.
	 *
	 * @param lastUpdatedTime -the last updated time
	 * @param currentTimeStamp -the current time stamp
	 * @return the list of {@link SyncJobDef}
	 */
	@Query("FROM SyncJobDef WHERE (createdDateTime > ?1 AND createdDateTime <=?2) OR (updatedDateTime > ?1 AND updatedDateTime<=?2)  OR (deletedDateTime > ?1 AND deletedDateTime <=?2)")
	List<SyncJobDef> findLatestByLastUpdatedTimeAndCurrentTimeStamp(LocalDateTime lastUpdatedTime,LocalDateTime currentTimeStamp);
}
