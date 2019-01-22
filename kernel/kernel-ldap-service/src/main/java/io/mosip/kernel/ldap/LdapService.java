package io.mosip.kernel.ldap;

import io.mosip.kernel.ldap.entities.LoginUser;
import io.mosip.kernel.ldap.entities.MosipUser;

import java.util.Collection;

public interface LdapService {
    MosipUser authenticateUser(LoginUser user) throws Exception;

    Collection<String> getRoles(LoginUser user) throws Exception;
}
