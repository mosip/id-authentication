package io.mosip.kernel.ldap;

import io.mosip.kernel.ldap.config.MosipEnvironment;
import io.mosip.kernel.ldap.entities.LoginUser;
import io.mosip.kernel.ldap.entities.MosipUser;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
public class LdapServiceImpl implements LdapService {

    @Autowired
    MosipEnvironment mosipEnvironment;

    private LdapConnection CreateAnonymousConnection() throws Exception {
        LdapConnection connection = new LdapNetworkConnection
                (mosipEnvironment.getLdapHost(), mosipEnvironment.getLdapPort());

        return connection;
    }

    private Dn CreateAdminDn() throws Exception {
        return new Dn(mosipEnvironment.getLdapAdminDn());
    }

    private Dn CreateUserDn(LoginUser user) throws Exception {
        return new Dn(mosipEnvironment.getUserDnPrefix()
                + user.getUserName()
                + mosipEnvironment.getUserDnSuffix());
    }

    private String ConvertRolesToString(Collection<String> roles) throws Exception {
        StringBuilder rolesString = new StringBuilder();
        for (String role : roles) {
            rolesString.append(role);
            rolesString.append(",");
        }

        return rolesString.length() > 0 ? rolesString.substring(0, rolesString.length() - 1) : "";
    }

    public MosipUser authenticateUser(LoginUser user) throws Exception {
        try {
            LdapConnection connection = CreateAnonymousConnection();
            Dn userdn = CreateUserDn(user);

            connection.bind(userdn, user.getPassword());

            if (connection.isAuthenticated()) {
                Entry userLookup = connection.lookup(userdn);

                connection.unBind();
                connection.close();

                Collection<String> roles = getRoles(user);
                String rolesString = ConvertRolesToString(roles);
                MosipUser mosipUser = new MosipUser(
                        userLookup.get("uid").get().toString(),
                        userLookup.get("mobile").get().toString(),
                        userLookup.get("mail").get().toString(),
                        rolesString
                );

                return mosipUser;
            }

            connection.unBind();
            connection.close();

            return new MosipUser();
        } catch (Exception err) {
            throw new RuntimeException(err + "Unable to authenticate user in LDAP");
        }
    }

    @Override
    public Collection<String> getRoles(LoginUser user) {
        try {
            LdapConnection connection = CreateAnonymousConnection();
            Dn userdn = CreateUserDn(user);
            Dn searchBase = new Dn(mosipEnvironment.getRolesSearchBase());
            String searchFilter = mosipEnvironment.getRolesSearchPrefix() + userdn + mosipEnvironment.getRolesSearchSuffix();

            connection.bind(userdn, user.getPassword());
            EntryCursor rolesData = connection.search(searchBase, searchFilter, SearchScope.ONELEVEL);

            Set<String> roles = new HashSet<String>();
            for (Entry entry : rolesData) {
                roles.add(entry.get("cn").getString());
            }

            rolesData.close();
            connection.unBind();
            connection.close();

            return roles;
        } catch (Exception err) {
            throw new RuntimeException(err + "Unable to fetch user roles from LDAP");
        }
    }
}


