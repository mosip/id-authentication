package io.mosip.registration.dao;

import java.util.Map;

/**
 * DAO class for RegistrationUserDetail
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface RegistrationUserDetailDAO {
	
	/**
	 * This method is used to get the User Details
	 * 
	 * @return Map of User deatils
	 */
	
	public Map<String,String> getUserDetail(String userId);
	
	/**
	 * This method is used to check the user status
	 * 
	 * @return String user status
	 */
	
	public String getUserStatus(String userId);

}

