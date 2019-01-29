package io.mosip.kernel.ldap;

import java.util.Collection;

import io.mosip.kernel.ldap.entities.LoginUser;
import io.mosip.kernel.ldap.entities.MosipUser;
import io.mosip.kernel.ldap.entities.OtpUser;
import io.mosip.kernel.ldap.entities.RolesResponseDto;

public interface LdapService {
	MosipUser authenticateUser(LoginUser user) throws Exception;

	MosipUser verifyOtpUser(OtpUser otpUser) throws Exception;

	Collection<String> getRoles(LoginUser user) throws Exception;

	RolesResponseDto getAllRoles();

	MosipUser getUserDetails(String user);
}
