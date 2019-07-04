package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.mosip.kernel.syncdata.entity.ProcessList;
import io.mosip.kernel.syncdata.entity.id.IdAndLanguageCodeID;

/**
 * ProcessListRepository.
 * 
 * @author Srinivasan
 * @since 1.0.0
 */
@Repository
public interface ProcessListRepository extends JpaRepository<ProcessList, IdAndLanguageCodeID> {

	/**
	 * Find by last updated time and current time stamp.
	 *
	 * @param lastUpdatedTime  the last updated time
	 * @param currentTimeStamp the current time stamp
	 * @return {@link ProcessList} list of ProcessList
	 */
	@Query("FROM ProcessList WHERE (createdDateTime > ?1 AND createdDateTime <=?2) OR (updatedDateTime > ?1 AND updatedDateTime<=?2)  OR (deletedDateTime > ?1 AND deletedDateTime <=?2) ")
	List<ProcessList> findByLastUpdatedTimeAndCurrentTimeStamp(LocalDateTime lastUpdatedTime,
			LocalDateTime currentTimeStamp);
}
