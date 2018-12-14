package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.BlacklistedWords;

/**
 * repository for blacklisted words
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
public interface BlacklistedWordsRepository extends BaseRepository<BlacklistedWords, String> {


	/**
	 * Method to find list of BlacklistedWords created , updated or deleted time is
	 * greater than lastUpdated timeStamp.
	 * 
	 * @param lastUpdated
	 *            timeStamp
	 * @return list of {@link BlacklistedWords}
	 */	
	@Query("FROM BlacklistedWords WHERE createdDateTime > ?1 OR updatedDateTime > ?1  OR deletedDateTime > ?1")
	List<BlacklistedWords> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);
}
