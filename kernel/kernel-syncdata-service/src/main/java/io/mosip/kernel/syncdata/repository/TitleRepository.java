package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.Title;

/**
 * Repository class for fetching titles from master db
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Repository
public interface TitleRepository extends BaseRepository<Title, String> {
	/**
	 * Method to find list of Title created , updated or deleted time is greater
	 * than lastUpdated timeStamp.
	 * 
	 * @param lastUpdated
	 *            timeStamp
	 * @return list of {@link Title}
	 */
	@Query("FROM Title WHERE createdDateTime > ?1 OR updatedDateTime > ?1  OR deletedDateTime > ?1")
	List<Title> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);
}
