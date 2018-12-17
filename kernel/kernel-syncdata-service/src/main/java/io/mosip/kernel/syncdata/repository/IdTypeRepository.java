
package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.IdType;

/**
 * Interface for idtype repository.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public interface IdTypeRepository extends BaseRepository<IdType, String> {
	/**
	 * Method to find list of IdType created , updated or deleted time is
	 * greater than lastUpdated timeStamp.
	 * 
	 * @param lastUpdated
	 *            timeStamp
	 * @return list of {@link IdType}
	 */
	@Query("FROM IdType WHERE createdDateTime > ?1 OR updatedDateTime > ?1  OR deletedDateTime > ?1")
	List<IdType> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);
}
