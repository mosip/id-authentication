package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.Gender;

/**
 * This class is used to fetch the gender details from {@link Gender} table.
 * 
 * @author Brahmananda Reddy
 *
 */
public interface GenderDAO {
	
	/**
	 * This method is used to fetch the gender details.
	 * 
	 * @return the list of gender
	 */

	List<Gender> getGenders();

}
