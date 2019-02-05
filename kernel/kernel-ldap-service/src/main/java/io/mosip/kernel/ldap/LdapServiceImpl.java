package io.mosip.kernel.ldap;

import io.mosip.kernel.ldap.config.MosipEnvironment;
import io.mosip.kernel.ldap.dto.*;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.*;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LdapServiceImpl implements LdapService {

    @Autowired
    MosipEnvironment mosipEnvironment;

    private LdapConnection createAnonymousConnection() throws Exception {
        System.out.println("getLdapHost:: " + mosipEnvironment.getLdapHost());
        System.out.println(" getLdapPort:: " + mosipEnvironment.getLdapPort());
        LdapConnection connection = new LdapNetworkConnection
                (mosipEnvironment.getLdapHost(), mosipEnvironment.getLdapPort());

        return connection;
    }

    private Dn createAdminDn() throws Exception {
        return new Dn(mosipEnvironment.getLdapAdminDn());
    }

    private Dn createUserDn(String userName) throws Exception {
        return new Dn(mosipEnvironment.getUserDnPrefix()
                + userName
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

    private Collection<String> getUserRoles(Dn userdn, LdapConnection connection) {
        try {
            Dn searchBase = new Dn(mosipEnvironment.getRolesSearchBase());
            String searchFilter = mosipEnvironment.getRolesSearchPrefix() + userdn + mosipEnvironment.getRolesSearchSuffix();

            EntryCursor rolesData = connection.search(searchBase, searchFilter, SearchScope.ONELEVEL);

            Set<String> roles = new HashSet<String>();
            for (Entry entry : rolesData) {
                roles.add(entry.get("cn").getString());
            }

            rolesData.close();
            return roles;
        } catch (Exception err) {
            throw new RuntimeException(err + "Unable to fetch user roles from LDAP");
        }
    }

    private MosipUserDto lookupUserDetails(Dn userdn, LdapConnection connection) throws Exception {
        try {
            //if lookup access is retricted only to admin then bind the connection with admin details
            // connection.bind(createAdminDn(), mosipEnvironment.getLdapAdminPassword());
            Entry userLookup = connection.lookup(userdn);
            Collection<String> roles = getUserRoles(userdn, connection);
            String rolesString = convertRolesToString(roles);
            MosipUserDto mosipUserDto = new MosipUserDto();
            mosipUserDto.setUserName(userLookup.get("uid").get().toString());
            mosipUserDto.setMobile(userLookup.get("mobile").get().toString());
            mosipUserDto.setMail(userLookup.get("mail").get().toString());
            mosipUserDto.setUserPassword(userLookup.get("userPassword").get().getBytes());
//            mosipUserDto.setLangCode(userLookup.get("preferredLanguage").get().toString());
            mosipUserDto.setName(userLookup.get("cn").get().toString());
            mosipUserDto.setRole(rolesString);
            //unbind before closing the connection
            //connection.unbind();
            return mosipUserDto;
        } catch (Exception err) {
            throw new RuntimeException("unable to fetch user details", err);
        }
    }

    @Override
    public MosipUserDto authenticateUser(LoginUserDto user) throws Exception {
        try {
            LdapConnection connection = createAnonymousConnection();
            Dn userdn = createUserDn(user.getUserName());
            connection.bind(userdn, user.getPassword());

            if (connection.isAuthenticated()) {
                return lookupUserDetails(userdn, connection);
            }
            connection.unBind();
            connection.close();

            return new MosipUserDto();
        } catch (Exception err) {
            throw new RuntimeException(err + "Unable to authenticate user in LDAP");
        }
    }

    @Override
    public MosipUserDto verifyOtpUser(OtpUserDto otpUserDto) throws Exception {
        try {
            LdapConnection connection = createAnonymousConnection();
            Dn otpUserDn = createUserDn(otpUserDto.getPhone());
            if (!connection.exists(otpUserDn)) {
                Entry userEntry = new DefaultEntry(
                        "uid=" + otpUserDto.getPhone() + ",ou=people,c=morocco",
                        "objectClass: organizationalPerson",
                        "objectClass: person",
                        "objectClass: inetOrgPerson",
                        "objectClass: top",
                        "mobile", otpUserDto.getPhone(),
                        "mail", otpUserDto.getEmail(),
                        "uid", otpUserDto.getPhone(),
                        "preferredLanguage", otpUserDto.getLangCode()
                );
                connection.add(userEntry);

                Modification roleModification = new DefaultModification(
                        ModificationOperation.ADD_ATTRIBUTE,
                        "roleOccupant",
                        String.valueOf(otpUserDn)
                );

                connection.modify("cn=PRE_REG_USER,ou=roles,c=morocco", roleModification);
            }

            MosipUserDto mosipUserDto = lookupUserDetails(otpUserDn, connection);
            connection.close();
            return mosipUserDto;
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    @Override
    public RolesListDto getAllRoles() {
        RolesListDto rolesListDto = new RolesListDto();

        try {
            LdapConnection connection = createAnonymousConnection();
            List<RoleDto> roleDtos = new ArrayList<>();
            Dn searchBase = new Dn(mosipEnvironment.getRolesSearchBase());
            String searchFilter = mosipEnvironment.getAllRoles();

            EntryCursor rolesData = connection.search(searchBase, searchFilter, SearchScope.ONELEVEL);

            for (Entry entry : rolesData) {
                RoleDto roleDto = new RoleDto();
                roleDto.setRoleId(entry.get("cn").get().toString());
                roleDto.setRoleName(entry.get("cn").get().toString());
                roleDto.setRoleDescription(entry.get("description").get().toString());
                roleDtos.add(roleDto);
            }
            rolesListDto.setRoles(roleDtos);
            rolesData.close();
            connection.close();

            return rolesListDto;
        } catch (Exception e) {
            throw new RuntimeException(e + " Unable to fetch user roles from LDAP");
        }
    }

    @Override
    public MosipUserListDto getListOfUsersDetails(List<String> users) throws Exception {
        try {
            MosipUserListDto userResponseDto = new MosipUserListDto();
            List<MosipUserDto> mosipUserDtos = new ArrayList<>();

            LdapConnection connection = createAnonymousConnection();

            for (String user : users) {
                Dn userdn = createUserDn(user);
                mosipUserDtos.add(lookupUserDetails(userdn, connection));
            }

            connection.close();
            userResponseDto.setMosipUserDtoList(mosipUserDtos);
            return userResponseDto;
        } catch (Exception err) {
            throw new RuntimeException(err + " Unable to fetch user roles from LDAP");
        }
    }
}


