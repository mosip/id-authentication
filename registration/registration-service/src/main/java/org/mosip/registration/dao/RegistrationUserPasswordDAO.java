package org.mosip.registration.dao;

/**
 * DAO class for RegistrationUserPassword
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface RegistrationUserPasswordDAO {
	
	/**
	 * This method is used to get the User credentials
	 * 
	 * @return String 
	 */
	
	public String getPassword(String userId, String hashPassword);

}
