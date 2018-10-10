package io.mosip.registration.dao;

/**
 * DAO class for RegistrationUserPassword
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface RegistrationUserPasswordDAO {
	
	/**
	 * This method is used to get and validate the User credentials
	 * 
	 * @return boolean 
	 */
	
	public boolean getPassword(String userId, String hashPassword);

}
