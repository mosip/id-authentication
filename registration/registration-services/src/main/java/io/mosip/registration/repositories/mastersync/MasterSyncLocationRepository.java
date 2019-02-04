package io.mosip.registration.repositories.mastersync;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.mastersync.MasterLocation;
/**
 * This interface is JPA repository class which interacts with database and does the CRUD function. It is 
 * extended from {@link BaseRepository}
 * @author Sreekar Chukka
 *
 */
public interface MasterSyncLocationRepository extends BaseRepository<MasterLocation, String> {
	
	
	/**
	 * Find master location by hierarchy name and language code.
	 *
	 * @param hierarchyName the hierarchy name
	 * @param langCode the lang code
	 * @return the list
	 */
	List<MasterLocation> findMasterLocationByHierarchyNameAndLangCode(String hierarchyName , String langCode);
	
	/**
	 * Find master location by parent loc code.
	 *
	 * @param parentLocCode the parent loc code
	 * @return the list
	 */
	List<MasterLocation> findMasterLocationByParentLocCodeAndLangCode(String parentLocCode,String langCode);

}
