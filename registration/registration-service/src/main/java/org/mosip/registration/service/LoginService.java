package org.mosip.registration.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mosip.registration.dto.RegistrationCenterDetailDTO;
import org.mosip.registration.dto.ResponseDTO;

public interface LoginService {

	Map<String, Object> getModesOfLogin();

	boolean validateUserPassword(String userId, String hashPassword);

	HashMap<String, String> getUserDetail(String userId);

	String getCenterName(String centerId);

	RegistrationCenterDetailDTO getRegistrationCenterDetails(String centerId);

	List<String> getRoles(String userId);

	/**
	 * get otp
	 * 
	 * @param key
	 *            eoUserName to generate OTP
	 * @return Response success or Error
	 * @throws RegBaseCheckedException
	 *             generalised exception with error code and error message
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
	 * @throws RegBaseCheckedException
	 *             generalised exception with error code and error message
	 */
	ResponseDTO validateOTP(String key, String otp);

}
