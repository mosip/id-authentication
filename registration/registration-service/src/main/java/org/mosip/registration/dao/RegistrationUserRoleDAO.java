package org.mosip.registration.dao;

import java.util.List;

/**
 * DAO class for RegistrationUserRole
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface RegistrationUserRoleDAO {
	
	/**
	 * This method is used to get the Registration User Roles
	 * 
	 * @return List of roles
	 */
	
	public List<String> getRoles(String userId);
	
}
