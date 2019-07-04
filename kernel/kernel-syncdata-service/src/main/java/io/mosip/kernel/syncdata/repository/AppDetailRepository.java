package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.mosip.kernel.syncdata.entity.AppDetail;
import io.mosip.kernel.syncdata.entity.id.IdAndLanguageCodeID;

/**
 * Interface AppDetailRepository.
 * 
 * @author Srinivasan
 * @since 1.0.0
 * 
 */
@Repository
public interface AppDetailRepository extends JpaRepository<AppDetail, IdAndLanguageCodeID> {

	/**
	 * Find by last updated time and current time stamp.
	 *
	 * @param lastTimeUpdate   the last time update
	 * @param currentTimeStamp the current time stamp
	 * @return the list
	 */
	@Query("FROM AppDetail WHERE (createdDateTime > ?1 AND createdDateTime <=?2) OR (updatedDateTime > ?1 AND updatedDateTime<=?2)  OR (deletedDateTime > ?1 AND deletedDateTime <=?2) ")
	List<AppDetail> findByLastUpdatedTimeAndCurrentTimeStamp(LocalDateTime lastTimeUpdate,
			LocalDateTime currentTimeStamp);

}
