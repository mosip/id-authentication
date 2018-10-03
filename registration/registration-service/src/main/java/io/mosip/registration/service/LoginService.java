package io.mosip.registration.service;

import java.util.List;
import java.util.Map;

import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;

public interface LoginService {

	Map<String, Object> getModesOfLogin();

	boolean validateUserPassword(String userId, String hashPassword);

	Map<String, String> getUserDetail(String userId);

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
