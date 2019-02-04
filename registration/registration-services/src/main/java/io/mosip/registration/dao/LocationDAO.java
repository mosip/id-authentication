package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.Location;

/**
 * 
 * @author Brahmananda Reddy
 *
 */
public interface LocationDAO {
	/**
	 * This method fetches the locations
	 * 
	 * @return the {@link List} of locations
	 */

	List<Location> getLocations();

}
