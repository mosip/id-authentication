package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.Location;

/**
 * This interface is JPA repository class which interacts with database and does
 * the CRUD function. It is extended from {@link BaseRepository}
 * 
 * @author Srinivasan
 *
 */
@Repository
public interface LocationRepository extends BaseRepository<Location, String> {

	List<Location> findLocationHierarchyByIsDeletedFalse();

	List<Location> findLocationHierarchyByCodeAndLanguageCodeAndIsDeletedFalse(String locCode, String languagecode);

	List<Location> findLocationHierarchyByParentLocCodeAndLanguageCodeAndIsDeletedFalse(String parentLocCode,
			String languageCode);

	@Query(value = "select distinct hierarchy_level,hierarchy_level_name,is_active from master.location where lang_code=?1 and is_deleted='f'", nativeQuery = true)
	List<Object[]> findDistinctLocationHierarchyByIsDeletedFalse(String langCode);

	@Query("FROM Location WHERE createdDateTime > ?1 OR updatedDateTime > ?1  OR deletedDateTime > ?1")
	List<Location> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);

}
