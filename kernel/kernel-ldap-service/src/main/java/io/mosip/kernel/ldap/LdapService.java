package io.mosip.kernel.ldap;

import io.mosip.kernel.ldap.entities.LoginUser;
import io.mosip.kernel.ldap.entities.MosipUser;
import io.mosip.kernel.ldap.entities.OtpUser;

import java.util.Collection;

public interface LdapService {
    MosipUser authenticateUser(LoginUser user) throws Exception;

    MosipUser verifyOtpUser(OtpUser otpUser) throws Exception;

    Collection<String> getRoles(LoginUser user) throws Exception;
}
