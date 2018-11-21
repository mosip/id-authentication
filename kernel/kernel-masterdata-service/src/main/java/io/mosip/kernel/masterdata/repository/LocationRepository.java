package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.Location;
/**
 * This interface is JPA repository class which interacts with database and does the CRUD function. It is 
 * extended from {@link BaseRepository}
 * @author Srinivasan
 *
 */
@Repository
public interface LocationRepository extends BaseRepository<Location, String> {
	
	List<Location> findLocationHierarchyByIsActiveTrueAndIsDeletedFalse();

	List<Location> findLocationHierarchyByCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse(String locCode,String languagecode);
	
	List<Location> findLocationHierarchyByParentLocCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse(String parentLocCode,
			String languageCode);

}
