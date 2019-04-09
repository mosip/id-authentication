package io.mosip.registration.dao;

import java.util.List;
import java.util.Set;

/**
 * DAO class for AppAuthentication
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface AppAuthenticationDAO {

	/**
	 * This method is used to get the Login Mode
	 * 
	 * @param authType
	 *            authentication type
	 * @param roleList
	 *            list of roles
	 * 
	 * @return Map of Login modes along with the sequence
	 */
	List<String> getModesOfLogin(String authType, Set<String> roleList);

}
