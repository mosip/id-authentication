package io.mosip.kernel.synchandler.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.synchandler.entity.Gender;

/**
 * Repository class for fetching gender data
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Repository
public interface GenderRepository extends BaseRepository<Gender, String> {

	List<Gender> findGenderByLangCodeAndIsDeletedFalse(String langCode);

	@Query("FROM Gender WHERE createdDateTime > ?1 OR updatedDateTime > ?1  OR deletedDateTime > ?1")
	List<Gender> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);
}
