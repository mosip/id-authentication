package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.mosip.kernel.syncdata.entity.BlacklistedWords;

/**
 * repository for blacklisted words
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
public interface BlacklistedWordsRepository extends JpaRepository<BlacklistedWords, String> {

	/**
	 * Method to find list of BlacklistedWords created , updated or deleted time is
	 * greater than lastUpdated timeStamp.
	 * 
	 * @param lastUpdated      timeStamp - last updated time stamp
	 * @param currentTimeStamp - currentTimestamp
	 * @return list of {@link BlacklistedWords} - list of blacklisted words
	 */
	@Query("FROM BlacklistedWords WHERE (createdDateTime > ?1 AND createdDateTime <=?2) OR (updatedDateTime > ?1 AND updatedDateTime <=?2)  OR (deletedDateTime > ?1 AND deletedDateTime <=?2)")
	List<BlacklistedWords> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated, LocalDateTime currentTimeStamp);
}
