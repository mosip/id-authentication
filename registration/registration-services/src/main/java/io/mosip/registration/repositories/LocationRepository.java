package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.Location;

/**
 * Location repository
 * 
 * @author Brahmananda Reddy
 *
 */

public interface LocationRepository extends BaseRepository<Location, String> {
	/**
	 * Find master location by hierarchy name and language code.
	 *
	 * @param hierarchyName the hierarchy name
	 * @param langCode      the lang code
	 * @return the list
	 */
	List<Location> findByIsActiveTrueAndHierarchyNameAndLangCode(String hierarchyName, String langCode);

	/**
	 * Find master location by parent loc code.
	 *
	 * @param parentLocCode the parent loc code
	 * @param langCode the lang code
	 * @return the list
	 */
	List<Location> findByIsActiveTrueAndParentLocCodeAndLangCode(String parentLocCode, String langCode);

}
