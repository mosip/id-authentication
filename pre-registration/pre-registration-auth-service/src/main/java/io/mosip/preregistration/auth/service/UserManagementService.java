package io.mosip.preregistration.auth.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import io.mosip.preregistration.auth.constants.StatusCodes;
import io.mosip.preregistration.auth.dto.UserDto;
import io.mosip.preregistration.auth.exceptions.UserNameNotValidException;

/**
 * @author Shashank Agrawal
 *
 */
@Service
public interface UserManagementService {

	/**
	 * 
	 * This method will generate the otp that will be sent to the respected user.
	 * 
	 * @param userName
	 * @return
	 */
	public Map<String, StatusCodes> userLogin(String userName);

	/**
	 * 
	 * This method will validate the otp that will be entered by the user with the
	 * respect to the otp present coreesponding to the user's device
	 * 
	 * @param userName
	 * @param otp
	 * @return
	 */
	public Map<String, StatusCodes> userValidation(String userName, String otp);

	/**
	 * This method will update the user data after validating the otp entered by the
	 * user.
	 * 
	 * @param userName
	 * @param userDto
	 * @return
	 * @throws UserNameNotValidException
	 */
	public Map<String, StatusCodes> userUpdation(String userName, UserDto userDto) throws UserNameNotValidException;
}
