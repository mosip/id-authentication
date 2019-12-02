package io.mosip.kernel.auth.repository.impl;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;

import org.apache.directory.api.ldap.model.password.PasswordDetails;
import org.apache.directory.api.ldap.model.password.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.mosip.kernel.auth.adapter.exception.AuthManagerException;
import io.mosip.kernel.auth.adapter.exception.AuthNException;
import io.mosip.kernel.auth.adapter.exception.AuthZException;
import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.constant.AuthErrorCode;
import io.mosip.kernel.auth.dto.AuthZResponseDto;
import io.mosip.kernel.auth.dto.ClientSecret;
import io.mosip.kernel.auth.dto.KeycloakPasswordDTO;
import io.mosip.kernel.auth.dto.KeycloakRequestDto;
import io.mosip.kernel.auth.dto.LoginUser;
import io.mosip.kernel.auth.dto.MosipUserDto;
import io.mosip.kernel.auth.dto.MosipUserListDto;
import io.mosip.kernel.auth.dto.MosipUserSalt;
import io.mosip.kernel.auth.dto.MosipUserSaltListDto;
import io.mosip.kernel.auth.dto.PasswordDto;
import io.mosip.kernel.auth.dto.RIdDto;
import io.mosip.kernel.auth.dto.Role;
import io.mosip.kernel.auth.dto.Roles;
import io.mosip.kernel.auth.dto.RolesListDto;
import io.mosip.kernel.auth.dto.UserDetailsResponseDto;
import io.mosip.kernel.auth.dto.UserNameDto;
import io.mosip.kernel.auth.dto.UserOtp;
import io.mosip.kernel.auth.dto.UserPasswordRequestDto;
import io.mosip.kernel.auth.dto.UserPasswordResponseDto;
import io.mosip.kernel.auth.dto.UserRegistrationRequestDto;
import io.mosip.kernel.auth.dto.ValidationResponseDto;
import io.mosip.kernel.auth.dto.otp.OtpUser;
import io.mosip.kernel.auth.repository.DataStore;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.util.CryptoUtil;

@Service
public class KeycloakImpl implements DataStore {
	
	private static final String INDIVIDUAL = "INDIVIDUAL";

	@Value("${mosip.kernel.base-url}")
	private String keycloakBaseUrl;

	@Value("${mosip.kernel.admin-url}")
	private String keycloakAdminUrl;

	@Value("${mosip.kernel.admin-realm-id}")
	private String adminRealmId;

	@Value("${mosip.kernel.realm-id}")
	private String realmId;

	@Value("${mosip.kernel.roles-url}")
	private String roles;

	@Value("${mosip.kernel.users-url}")
	private String users;

	@Value("${mosip.kernel.role-user-mapping-url}")
	private String roleUserMappingurl;
	
	@Value("${mosip.admin.individual_role_id}")
	private String individualRoleID;

	@Qualifier(value = "keycloakRestTemplate")
	@Autowired
	private RestTemplate restTemplate;

	@Value("${db_3_DS.keycloak.ipaddress}")
	private String keycloakHost;

	@Value("${db_3_DS.keycloak.port}")
	private String keycloakPort;

	@Value("${db_3_DS.keycloak.username}")
	private String keycloakUsername;

	@Value("${db_3_DS.keycloak.password}")
	private String keycloakPassword;

	@Value("${db_3_DS.keycloak.driverClassName}")
	private String keycloakDriver;
	
	
	@Value("${mosip.admin.pre-reg_user_password}")
	private String preRegUserPassword;
	
	@Value("${hikari.maximumPoolSize:25}")
	private int maximumPoolSize;
	@Value("${hikari.validationTimeout:3000}")
	private int validationTimeout;
	@Value("${hikari.connectionTimeout:60000}")
	private int connectionTimeout;
	@Value("${hikari.idleTimeout:200000}")
	private int idleTimeout;
	@Value("${hikari.minimumIdle:0}")
	private int minimumIdle;

	private NamedParameterJdbcTemplate jdbcTemplate;

	private static final String FETCH_ALL_SALTS = "select ue.username,ua.value from public.user_entity ue, public.user_attribute ua where ue.id=ua.user_id and ua.name='userPassword'"; // and
																																							// ue.username
																																							// IN
																																							// (:username)";

	private static final String FETCH_PASSWORD = "select cr.value from public.credential cr, public.user_entity ue where cr.user_id=ue.id and ue.username=:username";
	@Autowired
	private ObjectMapper objectMapper;

	@PostConstruct
	private void setup() {
		setUpConnection();
	}

	private void setUpConnection() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName(keycloakDriver);
		hikariConfig.setJdbcUrl(keycloakHost);
		hikariConfig.setUsername(keycloakUsername);
		hikariConfig.setPassword(keycloakPassword);
		hikariConfig.setMaximumPoolSize(maximumPoolSize);
		hikariConfig.setValidationTimeout(validationTimeout);
		hikariConfig.setConnectionTimeout(connectionTimeout);
		hikariConfig.setIdleTimeout(idleTimeout);
		hikariConfig.setMinimumIdle(minimumIdle);
		HikariDataSource dataSource = new HikariDataSource(hikariConfig);
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public RolesListDto getAllRoles() {

		Map<String, String> pathParams = new HashMap<>();
		pathParams.put(AuthConstant.REALM_ID, realmId);
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(keycloakAdminUrl + roles);
		HttpHeaders httpHeaders = new HttpHeaders();
		HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
		String response = callKeycloakService(uriComponentsBuilder.buildAndExpand(pathParams).toString(),
				HttpMethod.GET, httpEntity);
		List<Role> rolesList = new ArrayList<>();
		try {
			JsonNode node = objectMapper.readTree(response);
			for (JsonNode jsonNode : node) {
				Role role = new Role();
				String name = jsonNode.get("name").textValue();
				role.setRoleId(name);
				role.setRoleName(name);
				rolesList.add(role);
			}
		} catch (IOException e) {
			throw new AuthManagerException(AuthErrorCode.IO_EXCEPTION.getErrorCode(),
					AuthErrorCode.IO_EXCEPTION.getErrorMessage());
		}
		RolesListDto rolesListDto = new RolesListDto();
		rolesListDto.setRoles(rolesList);
		return rolesListDto;
	}

	@Override
	public MosipUserListDto getListOfUsersDetails(List<String> userDetails) throws Exception {
		List<MosipUserDto> mosipUserDtos = null;
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put(AuthConstant.REALM_ID, realmId);
		HttpEntity<String> httpEntity = new HttpEntity<>(null, new HttpHeaders());
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(keycloakAdminUrl + users);
		String response = callKeycloakService(uriComponentsBuilder.buildAndExpand(pathParams).toString(),
				HttpMethod.GET, httpEntity);
		try {
			JsonNode node = objectMapper.readTree(response);
			mosipUserDtos = mapUsersToUserDetailDto(node, userDetails);
		} catch (IOException e) {
			throw new AuthManagerException(AuthErrorCode.IO_EXCEPTION.getErrorCode(),
					AuthErrorCode.IO_EXCEPTION.getErrorMessage());
		}
		MosipUserListDto mosipUserListDto = new MosipUserListDto();
		mosipUserListDto.setMosipUserDtoList(mosipUserDtos);
		return mosipUserListDto;
	}

	
	@Override
	public MosipUserSaltListDto getAllUserDetailsWithSalt() throws Exception {
		return jdbcTemplate.query(FETCH_ALL_SALTS, new MapSqlParameterSource(),
				new ResultSetExtractor<MosipUserSaltListDto>() {

					@Override
					public MosipUserSaltListDto extractData(ResultSet rs) throws SQLException, DataAccessException {
						MosipUserSaltListDto mosipUserSaltListDto = new MosipUserSaltListDto();
						List<MosipUserSalt> mosipUserSaltList = new ArrayList<>();
						while (rs.next()) {
							MosipUserSalt mosipUserSalt = new MosipUserSalt();
							mosipUserSalt.setUserId(rs.getString("username"));
							//System.out.println(rs.getString("value"));
							PasswordDetails password = PasswordUtil.splitCredentials(CryptoUtil.decodeBase64(rs.getString("value")));
							//System.out.println(rs.getString("username"));
							//System.out.println("userPasword salt= "+rs.getString("value"));
							//System.out.println("password salt= "+DatatypeConverter.printHexBinary(password.getPassword()));
							//System.out.println("salt salt= "+CryptoUtil.encodeBase64String(password.getSalt()));
							//mosipUserSalt.setSalt(CryptoUtil.encodeBase64(password.getSalt()));
							mosipUserSalt.setSalt(CryptoUtil.encodeBase64String(password.getSalt()));
							//System.out.println(mosipUserSalt.getSalt());
							mosipUserSaltList.add(mosipUserSalt);
						}
						mosipUserSaltListDto.setMosipUserSaltList(mosipUserSaltList);
						return mosipUserSaltListDto;
					}

				});
	}

	@Override
	public RIdDto getRidFromUserId(String userId) throws Exception {
		RIdDto rIdDto = new RIdDto();
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put(AuthConstant.REALM_ID, realmId);
		HttpHeaders httpHeaders = new HttpHeaders();
		HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(keycloakAdminUrl + users);
		String response = callKeycloakService(uriComponentsBuilder.buildAndExpand(pathParams).toString(),
				HttpMethod.GET, httpEntity);
		try {
			JsonNode node = objectMapper.readTree(response);
			for (JsonNode jsonNode : node) {
				if (jsonNode.get("username").textValue().equals(userId)) {
					JsonNode attriNode = jsonNode.get("attributes");
					String rid = attriNode.get("rid").get(0).textValue();
					rIdDto.setRId(rid);
					break;
				}
			}

		} catch (IOException e) {
			throw new AuthManagerException(AuthErrorCode.IO_EXCEPTION.getErrorCode(),
					AuthErrorCode.IO_EXCEPTION.getErrorMessage());
		}

		return rIdDto;

	}

	@Override
	public AuthZResponseDto unBlockAccount(String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MosipUserDto registerUser(UserRegistrationRequestDto userId) {
		Map<String, String> pathParams = new HashMap<>();
		KeycloakRequestDto keycloakRequestDto = mapUserRequestToKeycloakRequestDto(userId);
		pathParams.put(AuthConstant.REALM_ID, realmId);
		HttpEntity<KeycloakRequestDto> httpEntity = new HttpEntity<KeycloakRequestDto>(keycloakRequestDto);
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(keycloakAdminUrl + users);
		if (!isUserAlreadyPresent(userId.getUserName())) {
			callKeycloakService(uriComponentsBuilder.buildAndExpand(pathParams).toString(), HttpMethod.POST,
					httpEntity);
			if (keycloakRequestDto.getRealmRoles().contains(INDIVIDUAL)) {
				String userID = getIDfromUserID(userId.getUserName());
				roleMapper(userID);
			}
		}

		MosipUserDto mosipUserDTO = new MosipUserDto();
		mosipUserDTO.setUserId(userId.getUserName());
		return mosipUserDTO;

	}

	private void roleMapper(String userID) {
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put(AuthConstant.REALM_ID, realmId);
		pathParams.put("userID", userID);
		Roles role = new Roles(individualRoleID, INDIVIDUAL);
		List<Roles> roles = new ArrayList<>();
		roles.add(role);
		pathParams.put(AuthConstant.REALM_ID, realmId);
		HttpEntity<List<Roles>> httpEntity = new HttpEntity<>(roles);
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
				.fromUriString(keycloakAdminUrl + users + "/{userID}/role-mappings/realm");
		String response = callKeycloakService(uriComponentsBuilder.buildAndExpand(pathParams).toString(),
				HttpMethod.POST, httpEntity);
	}

	private String getIDfromUserID(String userName) {
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put(AuthConstant.REALM_ID, realmId);
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
				.fromUriString(keycloakAdminUrl + users + "?username=" + userName);
		String response = callKeycloakService(uriComponentsBuilder.buildAndExpand(pathParams).toString(),
				HttpMethod.GET, null);
		JsonNode jsonNodes;
		try {
			if (response == null) {
				return null;
			}
			jsonNodes = objectMapper.readTree(response);
		} catch (IOException e) {
			throw new AuthManagerException(AuthErrorCode.IO_EXCEPTION.getErrorCode(),
					AuthErrorCode.IO_EXCEPTION.getErrorMessage());
		}
		if (jsonNodes.size() > 0) {
			for (JsonNode jsonNode : jsonNodes) {
				if (userName.equals(jsonNode.get("username").asText())) {
					return jsonNode.get("id").asText();
				}
			}

		}
		return null;
	}

	/**
	 * Checks if is user already present.
	 *
	 * @param userName the user name
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public boolean isUserAlreadyPresent(String userName) {
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put(AuthConstant.REALM_ID, realmId);
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
				.fromUriString(keycloakAdminUrl + users + "?username=" + userName);
		String response = callKeycloakService(uriComponentsBuilder.buildAndExpand(pathParams).toString(),
				HttpMethod.GET, null);
		JsonNode jsonNodes;
		try {
			if (response == null) {
				return false;
			}
			jsonNodes = objectMapper.readTree(response);
		} catch (IOException e) {
			throw new AuthManagerException(AuthErrorCode.IO_EXCEPTION.getErrorCode(),
					AuthErrorCode.IO_EXCEPTION.getErrorMessage());
		}
		if (jsonNodes.size() > 0) {
			for (JsonNode jsonNode : jsonNodes) {
				if (userName.equals(jsonNode.get("username").asText())) {
					return true;
				}
			}

		}
		return false;
	}

	private KeycloakRequestDto mapUserRequestToKeycloakRequestDto(UserRegistrationRequestDto userRegDto) {
		KeycloakRequestDto keycloakRequestDto = new KeycloakRequestDto();
		List<String> roles = new ArrayList<>();
		List<KeycloakPasswordDTO> credentialObject = null;
		KeycloakPasswordDTO dto = null;
		if (userRegDto.getAppId().equalsIgnoreCase("preregistration")) {
			roles.add(INDIVIDUAL);
			credentialObject = new ArrayList<>();
			dto = new KeycloakPasswordDTO();
			dto.setType(AuthConstant.PASSWORDCONSTANT);
			dto.setValue(preRegUserPassword);
		} else if (userRegDto.getAppId().equalsIgnoreCase("registrationclient")) {
			credentialObject = new ArrayList<>();
			dto = new KeycloakPasswordDTO();
			dto.setType(AuthConstant.PASSWORDCONSTANT);
			dto.setValue(userRegDto.getUserPassword());
		}
		credentialObject.add(dto);
		List<Object> contactNoList = new ArrayList<>();
		List<Object> genderList = new ArrayList<>();
		genderList.add(userRegDto.getGender());
		contactNoList.add(userRegDto.getContactNo());
		HashMap<String, List<Object>> attributes = new HashMap<>();
		attributes.put("mobile", contactNoList);

		attributes.put("gender", genderList);
		keycloakRequestDto.setUsername(userRegDto.getUserName());
		keycloakRequestDto.setFirstName(userRegDto.getFirstName());
		keycloakRequestDto.setEmail(userRegDto.getEmailID());
		keycloakRequestDto.setRealmRoles(roles);
		keycloakRequestDto.setAttributes(attributes);
		keycloakRequestDto.setEnabled(true);
		if (credentialObject != null) {
			keycloakRequestDto.setCredentials(credentialObject);
		}
		return keycloakRequestDto;
	}

	private void KeycloakRequestDtomapUserRequestToKeycloakRequestDto(UserRegistrationRequestDto userId) {
		// TODO Auto-generated method stub

	}

	@Override
	public UserPasswordResponseDto addPassword(UserPasswordRequestDto userPasswordRequestDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuthZResponseDto changePassword(PasswordDto passwordDto) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuthZResponseDto resetPassword(PasswordDto passwordDto) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserNameDto getUserNameBasedOnMobileNumber(String mobileNumber) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MosipUserDto authenticateUser(LoginUser loginUser) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MosipUserDto authenticateWithOtp(OtpUser otpUser) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MosipUserDto authenticateUserWithOtp(UserOtp loginUser) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MosipUserDto authenticateWithSecretKey(ClientSecret clientSecret) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MosipUserDto getUserRoleByUserId(String username) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MosipUserDto getUserDetailBasedonMobileNumber(String mobileNumber) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValidationResponseDto validateUserName(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDetailsResponseDto getUserDetailBasedOnUid(List<String> userIds) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Call keycloak service.
	 *
	 * @param url           the url
	 * @param httpMethod    the http method
	 * @param requestEntity the request entity
	 * @return the string
	 */
	private String callKeycloakService(String url, HttpMethod httpMethod, HttpEntity<?> requestEntity) {
		ResponseEntity<String> responseEntity = null;
		String response = null;
		try {

			responseEntity = restTemplate.exchange(url, httpMethod, requestEntity, String.class);
		} catch (HttpServerErrorException | HttpClientErrorException ex) {
			List<ServiceError> validationErrorsList = ExceptionUtils.getServiceErrorList(ex.getResponseBodyAsString());

			if (ex.getRawStatusCode() == 401) {
				if (!validationErrorsList.isEmpty()) {
					throw new AuthNException(validationErrorsList);
				} else {
					throw new BadCredentialsException("Authentication failed from AuthManager");
				}
			}
			if (ex.getRawStatusCode() == 403) {
				if (!validationErrorsList.isEmpty()) {
					throw new AuthZException(validationErrorsList);
				} else {
					throw new AccessDeniedException("Access denied from AuthManager");
				}
			}

		}
		if (responseEntity != null && responseEntity.hasBody() && responseEntity.getStatusCode() == HttpStatus.OK) {
			response = responseEntity.getBody();
		}

		return response;
	}

	/**
	 * Map users to user detail dto.
	 *
	 * @param node        the node
	 * @param userDetails
	 * @return the list
	 */
	private List<MosipUserDto> mapUsersToUserDetailDto(JsonNode node, List<String> userDetails) {
		MosipUserDto mosipUserDto = null;
		List<MosipUserDto> mosipUserDtos = new ArrayList<>();
		String roles = null;
		for (JsonNode jsonNode : node) {
			mosipUserDto = new MosipUserDto();
			String userName = jsonNode.get("username").textValue();

			if (userDetails.stream().anyMatch(user -> user.equals(userName))) {
				String email = jsonNode.get("email").textValue();
				JsonNode attributeNodes = jsonNode.get("attributes");
				String userPassword = attributeNodes.get("userPassword").get(0).asText();
				//System.out.println("Password : "+userPassword);
				PasswordDetails password = PasswordUtil
						.splitCredentials(CryptoUtil.decodeBase64(userPassword));
				//System.out.println("userPasword userdetails= "+userPassword);
				//System.out.println("password userdetails= "+DatatypeConverter.printHexBinary(password.getPassword()));
				//System.out.println("salt userdetails= "+CryptoUtil.encodeBase64(password.getSalt()));
				//userPassword = CryptoUtil.encodeBase64(password.getPassword());
				//System.out.println("a = "+HMACUtils.digestAsPlainTextWithSalt(password.getPassword(), password.getSalt()));
				//System.out.println("b = "+HMACUtils.digestAsPlainTextWithSalt("mosip".getBytes(), password.getSalt()));
				//System.out.println(Strings.utf8ToString(password.getPassword()));
				userPassword = DatatypeConverter.printHexBinary(password.getPassword());
				String mobile = null;
				String rid = null;
				String name = jsonNode.get("firstName").asText()+" "+jsonNode.get("lastName").asText();
				try {
					roles = getRolesAsString(jsonNode.get("id").textValue());
				} catch (IOException e) {
					throw new AuthManagerException(AuthErrorCode.IO_EXCEPTION.getErrorCode(),
							AuthErrorCode.IO_EXCEPTION.getErrorMessage());
				}
				//userPassword = getPasswordFromDatabase(userName);
				if (attributeNodes.get("mobile") != null) {
					mobile = attributeNodes.get("mobile").get(0).textValue();
				}
				if (attributeNodes.get("name") != null) {
					name = jsonNode.get("firstName").asText()+" "+jsonNode.get("lastName").asText();
				}
				if (attributeNodes.get("rid") != null) {
					rid = attributeNodes.get("rid").get(0).textValue();
				}

				mosipUserDto.setMail(email);
				mosipUserDto.setMobile(mobile);
				mosipUserDto.setRId(rid);
				mosipUserDto.setUserId(userName);
				mosipUserDto.setName(name);
				mosipUserDto.setUserPassword(userPassword);
				mosipUserDto.setRole(roles);
				mosipUserDtos.add(mosipUserDto);
			}
		}

		return mosipUserDtos;

	}

	private String getPasswordFromDatabase(String userName) {
		return jdbcTemplate.query(FETCH_PASSWORD, new MapSqlParameterSource().addValue("username", userName),
				new ResultSetExtractor<String>() {

					@Override
					public String extractData(ResultSet rs) throws SQLException, DataAccessException {
						String pwd = null;
						while (rs.next()) {
							pwd = rs.getString("value");

						}
						return pwd;
					}
				});
	}

	/**
	 * Gets the roles as string.
	 *
	 * @param userId the id generated by keycloak for that user not username or
	 *               userid
	 * @return role as string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String getRolesAsString(String userId) throws IOException {
		StringBuilder roleBuilder = new StringBuilder();
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put(AuthConstant.REALM_ID, realmId);
		pathParams.put("userId", userId);
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
				.fromUriString(keycloakAdminUrl + users + roleUserMappingurl);
		HttpHeaders httpHeaders = new HttpHeaders();
		HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
		String response = callKeycloakService(uriComponentsBuilder.buildAndExpand(pathParams).toString(),
				HttpMethod.GET, httpEntity);
		JsonNode jsonNode = objectMapper.readTree(response);
		for (JsonNode node : jsonNode) {
			String role = node.get("name").textValue();
			Objects.nonNull(role);
			roleBuilder.append(role).append(AuthConstant.COMMA);
		}
		return roleBuilder.length() > 0 ? roleBuilder.substring(0, roleBuilder.length() - 1) : "";
	}
}
