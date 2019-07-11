/**
 * 
 */
package io.mosip.kernel.auth.repository;

import java.util.List;

import io.mosip.kernel.auth.dto.AuthZResponseDto;
import io.mosip.kernel.auth.dto.ClientSecret;
import io.mosip.kernel.auth.dto.LoginUser;
import io.mosip.kernel.auth.dto.MosipUserDto;
import io.mosip.kernel.auth.dto.MosipUserListDto;
import io.mosip.kernel.auth.dto.MosipUserSaltListDto;
import io.mosip.kernel.auth.dto.PasswordDto;
import io.mosip.kernel.auth.dto.RIdDto;
import io.mosip.kernel.auth.dto.RolesListDto;
import io.mosip.kernel.auth.dto.UserDetailsResponseDto;
import io.mosip.kernel.auth.dto.UserNameDto;
import io.mosip.kernel.auth.dto.UserOtp;
import io.mosip.kernel.auth.dto.UserPasswordRequestDto;
import io.mosip.kernel.auth.dto.UserPasswordResponseDto;
import io.mosip.kernel.auth.dto.UserRegistrationRequestDto;
import io.mosip.kernel.auth.dto.UserRegistrationResponseDto;
import io.mosip.kernel.auth.dto.ValidationResponseDto;
import io.mosip.kernel.auth.dto.otp.OtpUser;

/**
 * @author Ramadurai Pandian
 *
 */
public interface DataStore  {

	public RolesListDto getAllRoles();

	public MosipUserListDto getListOfUsersDetails(List<String> userDetails) throws Exception;

	public MosipUserSaltListDto getAllUserDetailsWithSalt() throws Exception;

	public RIdDto getRidFromUserId(String userId) throws Exception;
	
	public AuthZResponseDto unBlockAccount(String userId) throws Exception;
	
	public UserRegistrationResponseDto registerUser(UserRegistrationRequestDto userId) ;

	public UserPasswordResponseDto addPassword(UserPasswordRequestDto userPasswordRequestDto);
	
	public AuthZResponseDto changePassword(PasswordDto passwordDto) throws Exception;
	
	public AuthZResponseDto resetPassword(PasswordDto passwordDto ) throws Exception;
	
	public UserNameDto getUserNameBasedOnMobileNumber(String mobileNumber) throws Exception;
	
	public MosipUserDto authenticateUser(LoginUser loginUser) throws Exception;

	public MosipUserDto authenticateWithOtp(OtpUser otpUser) throws Exception;

	public MosipUserDto authenticateUserWithOtp(UserOtp loginUser) throws Exception;

	public MosipUserDto authenticateWithSecretKey(ClientSecret clientSecret) throws Exception;
	
	public MosipUserDto getUserRoleByUserId(String username)throws Exception;
	
	public MosipUserDto getUserDetailBasedonMobileNumber(String mobileNumber) throws Exception;
	
	public ValidationResponseDto validateUserName(String userId);
	
	public UserDetailsResponseDto getUserDetailBasedOnUid(List<String> userIds);
	

}
