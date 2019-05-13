/**
 * 
 */
package io.mosip.kernel.auth.factory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.directory.api.ldap.model.constants.LdapSecurityConstants;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.password.PasswordDetails;
import org.apache.directory.api.ldap.model.password.PasswordUtil;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.constant.AuthErrorCode;
import io.mosip.kernel.auth.constant.LDAPErrorCode;
import io.mosip.kernel.auth.entities.AuthZResponseDto;
import io.mosip.kernel.auth.entities.ClientSecret;
import io.mosip.kernel.auth.entities.LdapControl;
import io.mosip.kernel.auth.entities.LoginUser;
import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.MosipUserListDto;
import io.mosip.kernel.auth.entities.MosipUserSaltList;
import io.mosip.kernel.auth.entities.PasswordDto;
import io.mosip.kernel.auth.entities.RIdDto;
import io.mosip.kernel.auth.entities.RoleDto;
import io.mosip.kernel.auth.entities.RolesListDto;
import io.mosip.kernel.auth.entities.UserCreationRequestDto;
import io.mosip.kernel.auth.entities.UserCreationResponseDto;
import io.mosip.kernel.auth.entities.UserDetailsSalt;
import io.mosip.kernel.auth.entities.UserNameDto;
import io.mosip.kernel.auth.entities.UserOtp;
import io.mosip.kernel.auth.entities.otp.OtpUser;
import io.mosip.kernel.auth.exception.AuthManagerException;
import io.mosip.kernel.auth.jwtBuilder.TokenGenerator;
import io.mosip.kernel.auth.jwtBuilder.TokenValidator;
import io.mosip.kernel.core.util.HMACUtils;

/**
 * @author Ramadurai Pandian
 *
 */
@Component
public class ILdapDataStore implements IDataStore {

	private DataBaseConfig dataBaseConfig;

	public ILdapDataStore() {
	}

	public ILdapDataStore(DataBaseConfig dataBaseConfig) {
		super();
		this.dataBaseConfig = dataBaseConfig;
	}

	@Autowired
	TokenGenerator tokenGenerator;

	@Autowired
	TokenValidator tokenValidator;

	@Autowired
	MosipEnvironment environment;

	private LdapConnection createAnonymousConnection() throws Exception {
		// LdapNetworkConnection network = new
		// LdapNetworkConnection(dataBaseConfig.getUrl(),Integer.valueOf(dataBaseConfig.getPort()));
		LdapConnection connection = new LdapNetworkConnection(dataBaseConfig.getUrl(),
				Integer.valueOf(dataBaseConfig.getPort()));
		return connection;
	}
	
	private LdapContext getContext() throws NamingException {

		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, AuthConstant.LDAP_INITAL_CONTEXT_FACTORY);
		env.put(Context.PROVIDER_URL, "ldap://52.172.11.190:10389");
		// env.put(Context.PROVIDER_URL, "ldap://localhost:10389");
		env.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
		env.put(Context.SECURITY_CREDENTIALS, "secret");
		LdapContext context = new InitialLdapContext(env, null);
		LdapControl ldapControl = new LdapControl();
		context.setRequestControls(ldapControl.getControls());
		return context;
	}

	@Override
	public MosipUserDto authenticateUser(LoginUser loginUser) throws Exception {
		MosipUserDto mosipUser = getLoginDetails(loginUser);
		return mosipUser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.auth.service.AuthNService#authenticateWithOtp(io.mosip.kernel
	 * .auth.entities.otp.OtpUser)
	 */
	@Override
	public MosipUserDto authenticateWithOtp(OtpUser otpUser) throws Exception {
		MosipUserDto mosipUser = getOtpDetails(otpUser);
		return mosipUser;
	}

	private MosipUserDto getOtpDetails(OtpUser otpUser) throws Exception {
		LdapConnection connection = createAnonymousConnection();
		Dn userdn = createUserDn(otpUser.getUserId());
		if (!connection.exists(userdn)) {
			throw new AuthManagerException(AuthErrorCode.USER_VALIDATION_ERROR.getErrorCode(),
					AuthErrorCode.USER_VALIDATION_ERROR.getErrorMessage());

		}
		MosipUserDto mosipUserDto = lookupUserDetails(userdn, connection);
		return mosipUserDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.auth.service.AuthNService#authenticateUserWithOtp(io.mosip.
	 * kernel.auth.entities.UserOtp)
	 */
	@Override
	public MosipUserDto authenticateUserWithOtp(UserOtp userOtp) throws Exception {
		MosipUserDto mosipUserDto = getMosipUser(userOtp.getUserId());
		return mosipUserDto;
	}

	private MosipUserDto getMosipUser(String userId) throws Exception {
		LdapConnection connection = createAnonymousConnection();
		Dn userdn = createUserDn(userId);
		MosipUserDto mosipUserDto = lookupUserDetails(userdn, connection);
		return mosipUserDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.auth.service.AuthNService#authenticateWithSecretKey(io.mosip.
	 * kernel.auth.entities.ClientSecret)
	 */
	@Override
	public MosipUserDto authenticateWithSecretKey(ClientSecret clientSecret) throws Exception {
		MosipUserDto mosipUser = getClientSecretDetails(clientSecret);
		return mosipUser;
	}

	private MosipUserDto getClientSecretDetails(ClientSecret clientSecret) throws Exception {
		LdapConnection connection = createAnonymousConnection();
		Dn userdn = createUserDn(clientSecret.getClientId());
		try {
			connection.bind(userdn, clientSecret.getSecretKey());
		} catch (Exception ex) {
			throw new AuthManagerException(LDAPErrorCode.LDAP_CONNECTION_ERROR.getErrorCode(),
					LDAPErrorCode.LDAP_CONNECTION_ERROR.getErrorMessage());
		}
		if (connection.isAuthenticated()) {
			return lookupUserDetails(userdn, connection);
		}
		connection.unBind();
		connection.close();
		return null;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.auth.service.AuthZService#verifyOtp(io.mosip.kernel.auth.
	 * entities.otp.OtpValidateRequestDto, java.lang.String)
	 */

	public MosipUserDto getLoginDetails(LoginUser loginUser) throws Exception {
		LdapConnection connection = createAnonymousConnection();
		Dn userdn = createUserDn(loginUser.getUserName());
		try {
			connection.bind(userdn, loginUser.getPassword());
		} catch (Exception ex) {
			throw new AuthManagerException(LDAPErrorCode.LDAP_CONNECTION_ERROR.getErrorCode(),
					LDAPErrorCode.LDAP_CONNECTION_ERROR.getErrorMessage());
		}
		if (connection.isAuthenticated()) {
			return lookupUserDetails(userdn, connection);
		}
		connection.unBind();
		connection.close();
		return null;
	}

	private MosipUserDto lookupUserDetails(Dn userdn, LdapConnection connection) throws Exception {
		try {
			// if lookup access is retricted only to admin then bind the
			// connection with
			// admin details
			// connection.bind(createAdminDn(),
			// mosipEnvironment.getLdapAdminPassword());

			Collection<String> roles = getUserRoles(userdn, connection);
			String rolesString = convertRolesToString(roles);
			MosipUserDto mosipUserDto = null;

			Entry userLookup = connection.lookup(userdn);
			if (userLookup != null) {
				mosipUserDto = new MosipUserDto();
				mosipUserDto.setUserId(userLookup.get("uid").get().toString());
				mosipUserDto
						.setMobile(userLookup.get("mobile") != null ? userLookup.get("mobile").get().toString() : null);
				mosipUserDto.setMail(userLookup.get("mail") != null ? userLookup.get("mail").get().toString() : null);
				if(userLookup.get("userPassword")!=null) {
				PasswordDetails password = PasswordUtil
						.splitCredentials(userLookup.get("userPassword").get().getBytes());
				mosipUserDto.setUserPassword(
						userLookup.get("userPassword") != null ? HMACUtils.digestAsPlainText(password.getPassword())
								: null);
				}
				// mosipUserDto.setLangCode(userLookup.get("preferredLanguage").get().toString());
				mosipUserDto.setName(userLookup.get("cn").get().toString());
				if(userLookup.get("rid")!=null) {
				mosipUserDto.setRId(userLookup.get("rid").get().toString());
				}
				mosipUserDto.setRole(rolesString);
			}
			return mosipUserDto;
		} catch (Exception err) {
			throw new AuthManagerException(LDAPErrorCode.LDAP_PARSE_REQUEST_ERROR.getErrorCode(),
					LDAPErrorCode.LDAP_PARSE_REQUEST_ERROR.getErrorMessage());
		}
	}

	private Collection<String> getUserRoles(Dn userdn, LdapConnection connection) {
		try {
			Dn searchBase = new Dn("ou=roles,c=morocco");
			String searchFilter = "(&(objectClass=organizationalRole)(roleOccupant=" + userdn + "))";

			EntryCursor rolesData = connection.search(searchBase, searchFilter, SearchScope.ONELEVEL);

			Set<String> roles = new HashSet<String>();
			for (Entry entry : rolesData) {
				roles.add(entry.get("cn").getString());
			}

			rolesData.close();
			return roles;
		} catch (Exception err) {
			throw new AuthManagerException(LDAPErrorCode.LDAP_ROLES_REQUEST_ERROR.getErrorCode(),
					LDAPErrorCode.LDAP_ROLES_REQUEST_ERROR.getErrorMessage());
		}
	}

	private String convertRolesToString(Collection<String> roles) throws Exception {
		StringBuilder rolesString = new StringBuilder();
		for (String role : roles) {
			rolesString.append(role);
			rolesString.append(",");
		}

		return rolesString.length() > 0 ? rolesString.substring(0, rolesString.length() - 1) : "";
	}

	private Dn createUserDn(String userName) throws Exception {
		return new Dn("uid=" + userName + ",ou=people,c=morocco");
	}

	@Override
	public RolesListDto getAllRoles() {
		RolesListDto rolesListDto = new RolesListDto();

		try {
			LdapConnection connection = createAnonymousConnection();
			List<RoleDto> roleDtos = new ArrayList<>();
			Dn searchBase = new Dn("ou=roles,c=morocco");
			String searchFilter = "(objectClass=organizationalRole)";

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
			throw new AuthManagerException(LDAPErrorCode.LDAP_ROLES_REQUEST_ERROR.getErrorCode(),
					LDAPErrorCode.LDAP_ROLES_REQUEST_ERROR.getErrorMessage());
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
				MosipUserDto data = lookupUserDetails(userdn, connection);
				if (data != null)
					mosipUserDtos.add(data);
			}

			connection.close();
			userResponseDto.setMosipUserDtoList(mosipUserDtos);
			return userResponseDto;
		} catch (Exception err) {
			throw new AuthManagerException(LDAPErrorCode.LDAP_ROLES_REQUEST_ERROR.getErrorCode(),
					LDAPErrorCode.LDAP_ROLES_REQUEST_ERROR.getErrorMessage());
		}
	}

	@Override
	public MosipUserSaltList getAllUserDetailsWithSalt() throws Exception {
		MosipUserSaltList mosipUserSaltList = new MosipUserSaltList();
		List<UserDetailsSalt> mosipUserDtos = new ArrayList<>();
		LdapConnection connection = createAnonymousConnection();
		Dn searchBase = new Dn("ou=people,c=morocco");
		String searchFilter = "(&(objectClass=organizationalPerson)(objectClass=inetOrgPerson))";
		EntryCursor peoplesData = connection.search(searchBase, searchFilter, SearchScope.ONELEVEL);
		for (Entry entry : peoplesData) {
			UserDetailsSalt saltDetails = new UserDetailsSalt();
			saltDetails.setUserId(entry.get("uid").get().toString());
			if (entry.get("userPassword").get() != null) {
				PasswordDetails password = PasswordUtil.splitCredentials(entry.get("userPassword").get().getBytes());
				if (password.getSalt() != null) {
					saltDetails.setSalt(HMACUtils.digestAsPlainText(password.getSalt()));
				}
			}
			mosipUserDtos.add(saltDetails);
		}
		mosipUserSaltList.setMosipUserSaltList(mosipUserDtos);
		return mosipUserSaltList;
	}

	@Override
	public RIdDto getRidFromUserId(String userId) throws Exception {
		RIdDto ridDto = null;
		LdapConnection ldapConnection = createAnonymousConnection();
		Dn userdn = createUserDn(userId);
		MosipUserDto data = lookupUserDetails(userdn, ldapConnection);
		if (data == null) {
			throw new AuthManagerException(AuthErrorCode.USER_VALIDATION_ERROR.getErrorCode(),
					AuthErrorCode.USER_VALIDATION_ERROR.getErrorMessage());
		}
		if (data.getRId() != null) {
			ridDto = new RIdDto();
			ridDto.setRId(data.getRId());
		}
		return ridDto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AuthZResponseDto unBlockAccount(String userId) throws Exception {

		LdapContext context = null;
		AuthZResponseDto authZResponseDto = null;
		try {
			context = getContext();
			ModificationItem[] modItems = new ModificationItem[2];
			modItems[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
					new BasicAttribute(AuthConstant.PWD_ACCOUNT_LOCKED_TIME_ATTRIBUTE));
			modItems[1] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
					new BasicAttribute(AuthConstant.PWD_FAILURE_TIME_ATTRIBUTE));

			context.modifyAttributes("uid=" + userId + ",ou=people,c=morocco", modItems);
			authZResponseDto = new AuthZResponseDto();
			authZResponseDto.setMessage("Successfully Unblocked");
			authZResponseDto.setStatus("Sucesss");

		} catch (NamingException e) {
			throw new AuthManagerException(AuthErrorCode.NAMING_EXCEPTION.getErrorCode(),
					AuthErrorCode.NAMING_EXCEPTION.getErrorMessage() + "" + e.getExplanation());
		} finally {
			if (context != null) {
				context.close();
			}
		}
		return authZResponseDto;
	}

	@Override
	public AuthZResponseDto changePassword(PasswordDto passwordDto) throws NamingException {
		LdapContext ldapContext = null;
		AuthZResponseDto authZResponseDto = null;
		LdapConnection ldapConnection = null;
		try {
			ldapContext = getContext();
		} catch (NamingException e) {
			throw new AuthManagerException(AuthErrorCode.NAMING_EXCEPTION.getErrorCode(),
					AuthErrorCode.NAMING_EXCEPTION.getErrorMessage());

		}

		try {
			ldapConnection = createAnonymousConnection();
			Dn userdn = createUserDn(passwordDto.getUserId());
			MosipUserDto mosipUserDto = lookupUserDetails(userdn, ldapConnection);
			Objects.requireNonNull(mosipUserDto);
			String ldapPassword = getPassword(mosipUserDto.getUserId(), ldapContext);
			Objects.requireNonNull(ldapPassword);
			boolean isNotMatching = isNotAMatchWithUserOrEmail(mosipUserDto.getUserId(), mosipUserDto.getMail(),
					passwordDto.getNewPassword());

			validateOldPassword(passwordDto.getOldPassword(), ldapPassword);

			if (!isNotMatching && !passwordDto.getOldPassword().equals(passwordDto.getNewPassword())) {
				byte[] newUserPassword = PasswordUtil.createStoragePassword(passwordDto.getNewPassword().getBytes(),
						LdapSecurityConstants.getAlgorithm(passwordDto.getHashAlgo()));

				ModificationItem[] modItems = new ModificationItem[1];
				modItems[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
						new BasicAttribute("userPassword", newUserPassword));
				ldapContext.modifyAttributes("uid=" + passwordDto.getUserId() + ",ou=people,c=morocco", modItems);
				mosipUserDto.setUserPassword(new String(newUserPassword));
				authZResponseDto = new AuthZResponseDto();
				authZResponseDto.setMessage("Successfully changed");
				authZResponseDto.setStatus("Success");
			} else {
				throw new AuthManagerException(AuthErrorCode.PASSWORD_POLICY_EXCEPTION.getErrorCode(),
						AuthErrorCode.PASSWORD_POLICY_EXCEPTION.getErrorMessage());
			}

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			ldapContext.close();
			try {
				ldapConnection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return authZResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.auth.factory.IDataStore#resetPassword(io.mosip.kernel.auth.
	 * entities.PasswordDto)
	 */
	@Override
	public AuthZResponseDto resetPassword(PasswordDto passwordDto) throws Exception {
		LdapContext ldapContext = null;
		AuthZResponseDto authZResponseDto = null;
		ldapContext = getContext();
		LdapConnection ldapConnection;
		ldapConnection = createAnonymousConnection();
		Dn userdn = createUserDn(passwordDto.getUserId());
		MosipUserDto mosipUserDto = lookupUserDetails(userdn, ldapConnection);
		Objects.requireNonNull(mosipUserDto);
		boolean isNotMatching = isNotAMatchWithUserOrEmail(mosipUserDto.getUserId(), mosipUserDto.getMail(),
				passwordDto.getNewPassword());
		if (!isNotMatching) {
			byte[] newUserPassword = PasswordUtil.createStoragePassword(passwordDto.getNewPassword().getBytes(),
					LdapSecurityConstants.getAlgorithm(passwordDto.getHashAlgo()));

			ModificationItem[] modItems = new ModificationItem[1];
			modItems[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("userPassword", newUserPassword));
			ldapContext.modifyAttributes("uid=" + passwordDto.getUserId() + ",ou=people,c=morocco", modItems);
			mosipUserDto.setUserPassword(new String(newUserPassword));
			authZResponseDto = new AuthZResponseDto();
			authZResponseDto.setMessage("Successfully the password has been reset");
			authZResponseDto.setStatus("Success");
			ldapContext.close();
			ldapConnection.close();

		} else {
			ldapContext.close();
			ldapConnection.close();
			throw new AuthManagerException(AuthErrorCode.PASSWORD_POLICY_EXCEPTION.getErrorCode(),
					AuthErrorCode.PASSWORD_POLICY_EXCEPTION.getErrorMessage());
		}
		return authZResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.auth.factory.IDataStore#getUserNameBasedOnMobileNumber(java.
	 * lang.String)
	 */
	@Override
	public UserNameDto getUserNameBasedOnMobileNumber(String mobileNumber) throws Exception {
		Dn searchBase = new Dn("ou=people,c=morocco");
		UserNameDto userNameDto = new UserNameDto();
		String searchFilter = "(&(objectClass=organizationalPerson)(objectClass=inetOrgPerson)(objectClass=person)(mobile="
				+ mobileNumber + "))";
		LdapContext context = getContext();
		NamingEnumeration<SearchResult> searchResult = context.search(searchBase.getName(), searchFilter,
				new SearchControls());
		if (!searchResult.hasMore()) {
			throw new AuthManagerException("ADMN-ACM-MOB-NOT-FOUND", "Mobile is registered/not present");
		}
		while (searchResult.hasMore()) {
			Attributes attributes = searchResult.next().getAttributes();
			Attribute uid = attributes.get("uid");
			userNameDto.setUserName((String) uid.get());
		}
		context.close();
		return userNameDto;
	}

	/**
	 * @param userid
	 * @param ldapContext
	 * @return
	 * @throws Exception
	 */
	private String getPassword(String userid, LdapContext ldapContext) throws Exception {
		String encryptedPassword = null;
		Dn searchBase = new Dn("uid=" + userid + ",ou=people,c=morocco");
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<SearchResult> searchResult = ldapContext.search(searchBase.getName(),
				"(&(objectClass=organizationalPerson)(objectClass=inetOrgPerson)(objectClass=person))", searchControls);
		while (searchResult.hasMore()) {
			SearchResult result = searchResult.next();
			byte[] encryptedPasswordBytes = (byte[]) result.getAttributes().get("userPassword").get();
			encryptedPassword = new String(encryptedPasswordBytes);

		}
		return encryptedPassword;
	}

	/**
	 * TBD
	 * 
	 * @param oldPassword
	 * @param hashedPassword
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private void validateOldPassword(String oldPassword, String hashedPassword) {

		boolean password = PasswordUtil.compareCredentials(oldPassword.getBytes(), hashedPassword.getBytes());
		if (!password) {
			throw new AuthManagerException(AuthErrorCode.OLD_PASSWORD_NOT_MATCH.getErrorCode(),
					AuthErrorCode.OLD_PASSWORD_NOT_MATCH.getErrorMessage());
		}
	}

	/**
	 * Check password matches with either userid or email id. At most 3 letters can
	 * match with the password.
	 * 
	 * @param userId
	 *            - user id
	 * @param email
	 *            - email
	 * @param password
	 *            - password
	 * @return {@link boolean}
	 */
	private boolean isNotAMatchWithUserOrEmail(String userId, String email, String password) {

		return (password.contains(userId) || password.contains(email));
	}

	@Override
	public UserCreationResponseDto createAccount(UserCreationRequestDto userCreationRequestDto){
		LdapConnection connection = null;
		Dn dn = null;
		try {
			connection = createAnonymousConnection();
			dn=createUserDn(userCreationRequestDto.getUserName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		Hashtable<String,String> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, AuthConstant.LDAP_INITAL_CONTEXT_FACTORY);
		env.put(Context.PROVIDER_URL, "ldap://52.172.11.190:10389");
		env.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
		env.put(Context.SECURITY_CREDENTIALS, "secret");
		try {
		if(connection.exists(dn)) {
			//throw already exist exception
		}else {
	  DirContext  context = null;	
	
	  List<Attribute> attributes= new ArrayList<>();
       attributes.add(new BasicAttribute("cn", userCreationRequestDto.getUserName()));  
       attributes.add(new BasicAttribute("sn", userCreationRequestDto.getUserName()));  
       attributes.add(new BasicAttribute("mail", userCreationRequestDto.getEmailID()));  
       attributes.add(new BasicAttribute("mobile", userCreationRequestDto.getContactNo()));
       attributes.add(new BasicAttribute("dateOfBirth", userCreationRequestDto.getDateOfBirth()));
       attributes.add( new BasicAttribute("firstName", userCreationRequestDto.getFirstName()));
       attributes.add(new BasicAttribute("lastName", userCreationRequestDto.getLastName()));
       attributes.add( new BasicAttribute("gender", userCreationRequestDto.getGender()));
        
        Attribute oc = new BasicAttribute("objectClass");  
        oc.add("top");  
        oc.add("person");  
        oc.add("organizationalPerson");  
        oc.add("inetOrgPerson");  
        oc.add("userDetails"); 
        attributes.add(oc);

	context = new InitialDirContext(env);
	BasicAttributes entry = new BasicAttributes();  
    attributes.parallelStream().forEach(entry::put); 
    context.createSubcontext(dn.getName(), entry);  
	
}}catch (NamingException|LdapException e) {
	System.out.println(e.getMessage());
}
        return null;
	
	}}
