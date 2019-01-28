package io.mosip.kernel.ldap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.ldap.config.MosipEnvironment;
import io.mosip.kernel.ldap.entities.LoginUser;
import io.mosip.kernel.ldap.entities.MosipUser;
import io.mosip.kernel.ldap.entities.OtpUser;
import io.mosip.kernel.ldap.entities.RolesDto;
import io.mosip.kernel.ldap.entities.RolesResponseDto;

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

    private Dn CreateOtpUserDn(OtpUser otpUser) throws Exception {
        return new Dn(mosipEnvironment.getUserDnPrefix()
                + otpUser.getPhone()
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

    private Collection<String> getRolesForOtpUsers(OtpUser otpUser) {
        try {
            LdapConnection connection = CreateAnonymousConnection();
            Dn preregUserDn = CreateOtpUserDn(otpUser);
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
    public MosipUser verifyOtpUser(OtpUser otpUser) throws Exception {
        try {
            LdapConnection connection = CreateAnonymousConnection();
            Dn otpUserDn = CreateOtpUserDn(otpUser);
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
            String rolesString = ConvertRolesToString(roles);
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
    
    @Override
	public RolesResponseDto getAllRoles() {

		LdapConnection connection = null;
		RolesResponseDto rolesResponseDto = new RolesResponseDto();

		try {
			connection = CreateAnonymousConnection();
			List<RolesDto> rolesDtos = new ArrayList<>();
			Dn searchBase = new Dn(mosipEnvironment.getRolesSearchBase());
			String searchFilter = mosipEnvironment.getAllRoles();

			EntryCursor rolesData = connection.search(searchBase, searchFilter, SearchScope.ONELEVEL);

			for (Entry entry : rolesData) {
				RolesDto rolesDto = new RolesDto();
				rolesDto.setRoleId(entry.get("cn").get().toString());
				rolesDto.setRoleName(entry.get("cn").get().toString());
				rolesDto.setRoleDescription(entry.get("description").get().toString());
				rolesDtos.add(rolesDto);
			}
			rolesResponseDto.setRoles(rolesDtos);
			rolesData.close();
			connection.unBind();
			connection.close();
			return rolesResponseDto;
		
		} catch (Exception e) {

			throw new RuntimeException(e + " Unable to fetch user roles from LDAP");
		}

		
	}

	@Override
	public MosipUser getUserDetails(String user) {
		LdapConnection connection = null;

		try {
			connection = CreateAnonymousConnection();
			LoginUser userObj = new LoginUser();
			userObj.setUserName(user);
			Dn userdn = null;

			userdn = CreateUserDn(userObj);

			Entry userLookup = connection.lookup(userdn);

			connection.unBind();
			connection.close();

			return new MosipUser(userLookup.get("uid").get().toString(), userLookup.get("mobile").get().toString(),
					userLookup.get("mail").get().toString(), null);

		}  catch (Exception e) {
			throw new RuntimeException(e + "  Unable to fetch user roles from LDAP");
		}

	}
    
}


