/**
 * 
 */
package io.mosip.kernel.auth.service;

import java.util.List;

import io.mosip.kernel.auth.dto.AuthNResponse;
import io.mosip.kernel.auth.dto.AuthResponseDto;
import io.mosip.kernel.auth.dto.AuthZResponseDto;
import io.mosip.kernel.auth.dto.MosipUserDto;
import io.mosip.kernel.auth.dto.MosipUserListDto;
import io.mosip.kernel.auth.dto.MosipUserSaltListDto;
import io.mosip.kernel.auth.dto.MosipUserTokenDto;
import io.mosip.kernel.auth.dto.PasswordDto;
import io.mosip.kernel.auth.dto.RIdDto;
import io.mosip.kernel.auth.dto.RolesListDto;
import io.mosip.kernel.auth.dto.UserNameDto;
import io.mosip.kernel.auth.dto.UserPasswordRequestDto;
import io.mosip.kernel.auth.dto.UserPasswordResponseDto;
import io.mosip.kernel.auth.dto.UserRegistrationRequestDto;
import io.mosip.kernel.auth.dto.UserRegistrationResponseDto;
import io.mosip.kernel.auth.dto.UserRoleDto;

/**
 * @author Ramadurai Pandian
 *
 */
public interface AuthService extends AuthZService, AuthNService {

	public MosipUserTokenDto retryToken(String existingToken) throws Exception;

	public AuthNResponse invalidateToken(String token) throws Exception;

	public RolesListDto getAllRoles(String appId);

	public MosipUserListDto getListOfUsersDetails(List<String> userDetails, String appId) throws Exception;

	public MosipUserSaltListDto getAllUserDetailsWithSalt(String appId) throws Exception;

	public RIdDto getRidBasedOnUid(String userId, String appId) throws Exception;

	public AuthZResponseDto unBlockUser(String userId, String appId) throws Exception;

	public AuthZResponseDto changePassword(String appId, PasswordDto passwordDto) throws Exception;

	public AuthZResponseDto resetPassword(String appId, PasswordDto passwordDto) throws Exception;

	public UserNameDto getUserNameBasedOnMobileNumber(String appId, String mobileNumber) throws Exception;

	UserRegistrationResponseDto registerUser(UserRegistrationRequestDto userCreationRequestDto);

	UserPasswordResponseDto addUserPassword(UserPasswordRequestDto userPasswordRequestDto);

	public UserRoleDto getUserRole(String appId, String userId) throws Exception;

	public MosipUserDto getUserDetailBasedonMobileNumber(String appId, String mobileNumber) throws Exception;
	
	public MosipUserDto valdiateToken(String token);
	
	public AuthResponseDto logoutUser(String token);

}
