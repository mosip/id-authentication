package io.mosip.registration.dao;

import java.util.List;
import java.util.Set;

import io.mosip.registration.entity.AppAuthenticationMethod;

/**
 * This class is used to fetch the authentication related information from 
 * the {@link AppAuthenticationMethod}. It fetches different modes of authentication based on
 * authentication type and roles of user.
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface AppAuthenticationDAO {

	/**
	 * This method will fetch the list of authentication modes based on the
	 * authentication type and set of roles
	 * 
	 * @param authType
	 *            {@code String} authentication type [Login auth, Packet auth, Exception auth, EOD auth, Onboard auth]
	 * @param roleList
	 *            {@code List} list of user roles[Registration Officer, Registration supervisor, Registration Admin]
	 * 
	 * @return List of login modes
	 */
	List<String> getModesOfLogin(String authType, Set<String> roleList);

}
