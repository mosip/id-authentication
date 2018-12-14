package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.Language;

/**
 * Repository to perform CRUD operations on Language.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 * @see Language
 * @see BaseRepository
 *
 */
@Repository
public interface LanguageRepository extends BaseRepository<Language, String> {

	/**
	 * Method to find list of Language created , updated or deleted time is
	 * greater than lastUpdated timeStamp.
	 * 
	 * @param lastUpdated
	 *            timeStamp
	 * @return list of {@link Language}
	 */
	@Query("FROM Language WHERE createdDateTime > ?1 OR updatedDateTime > ?1  OR deletedDateTime > ?1")
	List<Language> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);
}
