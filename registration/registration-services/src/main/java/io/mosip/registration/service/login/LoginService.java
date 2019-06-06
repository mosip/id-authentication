package io.mosip.registration.service.login;

import java.util.List;
import java.util.Set;

import io.mosip.registration.dto.AuthorizationDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.UserDTO;

/**
 * Service Class for Login
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface LoginService {

	/**
	 * get login modes
	 * 
	 * @param authType
	 *            authentication type
	 * @param roleList
	 *            list of user roles
	 * 
	 * @return Map of login modes along with sequence
	 */
	List<String> getModesOfLogin(String authType, Set<String> roleList);

	/**
	 * fetching user details
	 * 
	 * @param userId
	 *            entered userId
	 * @return UserDTO
	 */
	UserDTO getUserDetail(String userId);

	/**
	 * fetching registration center details
	 * 
	 * @param centerId
	 *            centerId corresponding to entered userId
	 * @param langCode
	 *            language code
	 * @return RegistrationCenterDetailDTO center details
	 */
	RegistrationCenterDetailDTO getRegistrationCenterDetails(String centerId, String langCode);

	/**
	 * fetching registration screen authorization details
	 * 
	 * @param roleCode
	 *            list of roles
	 * @return AuthorizationDTO authorization details
	 */
	AuthorizationDTO getScreenAuthorizationDetails(List<String> roleCode);

	/**
	 * updating login params on invalid login attempts
	 * 
	 * @param userDTO
	 *            user dto
	 */
	void updateLoginParams(UserDTO userDTO);
	
	/**
	 * Execute initial sync
	 * 
	 * @return List of sync results
	 */
	List<String> initialSync();
	
	
	/**
	 * Validating login attempts
	 * 
	 * @param userDTO
	 *            user details
	 * @param errorMessage
	 *            error message
	 * @param invalidLoginCount
	 *            invalid login count
	 * @param invalidLoginTime
	 *            invalid login time
	 */
	String validateInvalidLogin(UserDTO userDTO, String errorMessage, int invalidLoginCount, int invalidLoginTime);
	
	/**
	 * Validating user
	 * 
	 * @param userId
	 *            userid
	 */
	ResponseDTO validateUser(String userId);

}
