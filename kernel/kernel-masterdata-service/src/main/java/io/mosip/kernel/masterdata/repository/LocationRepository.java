package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
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
	
	List<Location> findLocationHierarchyByIsDeletedIsNullOrIsDeletedFalse();
	@Query(value="FROM Location l where l.code=?1 and l.languageCode=?2 and (l.isDeleted is null or l.isDeleted=false)")
	List<Location> findLocationHierarchyByCodeAndLanguageCode(String locCode,String languagecode);
	@Query(value="FROM Location l where parentLocCode=?1 and languageCode=?2 and (l.isDeleted is null or l.isDeleted=false)")
	List<Location> findLocationHierarchyByParentLocCodeAndLanguageCode(String parentLocCode,
			String languageCode);
	@Query(value="select distinct hierarchy_level,hierarchy_level_name,is_active from master.location where lang_code=?1 and is_deleted=(null or 'f')",nativeQuery=true)
	List<Object[]> findDistinctLocationHierarchyByIsDeletedFalse(String langCode);

}
