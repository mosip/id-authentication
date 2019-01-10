package io.mosip.registration.service;

import java.util.List;

import io.mosip.registration.dto.AuthorizationDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.entity.RegistrationUserDetail;

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
	 * @return Map of login modes along with sequence
	 */
	List<String> getModesOfLogin(String authType);

	/**
	 * fetching user details
	 * 
	 * @param userId
	 *            entered userId
	 * @return RegistrationUserDetail
	 */
	RegistrationUserDetail getUserDetail(String userId);
	
	/**
	 * fetching registration center details
	 * 
	 * @param centerId
	 *            centerId corresponding to entered userId
	 * @return RegistrationCenterDetailDTO center details
	 */
	RegistrationCenterDetailDTO getRegistrationCenterDetails(String centerId);

	/**
	 * fetching registration screen authorization details
	 * 
	 * @param roleCode
	 *            list of roles
	 * @return AuthorizationDTO authorization details
	 */
	AuthorizationDTO getScreenAuthorizationDetails(List<String> roleCode);
	
	/**
	 *updating login params on invalid login attempts
	 * 
	 * @param registrationUserDetail
	 *            user details
	 */
	void updateLoginParams(RegistrationUserDetail registrationUserDetail);

}


