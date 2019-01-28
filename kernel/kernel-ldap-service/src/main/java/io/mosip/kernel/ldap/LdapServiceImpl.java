package io.mosip.kernel.ldap;

import io.mosip.kernel.ldap.config.MosipEnvironment;
import io.mosip.kernel.ldap.entities.LoginUser;
import io.mosip.kernel.ldap.entities.MosipUser;
import io.mosip.kernel.ldap.entities.OtpUser;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.*;
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

    private LdapConnection createAnonymousConnection() throws Exception {
        LdapConnection connection = new LdapNetworkConnection
                (mosipEnvironment.getLdapHost(), mosipEnvironment.getLdapPort());

        return connection;
    }

    private Dn createAdminDn() throws Exception {
        return new Dn(mosipEnvironment.getLdapAdminDn());
    }

    private Dn createUserDn(LoginUser user) throws Exception {
        return new Dn(mosipEnvironment.getUserDnPrefix()
                + user.getUserName()
                + mosipEnvironment.getUserDnSuffix());
    }

    private Dn createOtpUserDn(OtpUser otpUser) throws Exception {
        return new Dn(mosipEnvironment.getUserDnPrefix()
                + otpUser.getPhone()
                + mosipEnvironment.getUserDnSuffix());
    }

    private String convertRolesToString(Collection<String> roles) throws Exception {
        StringBuilder rolesString = new StringBuilder();
        for (String role : roles) {
            rolesString.append(role);
            rolesString.append(",");
        }

        return rolesString.length() > 0 ? rolesString.substring(0, rolesString.length() - 1) : "";
    }

    public Collection<String> getRoles(LoginUser user) {
        try {
            LdapConnection connection = createAnonymousConnection();
            Dn userdn = createUserDn(user);
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

    private Collection<String> getRolesForOtpUsers(OtpUser otpUser) {
        try {
            LdapConnection connection = createAnonymousConnection();
            Dn preregUserDn = createOtpUserDn(otpUser);
            Dn searchBase = new Dn(mosipEnvironment.getRolesSearchBase());
            String searchFilter = mosipEnvironment.getRolesSearchPrefix() + preregUserDn + mosipEnvironment.getRolesSearchSuffix();

            EntryCursor rolesData = connection.search(searchBase, searchFilter, SearchScope.ONELEVEL);

            Set<String> roles = new HashSet<String>();
            for (Entry entry : rolesData) {
                roles.add(entry.get("cn").getString());
            }

            rolesData.close();
            connection.close();

            return roles;
        } catch (Exception err) {
            throw new RuntimeException(err + "Unable to fetch user roles from LDAP");
        }
    }

    @Override
    public MosipUser authenticateUser(LoginUser user) throws Exception {
        try {
            LdapConnection connection = createAnonymousConnection();
            Dn userdn = createUserDn(user);

            connection.bind(userdn, user.getPassword());

            if (connection.isAuthenticated()) {
                Entry userLookup = connection.lookup(userdn);

                connection.unBind();
                connection.close();

                Collection<String> roles = getRoles(user);
                String rolesString = convertRolesToString(roles);
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
    public MosipUser verifyOtpUser(OtpUser otpUser) throws Exception {
        try {
            LdapConnection connection = createAnonymousConnection();
            Dn otpUserDn = createOtpUserDn(otpUser);
            if (!connection.exists(otpUserDn)) {
                Entry userEntry = new DefaultEntry(
                        "uid=" + otpUser.getPhone() + ",ou=people,c=morocco",
                        "objectClass: organizationalPerson",
                        "objectClass: person",
                        "objectClass: inetOrgPerson",
                        "objectClass: top",
                        "mobile", otpUser.getPhone(),
                        "mail", otpUser.getEmail(),
                        "uid", otpUser.getPhone(),
                        "preferredLanguage", otpUser.getLangCode()
                );
                connection.add(userEntry);

                Modification roleModification = new DefaultModification(
                        ModificationOperation.ADD_ATTRIBUTE,
                        "roleOccupant",
                        String.valueOf(otpUserDn)
                );

                connection.modify("cn=PRE_REG_USER,ou=roles,c=morocco", roleModification);
            }

            Entry userLookup = connection.lookup(otpUserDn);
            connection.close();

            Collection<String> roles = getRolesForOtpUsers(otpUser);
            String rolesString = convertRolesToString(roles);
            MosipUser mosipUser = new MosipUser(
                    userLookup.get("uid").get().toString(),
                    userLookup.get("mobile").get().toString(),
                    userLookup.get("mail").get().toString(),
                    rolesString,
                    userLookup.get("preferredLanguage").get().toString()
            );

            return mosipUser;
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }
}


