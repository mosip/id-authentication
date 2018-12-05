package io.mosip.registration.service;

import java.util.List;
import java.util.Map;

import io.mosip.registration.dto.AuthorizationDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
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
	Map<String, Object> getModesOfLogin(String authType);

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
	 * @param userId
	 *            entered userId
	 * @return AuthorizationDTO authorization details
	 */
	AuthorizationDTO getScreenAuthorizationDetails(String userId);

	/**
	 * get otp
	 * 
	 * @param key
	 *            eoUserName to generate OTP
	 * @return Response success or Error
	 */
	ResponseDTO getOTP(String key);

	/**
	 * OTP validation entered by user
	 * 
	 * @param key
	 *            is eoUsername
	 * @param otp
	 *            user entered
	 * @return Response success or error
	 */

	ResponseDTO validateOTP(String key, String otp);
	
	/**
	 *updating login params on invalid login attempts
	 * 
	 * @param registrationUserDetail
	 *            user details
	 */
	void updateLoginParams(RegistrationUserDetail registrationUserDetail);
	
	List<RegistrationUserDetail> getAllActiveUsers();
		
}


