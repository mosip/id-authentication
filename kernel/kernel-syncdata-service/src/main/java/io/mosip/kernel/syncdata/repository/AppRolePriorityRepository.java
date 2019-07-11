package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.mosip.kernel.syncdata.entity.AppRolePriority;
import io.mosip.kernel.syncdata.entity.id.IdAndLanguageCodeID;

/**
 * AppRolePriorityRepository.
 *
 * @author Srinivasan
 * @since 1.0.0
 */
@Repository
public interface AppRolePriorityRepository extends JpaRepository<AppRolePriority, IdAndLanguageCodeID> {

	/**
	 * Find by last updated and current time stamp.
	 *
	 * @param lastUpdatedTime  the last updated time
	 * @param currentTimeStamp the current time stamp
	 * @return {@link AppRolePriority}
	 */
	@Query("FROM AppRolePriority WHERE (createdDateTime > ?1 AND createdDateTime <=?2) OR (updatedDateTime > ?1 AND updatedDateTime<=?2)  OR (deletedDateTime > ?1 AND deletedDateTime <=?2)")
	List<AppRolePriority> findByLastUpdatedAndCurrentTimeStamp(LocalDateTime lastUpdatedTime,
			LocalDateTime currentTimeStamp);
}
