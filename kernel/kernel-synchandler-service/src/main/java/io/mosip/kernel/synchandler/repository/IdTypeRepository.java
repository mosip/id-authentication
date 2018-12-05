
package io.mosip.kernel.synchandler.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.synchandler.entity.IdType;

/**
 * Interface for idtype repository.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public interface IdTypeRepository extends BaseRepository<IdType, String> {
	/**
	 * Method that returns the list of id types for the specific language code.
	 * 
	 * @param languageCode
	 *            the language code.
	 * @return the list of id types.
	 */
	List<IdType> findByLangCodeAndIsDeletedFalse(String languageCode);

	@Query("FROM IdType WHERE createdDateTime > ?1 OR updatedDateTime > ?1  OR deletedDateTime > ?1")
	List<IdType> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);
}
