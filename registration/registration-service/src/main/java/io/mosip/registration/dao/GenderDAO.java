package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.Gender;

/**
 * 
 * @author Brahmananda Reddy
 *
 */
public interface GenderDAO {
	/**
	 * method to fetch the genders
	 * 
	 * @return the{@link List} of gender
	 */

	List<Gender> getGenders();

}
