package io.mosip.kernel.ldap;

import io.mosip.kernel.ldap.dto.MosipUserListDto;
import io.mosip.kernel.ldap.dto.LoginUserDto;
import io.mosip.kernel.ldap.dto.MosipUserDto;
import io.mosip.kernel.ldap.dto.OtpUserDto;
import io.mosip.kernel.ldap.dto.RolesListDto;

import java.util.List;

/**
 *  @author Sabbu Uday Kumar
 *  @since 1.0.0
 */
public interface LdapService {
	MosipUserDto authenticateUser(LoginUserDto user) throws Exception;

	MosipUserDto verifyOtpUser(OtpUserDto otpUserDto) throws Exception;

	RolesListDto getAllRoles();

	MosipUserListDto getListOfUsersDetails(List<String> users) throws Exception;
}
