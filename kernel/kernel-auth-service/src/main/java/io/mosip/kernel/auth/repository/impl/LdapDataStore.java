/**
 * 
 */
package io.mosip.kernel.auth.repository.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
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
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
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
import io.mosip.kernel.auth.constant.LdapConstants;
import io.mosip.kernel.auth.dto.AuthZResponseDto;
import io.mosip.kernel.auth.dto.ClientSecret;
import io.mosip.kernel.auth.dto.DataBaseProps;
import io.mosip.kernel.auth.dto.LdapControl;
import io.mosip.kernel.auth.dto.LoginUser;
import io.mosip.kernel.auth.dto.MosipUserDto;
import io.mosip.kernel.auth.dto.MosipUserListDto;
import io.mosip.kernel.auth.dto.MosipUserSalt;
import io.mosip.kernel.auth.dto.MosipUserSaltListDto;
import io.mosip.kernel.auth.dto.PasswordDto;
import io.mosip.kernel.auth.dto.RIdDto;
import io.mosip.kernel.auth.dto.Role;
import io.mosip.kernel.auth.dto.RolesListDto;
import io.mosip.kernel.auth.dto.UserNameDto;
import io.mosip.kernel.auth.dto.UserOtp;
import io.mosip.kernel.auth.dto.UserPasswordRequestDto;
import io.mosip.kernel.auth.dto.UserPasswordResponseDto;
import io.mosip.kernel.auth.dto.UserRegistrationRequestDto;
import io.mosip.kernel.auth.dto.UserRegistrationResponseDto;
import io.mosip.kernel.auth.dto.ValidationResponseDto;
import io.mosip.kernel.auth.dto.otp.OtpUser;
import io.mosip.kernel.auth.exception.AuthManagerException;
import io.mosip.kernel.auth.repository.DataStore;
import io.mosip.kernel.auth.util.TokenGenerator;
import io.mosip.kernel.auth.util.TokenValidator;
import io.mosip.kernel.core.util.HMACUtils;

/**
 * @author Ramadurai Pandian
 *
 */
@Component
public class LdapDataStore implements DataStore {

	private DataBaseProps dataBaseConfig;

	public LdapDataStore() {
	}

	public LdapDataStore(DataBaseProps dataBaseConfig) {
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
		env.put(Context.INITIAL_CONTEXT_FACTORY, LdapConstants.LDAP_INITAL_CONTEXT_FACTORY);
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
				if (userLookup.get("userPassword") != null) {
					PasswordDetails password = PasswordUtil
							.splitCredentials(userLookup.get("userPassword").get().getBytes());
					mosipUserDto.setUserPassword(
							userLookup.get("userPassword") != null ? HMACUtils.digestAsPlainText(password.getPassword())
									: null);
				}
				// mosipUserDto.setLangCode(userLookup.get("preferredLanguage").get().toString());
				mosipUserDto.setName(userLookup.get("cn").get().toString());
				if (userLookup.get("rid") != null) {
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

	private Dn createUserDn(String userName) throws LdapInvalidDnException {
		return new Dn("uid=" + userName + ",ou=people,c=morocco");
	}

	private Dn createRoleDn(String role) throws LdapInvalidDnException {
		return new Dn("cn=" + role + ",ou=roles,c=morocco");
	}

	@Override
	public RolesListDto getAllRoles() {
		RolesListDto rolesListDto = new RolesListDto();

		try {
			LdapConnection connection = createAnonymousConnection();
			List<Role> roleDtos = new ArrayList<>();
			Dn searchBase = new Dn("ou=roles,c=morocco");
			String searchFilter = "(objectClass=organizationalRole)";

			EntryCursor rolesData = connection.search(searchBase, searchFilter, SearchScope.ONELEVEL);

			for (Entry entry : rolesData) {
				Role roleDto = new Role();
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
	public MosipUserSaltListDto getAllUserDetailsWithSalt() throws Exception {
		MosipUserSaltListDto mosipUserSaltList = new MosipUserSaltListDto();
		List<MosipUserSalt> mosipUserDtos = new ArrayList<>();
		LdapConnection connection = createAnonymousConnection();
		Dn searchBase = new Dn("ou=people,c=morocco");
		String searchFilter = "(&(objectClass=organizationalPerson)(objectClass=inetOrgPerson))";
		EntryCursor peoplesData = connection.search(searchBase, searchFilter, SearchScope.ONELEVEL);
		for (Entry entry : peoplesData) {
			MosipUserSalt saltDetails = new MosipUserSalt();
			saltDetails.setUserId(entry.get("uid").get().toString());
			if (entry.get("userPassword") != null) {
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
		ldapConnection.close();
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
					new BasicAttribute(LdapConstants.PWD_ACCOUNT_LOCKED_TIME_ATTRIBUTE));
			modItems[1] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
					new BasicAttribute(LdapConstants.PWD_FAILURE_TIME_ATTRIBUTE));

			context.modifyAttributes("uid=" + userId + ",ou=people,c=morocco", modItems);
			authZResponseDto = new AuthZResponseDto();
			authZResponseDto.setMessage("Successfully Unblocked");
			authZResponseDto.setStatus("Sucesss");
            closeContext(context);
		} catch (NamingException e) {
			 closeContext(context);
			throw new AuthManagerException(AuthErrorCode.NAMING_EXCEPTION.getErrorCode(),
					AuthErrorCode.NAMING_EXCEPTION.getErrorMessage() + "" + e.getExplanation());
		} 
		return authZResponseDto;
	}

	@Override
	public AuthZResponseDto changePassword(PasswordDto passwordDto) {
		LdapContext ldapContext = null;
		AuthZResponseDto authZResponseDto = null;
		String mailId = null;
		String userId = null;
		try {
			ldapContext = getContext();
		} catch (NamingException e) {
			throw new AuthManagerException(AuthErrorCode.NAMING_EXCEPTION.getErrorCode(),
					AuthErrorCode.NAMING_EXCEPTION.getErrorMessage());
		}
		try {

			NamingEnumeration<SearchResult> userDetailSearchResult = getUserDetailSearchResult(passwordDto.getUserId());
			while (userDetailSearchResult.hasMore()) {
				SearchResult searchObject = userDetailSearchResult.next();
				mailId = (String) searchObject.getAttributes().get(LdapConstants.MAIL).get();
				userId = (String) searchObject.getAttributes().get("uid").get();
			}
			String ldapPassword = getPassword(passwordDto.getUserId(), ldapContext);
			Objects.requireNonNull(ldapPassword);
			boolean isNotMatching = isNotAMatchWithUserOrEmail(userId, mailId, passwordDto.getNewPassword());

			validateOldPassword(passwordDto.getOldPassword(), ldapPassword);

			if (!isNotMatching && !passwordDto.getOldPassword().equals(passwordDto.getNewPassword())) {
				byte[] newUserPassword = PasswordUtil.createStoragePassword(passwordDto.getNewPassword().getBytes(),
						LdapSecurityConstants.getAlgorithm(passwordDto.getHashAlgo()));

				ModificationItem[] modItems = new ModificationItem[1];
				modItems[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
						new BasicAttribute("userPassword", newUserPassword));
				ldapContext.modifyAttributes("uid=" + passwordDto.getUserId() + ",ou=people,c=morocco", modItems);
				authZResponseDto = new AuthZResponseDto();
				authZResponseDto.setMessage("Successfully changed");
				authZResponseDto.setStatus("Success");
			} else {
				throw new AuthManagerException(AuthErrorCode.PASSWORD_POLICY_EXCEPTION.getErrorCode(),
						AuthErrorCode.PASSWORD_POLICY_EXCEPTION.getErrorMessage());
			}
			closeContext(ldapContext);
		} catch (Exception e) {
			closeContext(ldapContext);
			throw new AuthManagerException(AuthErrorCode.SERVER_ERROR.getErrorCode(),
					AuthErrorCode.SERVER_ERROR.getErrorMessage() + " " + e.getCause());
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
	public AuthZResponseDto resetPassword(PasswordDto passwordDto) {
		LdapContext ldapContext = null;
		AuthZResponseDto authZResponseDto = null;
		String mailId = null;
		String userId = null;
		try {
			ldapContext = getContext();
			NamingEnumeration<SearchResult> userDetailSearchResult = getUserDetailSearchResult(passwordDto.getUserId());
			while (userDetailSearchResult.hasMore()) {
				SearchResult searchObject = userDetailSearchResult.next();
				mailId = (String) searchObject.getAttributes().get(LdapConstants.MAIL).get();
				userId = (String) searchObject.getAttributes().get("uid").get();
			}
			boolean isNotMatching = isNotAMatchWithUserOrEmail(userId, mailId, passwordDto.getNewPassword());
			if (!isNotMatching) {
				byte[] newUserPassword = PasswordUtil.createStoragePassword(passwordDto.getNewPassword().getBytes(),
						LdapSecurityConstants.getAlgorithm(passwordDto.getHashAlgo()));

				ModificationItem[] modItems = new ModificationItem[1];
				modItems[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
						new BasicAttribute("userPassword", newUserPassword));
				ldapContext.modifyAttributes("uid=" + passwordDto.getUserId() + ",ou=people,c=morocco", modItems);

				authZResponseDto = new AuthZResponseDto();
				authZResponseDto.setMessage("Successfully the password has been reset");
				authZResponseDto.setStatus("Success");
			} else {
				throw new AuthManagerException(AuthErrorCode.PASSWORD_POLICY_EXCEPTION.getErrorCode(),
						AuthErrorCode.PASSWORD_POLICY_EXCEPTION.getErrorMessage());
			}
			closeContext(ldapContext);
		} catch (LdapInvalidDnException ex) {
			closeContext(ldapContext);
			throw new AuthManagerException(AuthErrorCode.INVALID_DN.getErrorCode(),
					AuthErrorCode.INVALID_DN.getErrorMessage() + ex.getCause());
		} catch (NamingException ex) {
			closeContext(ldapContext);
			throw new AuthManagerException(AuthErrorCode.NAMING_EXCEPTION.getErrorCode(),
					AuthErrorCode.NAMING_EXCEPTION.getErrorMessage() + ex.getCause());
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
		NamingEnumeration<SearchResult> searchResult = getUserDetail(mobileNumber);
		UserNameDto userNameDto = new UserNameDto();
		if (!searchResult.hasMore()) {
			throw new AuthManagerException("ADMN-ACM-MOB-NOT-FOUND", "Mobile is registered/not present");
		}
		while (searchResult.hasMore()) {
			Attributes attributes = searchResult.next().getAttributes();
			Attribute uid = attributes.get("uid");
			userNameDto.setUserName((String) uid.get());
		}

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
	public UserRegistrationResponseDto registerUser(UserRegistrationRequestDto userCreationRequestDto) {
		Dn userDn = null;
		DirContext context = null;
		try {
			context = getDirContext();
			userDn = createUserDn(userCreationRequestDto.getUserName());
			List<Attribute> attributes = new ArrayList<>();
			attributes.add(new BasicAttribute(LdapConstants.CN, userCreationRequestDto.getUserName()));
			attributes.add(new BasicAttribute(LdapConstants.SN, userCreationRequestDto.getUserName()));
			attributes.add(new BasicAttribute(LdapConstants.MAIL, userCreationRequestDto.getEmailID()));
			attributes.add(new BasicAttribute(LdapConstants.MOBILE, userCreationRequestDto.getContactNo()));
			attributes.add(new BasicAttribute(LdapConstants.DOB, userCreationRequestDto.getDateOfBirth().toString()));
			attributes.add(new BasicAttribute(LdapConstants.FIRST_NAME, userCreationRequestDto.getFirstName()));
			attributes.add(new BasicAttribute(LdapConstants.LAST_NAME, userCreationRequestDto.getLastName()));
			attributes.add(new BasicAttribute(LdapConstants.GENDER_CODE, userCreationRequestDto.getGender()));
			attributes.add(new BasicAttribute(LdapConstants.IS_ACTIVE, LdapConstants.FALSE));
			Attribute oc = new BasicAttribute(LdapConstants.OBJECT_CLASS);
			oc.add(LdapConstants.INET_ORG_PERSON);
			oc.add(LdapConstants.ORGANIZATIONAL_PERSON);
			oc.add(LdapConstants.PERSON);
			oc.add(LdapConstants.TOP);
			oc.add(LdapConstants.USER_DETAILS);
			attributes.add(oc);

			BasicAttributes entry = new BasicAttributes();
			attributes.parallelStream().forEach(entry::put);
			context.createSubcontext(userDn.getName(), entry);

		} catch (NameAlreadyBoundException exception) {
			throw new AuthManagerException(AuthErrorCode.USER_ALREADY_EXIST.getErrorCode(),
					AuthErrorCode.USER_ALREADY_EXIST.getErrorMessage());
		} catch (NameNotFoundException exception) {
			rollbackUser(userDn, context);
			throw new AuthManagerException(AuthErrorCode.ROLE_NOT_FOUND.getErrorCode(),
					AuthErrorCode.ROLE_NOT_FOUND.getErrorMessage() + exception.getMessage());
		} catch (NamingException exception) {
			throw new AuthManagerException(AuthErrorCode.USER_CREATE_EXCEPTION.getErrorCode(),
					AuthErrorCode.USER_CREATE_EXCEPTION.getErrorMessage() + exception.getMessage());
		} catch (LdapInvalidDnException exception) {
			throw new AuthManagerException(AuthErrorCode.INVALID_DN.getErrorCode(),
					AuthErrorCode.INVALID_DN.getErrorMessage() + exception.getMessage());
		}
		try {
			Dn roleOccupant = createRoleDn(userCreationRequestDto.getRole());
			ModificationItem[] mods = new ModificationItem[1];
			mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE,
					new BasicAttribute(LdapConstants.ROLE_OCCUPANT, userDn.getName()));
			context.modifyAttributes(roleOccupant.getName(), mods);
		} catch (NameAlreadyBoundException exception) {
			rollbackUser(userDn, context);
			throw new AuthManagerException(AuthErrorCode.USER_ALREADY_EXIST.getErrorCode(),
					AuthErrorCode.USER_ALREADY_EXIST.getErrorMessage());
		} catch (NameNotFoundException exception) {
			rollbackUser(userDn, context);
			throw new AuthManagerException(AuthErrorCode.ROLE_NOT_FOUND.getErrorCode(),
					AuthErrorCode.ROLE_NOT_FOUND.getErrorMessage() + exception.getMessage());
		} catch (NamingException exception) {
			rollbackUser(userDn, context);
			throw new AuthManagerException(AuthErrorCode.USER_CREATE_EXCEPTION.getErrorCode(),
					AuthErrorCode.USER_CREATE_EXCEPTION.getErrorMessage() + exception.getMessage());
		} catch (LdapInvalidDnException exception) {
			rollbackUser(userDn, context);
			throw new AuthManagerException(AuthErrorCode.INVALID_DN.getErrorCode(),
					AuthErrorCode.INVALID_DN.getErrorMessage() + exception.getMessage());
		}
		return new UserRegistrationResponseDto(userCreationRequestDto.getUserName());

	}

	private DirContext getDirContext() throws NamingException {
		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, AuthConstant.LDAP_INITAL_CONTEXT_FACTORY);
		env.put(Context.PROVIDER_URL, "ldap://52.172.11.190:10389");
		env.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
		env.put(Context.SECURITY_CREDENTIALS, "secret");
		return new InitialDirContext(env);
	}

	@Override
	public UserPasswordResponseDto addPassword(UserPasswordRequestDto userPasswordRequestDto) {
		Dn userDn = null;
		DirContext context = null;
		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, AuthConstant.LDAP_INITAL_CONTEXT_FACTORY);
		env.put(Context.PROVIDER_URL, "ldap://52.172.11.190:10389");
		env.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
		env.put(Context.SECURITY_CREDENTIALS, "secret");
		try {
			userDn = createUserDn(userPasswordRequestDto.getUserName());
			context = new InitialDirContext(env);
			ModificationItem[] mods = new ModificationItem[3];
			mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE,
					new BasicAttribute(LdapConstants.RID, userPasswordRequestDto.getRid()));
			mods[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE,
					new BasicAttribute(LdapConstants.USER_PASSWORD, userPasswordRequestDto.getPassword()));
			mods[2] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute(LdapConstants.IS_ACTIVE, LdapConstants.TRUE));
			context.modifyAttributes(userDn.getName(), mods);

		} catch (NamingException exception) {
			throw new AuthManagerException(AuthErrorCode.USER_PASSWORD_EXCEPTION.getErrorCode(),
					AuthErrorCode.USER_PASSWORD_EXCEPTION.getErrorMessage() + exception.getMessage());
		} catch (LdapInvalidDnException exception) {
			throw new AuthManagerException(AuthErrorCode.INVALID_DN.getErrorCode(),
					AuthErrorCode.INVALID_DN.getErrorMessage() + exception.getMessage());
		}
		return new UserPasswordResponseDto(userPasswordRequestDto.getUserName());
	}

	private void rollbackUser(Dn userDn, DirContext context) {
		try {
			context.destroySubcontext(userDn.getName());
		} catch (NamingException exception) {
			throw new AuthManagerException(AuthErrorCode.ROLLBACK_USER_EXCEPTION.getErrorCode(),
					AuthErrorCode.ROLLBACK_USER_EXCEPTION.getErrorMessage());
		}
	}

	@Override
	public MosipUserDto getUserRoleByUserId(String username) throws Exception {
		LdapConnection ldapConnection = createAnonymousConnection();
		Dn userdn = createUserDn(username);
		MosipUserDto data = lookupUserDetails(userdn, ldapConnection);
		if (data == null) {
			throw new AuthManagerException(AuthErrorCode.USER_VALIDATION_ERROR.getErrorCode(),
					AuthErrorCode.USER_VALIDATION_ERROR.getErrorMessage());
		}
		ldapConnection.close();
		return data;
	}

	@Override
	public MosipUserDto getUserDetailBasedonMobileNumber(String mobileNumber) throws Exception {
		MosipUserDto mosipUserDto = new MosipUserDto();
		try {
			LdapContext context = getContext();
			NamingEnumeration<SearchResult> searchResult = getUserDetail(mobileNumber);

			while (searchResult.hasMore()) {
				Attributes attributes = searchResult.next().getAttributes();
				mosipUserDto.setUserId((String) attributes.get("uid").get());
				Dn searchBase = new Dn("ou=roles,c=morocco");
				String searchFilter = "(&(objectClass=organizationalRole)(roleOccupant=uid="
						+ (String) attributes.get("uid").get() + ",ou=people,c=morocco))";
				NamingEnumeration<SearchResult> searchResultRoles = context.search(searchBase.getName(), searchFilter,
						new SearchControls());
				Set<String> roles = new HashSet<String>();
				while (searchResultRoles.hasMore()) {
					Attributes attributeRoles = searchResultRoles.next().getAttributes();
					roles.add((String) attributeRoles.get("cn").get());
				}
				String rolesAsString = convertRolesToString(roles);
				mosipUserDto.setMail((String) attributes.get("mail").get());
				mosipUserDto.setMobile((String) attributes.get("mobile").get());
				mosipUserDto.setName((String) attributes.get("cn").get());
				mosipUserDto.setRole(rolesAsString);
				context.close();
			}
		} catch (NamingException e) {
			throw new AuthManagerException(AuthErrorCode.NAMING_EXCEPTION.getErrorCode(),
					AuthErrorCode.NAMING_EXCEPTION.getErrorMessage());
		} catch (LdapInvalidDnException e) {
			throw new AuthManagerException(AuthErrorCode.NAMING_EXCEPTION.getErrorCode(),
					AuthErrorCode.NAMING_EXCEPTION.getErrorMessage());
		}

		return mosipUserDto;
	}

	private NamingEnumeration<SearchResult> getUserDetail(String mobileNumber)
			throws LdapInvalidDnException, NamingException {
		Dn searchBase = new Dn("ou=people,c=morocco");
		String searchFilter = "(&(objectClass=organizationalPerson)(objectClass=inetOrgPerson)(objectClass=person)(mobile="
				+ mobileNumber + "))";
		LdapContext context = getContext();
		NamingEnumeration<SearchResult> searchResult = context.search(searchBase.getName(), searchFilter,
				new SearchControls());
		if (!searchResult.hasMore()) {
			throw new AuthManagerException("ADMN-ACM-MOB-NOT-FOUND", "Mobile is registered/not present");
		}
		context.close();
		return searchResult;
	}

	@Override
	public ValidationResponseDto validateUserName(String userId) {
		ValidationResponseDto validationResponseDto = new ValidationResponseDto();
		try {
			NamingEnumeration<SearchResult> searchResult = getUserDetailSearchResult(userId);
			while (searchResult.hasMore()) {
				Attributes attributes = searchResult.next().getAttributes();
				if (attributes.get("isActive") == null) {
					throw new AuthManagerException(AuthErrorCode.IS_ACTIVE_FLAG_NOT_FOUND.getErrorCode(),
							AuthErrorCode.IS_ACTIVE_FLAG_NOT_FOUND.getErrorMessage());
				}
				String isActive = (String) attributes.get("isActive").get();
				if (isActive.equalsIgnoreCase("true")) {
					validationResponseDto.setStatus("VALID");
				} else {
					validationResponseDto.setStatus("INVALID");
				}
			}

		} catch (NamingException e) {

			throw new AuthManagerException(AuthErrorCode.NAMING_EXCEPTION.getErrorCode(),
					AuthErrorCode.NAMING_EXCEPTION.getErrorMessage());
		} catch (LdapInvalidDnException e) {
			throw new AuthManagerException(AuthErrorCode.INVALID_DN.getErrorCode(),
					AuthErrorCode.INVALID_DN.getErrorMessage());
		}

		return validationResponseDto;
	}

	private NamingEnumeration<SearchResult> getUserDetailSearchResult(String userId)
			throws NamingException, LdapInvalidDnException {
		LdapContext context = getContext();
		Dn searchBase = new Dn("uid=" + userId + ",ou=people,c=morocco");
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<SearchResult> searchResult = context.search(searchBase.getName(),
				"(&(objectClass=organizationalPerson)(objectClass=inetOrgPerson)(objectClass=person))", searchControls);
		if (!searchResult.hasMore()) {
			throw new AuthManagerException(AuthErrorCode.USER_NOT_FOUND.getErrorCode(),
					AuthErrorCode.USER_NOT_FOUND.getErrorMessage());
		}
		context.close();
		return searchResult;
	}

	private void closeContext(LdapContext context) {
		try {
			Objects.requireNonNull(context,"context not initialized");
			context.close();
		} catch (NamingException e) {
			throw new AuthManagerException(AuthErrorCode.NAMING_EXCEPTION.getErrorCode(),
					AuthErrorCode.NAMING_EXCEPTION.getErrorMessage());
		}
	}
}
