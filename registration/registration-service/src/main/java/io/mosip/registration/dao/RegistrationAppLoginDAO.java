package io.mosip.registration.dao;

import java.util.Map;

/**
 * DAO class for RegistrationAppLogin
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface RegistrationAppLoginDAO {

	/**
	 * This method is used to get the Login Mode
	 * 
	 * @return Map of Login modes along with the sequence
	 */
	public Map<String,Object> getModesOfLogin();
	
}

