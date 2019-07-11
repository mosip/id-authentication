package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.mosip.kernel.syncdata.entity.IndividualType;
import io.mosip.kernel.syncdata.entity.id.CodeAndLanguageCodeID;

/**
 * Repository class to handle CRUD operations for
 * {@link IndividualTypeRepository}}
 * 
 * @author Srinivasan
 *
 */
@Repository
public interface IndividualTypeRepository extends JpaRepository<IndividualType, CodeAndLanguageCodeID> {

	/**
	 * 
	 * @param lastUpdatedTime - last updated time stamp
	 * @param currentTime     - currentTimestamp
	 * @return list of {@link IndividualType} - list of individual types
	 */
	@Query("FROM IndividualType it WHERE (createdDateTime > ?1 AND createdDateTime <=?2) OR (updatedDateTime > ?1 AND updatedDateTime<=?2)  OR (deletedDateTime > ?1 AND deletedDateTime <=?2)")
	public List<IndividualType> findAllIndvidualTypeByTimeStamp(LocalDateTime lastUpdatedTime,
			LocalDateTime currentTime);
}
