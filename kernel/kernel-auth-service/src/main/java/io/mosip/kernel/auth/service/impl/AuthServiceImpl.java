package io.mosip.kernel.auth.service.impl;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.adapter.constant.AuthAdapterConstant;
import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.constant.AuthErrorCode;
import io.mosip.kernel.auth.constant.KeycloakConstants;
import io.mosip.kernel.auth.dto.AccessTokenResponse;
import io.mosip.kernel.auth.dto.AccessTokenResponseDTO;
import io.mosip.kernel.auth.dto.AuthNResponse;
import io.mosip.kernel.auth.dto.AuthNResponseDto;
import io.mosip.kernel.auth.dto.AuthResponseDto;
import io.mosip.kernel.auth.dto.AuthToken;
import io.mosip.kernel.auth.dto.AuthZResponseDto;
import io.mosip.kernel.auth.dto.BasicTokenDto;
import io.mosip.kernel.auth.dto.ClientSecret;
import io.mosip.kernel.auth.dto.KeycloakErrorResponseDto;
import io.mosip.kernel.auth.dto.LoginUser;
import io.mosip.kernel.auth.dto.MosipUserDto;
import io.mosip.kernel.auth.dto.MosipUserListDto;
import io.mosip.kernel.auth.dto.MosipUserSaltListDto;
import io.mosip.kernel.auth.dto.MosipUserTokenDto;
import io.mosip.kernel.auth.dto.PasswordDto;
import io.mosip.kernel.auth.dto.RIdDto;
import io.mosip.kernel.auth.dto.RealmAccessDto;
import io.mosip.kernel.auth.dto.RolesListDto;
import io.mosip.kernel.auth.dto.TimeToken;
import io.mosip.kernel.auth.dto.UserDetailsResponseDto;
import io.mosip.kernel.auth.dto.UserNameDto;
import io.mosip.kernel.auth.dto.UserOtp;
import io.mosip.kernel.auth.dto.UserPasswordRequestDto;
import io.mosip.kernel.auth.dto.UserPasswordResponseDto;
import io.mosip.kernel.auth.dto.UserRegistrationRequestDto;
import io.mosip.kernel.auth.dto.UserRegistrationResponseDto;
import io.mosip.kernel.auth.dto.UserRoleDto;
import io.mosip.kernel.auth.dto.ValidationResponseDto;
import io.mosip.kernel.auth.dto.otp.OtpUser;
import io.mosip.kernel.auth.exception.AuthManagerException;
import io.mosip.kernel.auth.exception.LoginException;
import io.mosip.kernel.auth.repository.UserStoreFactory;
import io.mosip.kernel.auth.service.AuthService;
import io.mosip.kernel.auth.service.OTPService;
import io.mosip.kernel.auth.service.TokenService;
import io.mosip.kernel.auth.service.UinService;
import io.mosip.kernel.auth.util.TokenGenerator;
import io.mosip.kernel.auth.util.TokenValidator;
import io.mosip.kernel.core.util.EmptyCheckUtils;

/**
 * Auth Service for Authentication and Authorization
 * 
 * @author Ramadurai Pandian
 * @author Urvil Joshi
 * @author Srinivasan
 *
 */

@Component
public class AuthServiceImpl implements AuthService {

	private static final String LOG_OUT_FAILED = "log out failed";

	private static final String FAILED = "Failed";

	private static final String SUCCESS = "Success";

	private static final String SUCCESSFULLY_LOGGED_OUT = "successfully loggedout";

	@Autowired
	UserStoreFactory userStoreFactory;

	@Autowired
	TokenGenerator tokenGenerator;

	@Autowired
	TokenValidator tokenValidator;

	@Autowired
	TokenService customTokenServices;

	@Autowired
	OTPService oTPService;

	@Autowired
	UinService uinService;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	MosipEnvironment mosipEnvironment;

	@Autowired
	ObjectMapper objectmapper;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${mosip.kernel.open-id-uri}")
	private String openIdUrl;

	@Value("${mosip.admin.login_flow.name}")
	private String loginFlowName;

	@Value("${mosip.admin.clientid}")
	private String clientID;

	@Value("${mosip.admin.clientsecret}")
	private String clientSecret;

	@Value("${mosip.admin.redirecturi}")
	private String redirectURI;

	@Value("${mosip.admin.login_flow.scope}")
	private String scope;

	@Value("${mosip.admin.login_flow.response_type}")
	private String responseType;

	@Value("${mosip.keycloak.authorization_endpoint}")
	private String authorizationEndpoint;

	@Value("${mosip.keycloak.token_endpoint}")
	private String tokenEndpoint;
	
	@Value("${mosip.admin_realm_id}")
	private String realmID;

	/**
	 * Method used for validating Auth token
	 * 
	 * @param token
	 *            token
	 * 
	 * @return mosipUserDtoToken is of type {@link MosipUserTokenDto}
	 * 
	 * @throws Exception
	 *             exception
	 * 
	 */

	@Override
	public MosipUserTokenDto validateToken(String token) throws Exception {
		// long currentTime = Instant.now().toEpochMilli();
		MosipUserTokenDto mosipUserDtoToken = tokenValidator.validateToken(token);
		//AuthToken authToken = customTokenServices.getTokenDetails(token);
//		if (authToken == null) {
//			throw new AuthManagerException(AuthErrorCode.INVALID_TOKEN.getErrorCode(),
//					AuthErrorCode.INVALID_TOKEN.getErrorMessage());
//		}
		/*
		 * AuthToken authToken = customTokenServices.getTokenDetails(token); if
		 * (authToken == null) { throw new
		 * AuthManagerException(AuthConstant.UNAUTHORIZED_CODE,
		 * "Auth token has been changed,Please try with new login"); } long tenMinsExp =
		 * getExpiryTime(authToken.getExpirationTime()); if (currentTime > tenMinsExp &&
		 * currentTime < authToken.getExpirationTime()) { TimeToken newToken =
		 * tokenGenerator.generateNewToken(token);
		 * mosipUserDtoToken.setToken(newToken.getToken());
		 * mosipUserDtoToken.setExpTime(newToken.getExpTime()); AuthToken newAuthToken =
		 * getAuthToken(mosipUserDtoToken);
		 * customTokenServices.StoreToken(newAuthToken); return mosipUserDtoToken; }
		 */
		if (mosipUserDtoToken != null /* && (currentTime < authToken.getExpirationTime()) */) {
			return mosipUserDtoToken;
		} else {
			throw new NonceExpiredException(AuthConstant.AUTH_TOKEN_EXPIRED_MESSAGE);
		}
	}

	private AuthToken getAuthToken(MosipUserTokenDto mosipUserDtoToken) {
		return new AuthToken(mosipUserDtoToken.getMosipUserDto().getUserId(), mosipUserDtoToken.getToken(),
				mosipUserDtoToken.getExpTime(), mosipUserDtoToken.getRefreshToken());
	}

	private long getExpiryTime(long expirationTime) {
		Instant ins = Instant.ofEpochMilli(expirationTime);
		ins = ins.plus(mosipEnvironment.getAuthSlidingWindowExp(), ChronoUnit.MINUTES);
		return ins.toEpochMilli();
	}

	/**
	 * Method used for Authenticating User based on username and password
	 * 
	 * @param loginUser
	 *            is of type {@link LoginUser}
	 * 
	 * @return authNResponseDto is of type {@link AuthNResponseDto}
	 * 
	 * @throws Exception
	 *             exception
	 * 
	 */

	@Override
	public AuthNResponseDto authenticateUser(LoginUser loginUser) throws Exception {
		AuthNResponseDto authNResponseDto = null;
		MosipUserDto mosipUser = userStoreFactory.getDataStoreBasedOnApp(loginUser.getAppId())
				.authenticateUser(loginUser);
		BasicTokenDto basicTokenDto = tokenGenerator.basicGenerate(mosipUser);
		if (basicTokenDto != null) {
			authNResponseDto = new AuthNResponseDto();
			authNResponseDto.setToken(basicTokenDto.getAuthToken());
			authNResponseDto.setUserId(mosipUser.getUserId());
			authNResponseDto.setRefreshToken(basicTokenDto.getRefreshToken());
			authNResponseDto.setExpiryTime(basicTokenDto.getExpiryTime());
			authNResponseDto.setStatus(AuthConstant.SUCCESS_STATUS);
			authNResponseDto.setMessage(AuthConstant.USERPWD_SUCCESS_MESSAGE);
		}
		return authNResponseDto;
	}

	/**
	 * Method used for sending OTP
	 * 
	 * @param otpUser
	 *            is of type {@link OtpUser}
	 * 
	 * @return authNResponseDto is of type {@link AuthNResponseDto}
	 * 
	 * @throws Exception
	 *             exception
	 * 
	 */

	@Override
	public AuthNResponseDto authenticateWithOtp(OtpUser otpUser) throws Exception {
		AuthNResponseDto authNResponseDto = null;
		MosipUserDto mosipUser = null;
		otpUser.getOtpChannel().replaceAll(String::toLowerCase);
		otpUser.setOtpChannel(otpUser.getOtpChannel());
		if (AuthConstant.APPTYPE_UIN.equals(otpUser.getUseridtype())) {
			mosipUser = uinService.getDetailsFromUin(otpUser);
			authNResponseDto = oTPService.sendOTP(mosipUser, otpUser);
			authNResponseDto.setStatus(authNResponseDto.getStatus());
			authNResponseDto.setMessage(authNResponseDto.getMessage());
		} else if (AuthConstant.APPTYPE_USERID.equals(otpUser.getUseridtype())) {
			mosipUser = userStoreFactory.getDataStoreBasedOnApp(otpUser.getAppId()).authenticateWithOtp(otpUser);
			authNResponseDto = oTPService.sendOTP(mosipUser, otpUser);
			authNResponseDto.setStatus(authNResponseDto.getStatus());
			authNResponseDto.setMessage(authNResponseDto.getMessage());
		} else {
			throw new AuthManagerException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Invalid User Id type");
		}
		return authNResponseDto;
	}

	/**
	 * Method used for Authenticating User based with username and OTP
	 * 
	 * @param userOtp
	 *            is of type {@link UserOtp}
	 * 
	 * @return authNResponseDto is of type {@link AuthNResponseDto}
	 * 
	 * @throws Exception
	 *             exception
	 * 
	 */

	@Override
	public AuthNResponseDto authenticateUserWithOtp(UserOtp userOtp) throws Exception {
		AuthNResponseDto authNResponseDto = new AuthNResponseDto();
		MosipUserTokenDto mosipToken = null;
		MosipUserDto mosipUser = userStoreFactory.getDataStoreBasedOnApp(userOtp.getAppId())
				.authenticateUserWithOtp(userOtp);
		if (mosipUser == null && AuthConstant.IDA.toLowerCase().equals(userOtp.getAppId().toLowerCase())) {
			mosipUser = uinService.getDetailsForValidateOtp(userOtp.getUserId());
		}
		if (mosipUser != null) {
			mosipToken = oTPService.validateOTP(mosipUser, userOtp.getOtp());
		} else {
			throw new AuthManagerException(AuthErrorCode.USER_VALIDATION_ERROR.getErrorCode(),
					AuthErrorCode.USER_VALIDATION_ERROR.getErrorMessage());
		}
		if (mosipToken != null && mosipToken.getMosipUserDto() != null) {
			authNResponseDto.setMessage(mosipToken.getMessage());
			authNResponseDto.setStatus(mosipToken.getStatus());
			authNResponseDto.setToken(mosipToken.getToken());
			authNResponseDto.setExpiryTime(mosipToken.getExpTime());
			authNResponseDto.setRefreshToken(mosipToken.getRefreshToken());
			authNResponseDto.setUserId(mosipToken.getMosipUserDto().getUserId());
		} else {
			authNResponseDto.setMessage(mosipToken.getMessage());
			authNResponseDto.setStatus(mosipToken.getStatus());
		}
		return authNResponseDto;
	}

	/**
	 * Method used for Authenticating User based with secretkey and password
	 * 
	 * @param clientSecret
	 *            is of type {@link ClientSecret}
	 * 
	 * @return authNResponseDto is of type {@link AuthNResponseDto}
	 * 
	 * @throws Exception
	 *             exception
	 * 
	 */

	@Override
	public AuthNResponseDto authenticateWithSecretKey(ClientSecret clientSecret) throws Exception {
		AuthNResponseDto authNResponseDto = null;
		BasicTokenDto basicTokenDto = null;
		AuthToken authToken = null;
		MosipUserDto mosipUser = userStoreFactory.getDataStoreBasedOnApp(clientSecret.getAppId())
				.authenticateWithSecretKey(clientSecret);
		if (mosipUser == null) {
			throw new AuthManagerException(AuthErrorCode.USER_VALIDATION_ERROR.getErrorCode(),
					AuthErrorCode.USER_VALIDATION_ERROR.getErrorMessage());
		}
		if (mosipUser != null) {
			MosipUserTokenDto mosipToken = null;
			authToken = customTokenServices.getTokenBasedOnName(clientSecret.getClientId());
			try {
				if (authToken != null) {
					mosipToken = validateToken(authToken.getAccessToken());
				}

			} catch (AuthManagerException auth) {
				if (auth.getErrorCode().equals(AuthErrorCode.TOKEN_EXPIRED.getErrorCode())) {
					mosipToken = null;
					System.out.println("Token expired for user " + authToken);
				} else {
					throw new AuthManagerException(auth.getErrorCode(), auth.getMessage(), auth);
				}
			}
			if (authToken != null && mosipToken != null) {
				System.out.println("Token not expired old token ");
				authNResponseDto = new AuthNResponseDto();
				authNResponseDto.setToken(authToken.getAccessToken());
				authNResponseDto.setUserId(mosipUser.getUserId());
				authNResponseDto.setRefreshToken(authToken.getRefreshToken());
				authNResponseDto.setExpiryTime(authToken.getExpirationTime());
				authNResponseDto.setStatus(AuthConstant.SUCCESS_STATUS);
				authNResponseDto.setMessage(AuthConstant.CLIENT_SECRET_SUCCESS_MESSAGE);
			} else {
				System.out.println("New Token generation ");
				basicTokenDto = tokenGenerator.basicGenerate(mosipUser);
				if (basicTokenDto != null) {
					authNResponseDto = new AuthNResponseDto();
					authNResponseDto.setToken(basicTokenDto.getAuthToken());
					authNResponseDto.setUserId(mosipUser.getUserId());
					authNResponseDto.setRefreshToken(basicTokenDto.getRefreshToken());
					authNResponseDto.setExpiryTime(basicTokenDto.getExpiryTime());
					authNResponseDto.setStatus(AuthConstant.SUCCESS_STATUS);
					authNResponseDto.setMessage(AuthConstant.CLIENT_SECRET_SUCCESS_MESSAGE);
					AuthToken newAuthToken = getAuthToken(authNResponseDto);
					customTokenServices.StoreToken(newAuthToken);
				}

			}
		}
		return authNResponseDto;
	}

	private AuthToken getAuthToken(AuthNResponseDto authResponseDto) {
		return new AuthToken(authResponseDto.getUserId(), authResponseDto.getToken(), authResponseDto.getExpiryTime(),
				authResponseDto.getRefreshToken());
	}

	/**
	 * Method used for generating refresh token
	 * 
	 * @param existingToken
	 *            existing token
	 * 
	 * @return mosipUserDtoToken is of type {@link MosipUserTokenDto}
	 * 
	 * @throws Exception
	 *             exception
	 * 
	 */

	@Override
	public MosipUserTokenDto retryToken(String existingToken) throws Exception {
		MosipUserTokenDto mosipUserDtoToken = null;
		boolean checkRefreshToken = false;
		AuthToken accessToken = customTokenServices.getTokenDetails(existingToken);
		if (accessToken != null) {
			if (accessToken.getRefreshToken() != null) {
				checkRefreshToken = tokenValidator.validateExpiry(accessToken.getRefreshToken());
			}
			if (checkRefreshToken) {
				TimeToken newAccessToken = tokenGenerator.generateNewToken(accessToken.getRefreshToken());
				AuthToken updatedAccessToken = customTokenServices.getUpdatedAccessToken(accessToken.getUserId(),
						newAccessToken, accessToken.getUserId());
				mosipUserDtoToken = tokenValidator.validateToken(updatedAccessToken.getAccessToken());
			} else {
				throw new RuntimeException("Refresh Token Expired");
			}
		} else {
			throw new RuntimeException("Token doesn't exist");
		}
		return mosipUserDtoToken;
	}

	/**
	 * Method used for invalidate token
	 * 
	 * @param token
	 *            token
	 * 
	 * @return authNResponse is of type {@link AuthNResponse}
	 * 
	 * @throws Exception
	 *             exception
	 * 
	 */

	@Override
	public AuthNResponse invalidateToken(String token) throws Exception {
		AuthNResponse authNResponse = null;
		customTokenServices.revokeToken(token);
		authNResponse = new AuthNResponse();
		authNResponse.setStatus(AuthConstant.SUCCESS_STATUS);
		authNResponse.setMessage(AuthConstant.TOKEN_INVALID_MESSAGE);
		return authNResponse;
	}

	@Override
	public RolesListDto getAllRoles(String appId) {
		RolesListDto rolesListDto = userStoreFactory.getDataStoreBasedOnApp(appId).getAllRoles();
		return rolesListDto;
	}

	@Override
	public MosipUserListDto getListOfUsersDetails(List<String> userDetails, String appId) throws Exception {
		MosipUserListDto mosipUserListDto = userStoreFactory.getDataStoreBasedOnApp(appId)
				.getListOfUsersDetails(userDetails);
		return mosipUserListDto;
	}

	@Override
	public MosipUserSaltListDto getAllUserDetailsWithSalt(String appId) throws Exception {
		MosipUserSaltListDto mosipUserListDto = userStoreFactory.getDataStoreBasedOnApp(appId)
				.getAllUserDetailsWithSalt();
		return mosipUserListDto;
	}

	@Override
	public RIdDto getRidBasedOnUid(String userId, String appId) throws Exception {
		return userStoreFactory.getDataStoreBasedOnApp(appId).getRidFromUserId(userId);

	}

	@Override
	public AuthZResponseDto unBlockUser(String userId, String appId) throws Exception {
		return userStoreFactory.getDataStoreBasedOnApp(appId).unBlockAccount(userId);
	}

	@Override
	public AuthZResponseDto changePassword(String appId, PasswordDto passwordDto) throws Exception {
		return userStoreFactory.getDataStoreBasedOnApp(appId).changePassword(passwordDto);
	}

	@Override
	public AuthZResponseDto resetPassword(String appId, PasswordDto passwordDto) throws Exception {
		return userStoreFactory.getDataStoreBasedOnApp(appId).resetPassword(passwordDto);
	}

	@Override
	public UserNameDto getUserNameBasedOnMobileNumber(String appId, String mobileNumber) throws Exception {
		return userStoreFactory.getDataStoreBasedOnApp("registrationclient")
				.getUserNameBasedOnMobileNumber(mobileNumber);

	}

	@Override
	public UserRegistrationResponseDto registerUser(UserRegistrationRequestDto userCreationRequestDto) {
		return userStoreFactory.getDataStoreBasedOnApp(userCreationRequestDto.getAppId())
				.registerUser(userCreationRequestDto);
	}

	@Override
	public UserPasswordResponseDto addUserPassword(UserPasswordRequestDto userPasswordRequestDto) {
		return userStoreFactory.getDataStoreBasedOnApp(userPasswordRequestDto.getAppId())
				.addPassword(userPasswordRequestDto);
	}

	@Override
	public UserRoleDto getUserRole(String appId, String userId) throws Exception {
		MosipUserDto mosipuser = null;
		mosipuser = userStoreFactory.getDataStoreBasedOnApp(appId).getUserRoleByUserId(userId);
		UserRoleDto userRole = new UserRoleDto();
		userRole.setUserId(mosipuser.getUserId());
		userRole.setRole(mosipuser.getRole());
		return userRole;
	}

	@Override
	public MosipUserDto getUserDetailBasedonMobileNumber(String appId, String mobileNumber) throws Exception {

		return userStoreFactory.getDataStoreBasedOnApp(appId).getUserDetailBasedonMobileNumber(mobileNumber);
	}

	@Override
	public ValidationResponseDto validateUserName(String appId, String userName) {
		return userStoreFactory.getDataStoreBasedOnApp(appId).validateUserName(userName);
	}

	@Override
	public UserDetailsResponseDto getUserDetailBasedOnUserId(String appId, List<String> userIds) {
		return userStoreFactory.getDataStoreBasedOnApp(appId).getUserDetailBasedOnUid(userIds);
	}

	@Override
	public MosipUserDto valdiateToken(String token) {
		Map<String, String> pathparams = new HashMap<>();
		
		if(EmptyCheckUtils.isNullEmpty(token)) {
			throw new AuthenticationServiceException(AuthErrorCode.INVALID_TOKEN.getErrorMessage());
		}

		token = token.substring(AuthAdapterConstant.AUTH_ADMIN_COOKIE_PREFIX.length());
		pathparams.put(KeycloakConstants.REALM_ID, "mosip");
		ResponseEntity<String> response = null;
		MosipUserDto mosipUserDto = null;
		System.out.println("validate token url "+openIdUrl);
		StringBuilder urlBuilder = new StringBuilder().append(openIdUrl).append("userinfo");
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(urlBuilder.toString());
		HttpHeaders headers = new HttpHeaders();
		System.out.println(token);
		String accessToken = "Bearer " + token;
		headers.add("Authorization", accessToken);

		HttpEntity<String> httpRequest = new HttpEntity<>(headers);
		try {
			response = restTemplate.exchange(uriComponentsBuilder.buildAndExpand(pathparams).toUriString(),
					HttpMethod.GET, httpRequest, String.class);
			System.out.println(response.getBody());
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			KeycloakErrorResponseDto keycloakErrorResponseDto = parseKeyClockErrorResponse(e);
			if (keycloakErrorResponseDto.getError_description().equals("Token invalid: Failed to parse JWT")) {
				throw new AuthenticationServiceException(AuthErrorCode.INVALID_TOKEN.getErrorMessage());
			} else if (keycloakErrorResponseDto.getError_description().equals("Token invalid: Token is not active")) {
				throw new AuthenticationServiceException(AuthErrorCode.TOKEN_EXPIRED.getErrorMessage());
			} else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
				throw new AccessDeniedException(AuthErrorCode.INVALID_TOKEN.getErrorMessage());
			} 
			else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
				throw new AccessDeniedException(AuthErrorCode.FORBIDDEN.getErrorMessage());
			} else {
				throw new AuthManagerException(AuthErrorCode.REST_EXCEPTION.getErrorCode(),
						AuthErrorCode.REST_EXCEPTION.getErrorMessage() + " " + e.getResponseBodyAsString());
			}
		}

		if (response.getStatusCode().is2xxSuccessful()) {
			mosipUserDto = getClaims(token);
		}
		return mosipUserDto;

	}

	Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.auth.service.AuthService#logoutUser(java.lang.String)
	 */
	@Override
	public AuthResponseDto logoutUser(String token) {
		if(EmptyCheckUtils.isNullEmpty(token)) {
			throw new AuthenticationServiceException(AuthErrorCode.INVALID_TOKEN.getErrorMessage());
		}
		token = token.substring(AuthAdapterConstant.AUTH_ADMIN_COOKIE_PREFIX.length());
		Map<String, String> pathparams = new HashMap<>();
		pathparams.put(KeycloakConstants.REALM_ID, realmID);
		ResponseEntity<String> response = null;
		AuthResponseDto authResponseDto = new AuthResponseDto();
		StringBuilder urlBuilder = new StringBuilder().append(openIdUrl).append("logout");
        System.out.println(urlBuilder.toString());
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(urlBuilder.toString())
				.queryParam(KeycloakConstants.ID_TOKEN_HINT, token);
		try {
			response = restTemplate.getForEntity(uriComponentsBuilder.buildAndExpand(pathparams).toUriString(),
					String.class);
			System.out.println(response.getBody());
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			throw new AuthManagerException(AuthErrorCode.REST_EXCEPTION.getErrorCode(),
					AuthErrorCode.REST_EXCEPTION.getErrorMessage() + e.getResponseBodyAsString());
		}

		if (response.getStatusCode().is2xxSuccessful()) {
			authResponseDto.setMessage(SUCCESSFULLY_LOGGED_OUT);
			authResponseDto.setStatus(SUCCESS);
		} else {
			authResponseDto.setMessage(LOG_OUT_FAILED);
			authResponseDto.setStatus(FAILED);
		}
		return authResponseDto;
	}

	private MosipUserDto getClaims(String cookie) {
		DecodedJWT decodedJWT = JWT.decode(cookie);

		Claim realmAccess = decodedJWT.getClaim("realm_access");

		RealmAccessDto access = realmAccess.as(RealmAccessDto.class);
		String[] roles = access.getRoles();
		StringBuilder builder = new StringBuilder();

		for (String r : roles) {
			builder.append(r);
			builder.append(",");
		}
		MosipUserDto dto = new MosipUserDto();
		dto.setUserId(decodedJWT.getClaim("preferred_username").asString());
		dto.setMail(decodedJWT.getClaim("email").asString());
		dto.setMobile(decodedJWT.getClaim("contactno").asString());
		dto.setName(decodedJWT.getClaim("preferred_username").asString());
		dto.setRId(decodedJWT.getClaim("rid").asString());
		dto.setRole(builder.toString());
		return dto;
	}

	@Override
	public AccessTokenResponseDTO loginRedirect(String state, String sessionState, String code, String stateCookie,
			String redirectURI) {
		// Compare states
		if (!stateCookie.equals(state)) {
			throw new AuthManagerException(AuthErrorCode.KEYCLOAK_STATE_EXCEPTION.getErrorCode(),
					AuthErrorCode.KEYCLOAK_STATE_EXCEPTION.getErrorMessage());
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add(KeycloakConstants.GRANT_TYPE, loginFlowName);
		map.add(KeycloakConstants.CLIENT_ID, clientID);
		map.add(KeycloakConstants.CLIENT_SECRET, clientSecret);
		map.add(KeycloakConstants.CODE, code);
		map.add(KeycloakConstants.REDIRECT_URI, this.redirectURI + redirectURI);

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
		ResponseEntity<String> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(tokenEndpoint, HttpMethod.POST, entity, String.class);

		} catch (HttpClientErrorException | HttpServerErrorException e) {
			KeycloakErrorResponseDto keycloakErrorResponseDto = parseKeyClockErrorResponse(e);
			throw new LoginException(AuthErrorCode.KEYCLOAK_ACESSTOKEN_EXCEPTION.getErrorCode(),
					AuthErrorCode.KEYCLOAK_ACESSTOKEN_EXCEPTION.getErrorMessage() + AuthConstant.WHITESPACE
							+ keycloakErrorResponseDto.getError_description());
		}
		AccessTokenResponse accessTokenResponse = null;
		try {
			accessTokenResponse = objectmapper.readValue(responseEntity.getBody(), AccessTokenResponse.class);
		} catch (IOException exception) {
			throw new LoginException(AuthErrorCode.RESPONSE_PARSE_ERROR.getErrorCode(),
					AuthErrorCode.RESPONSE_PARSE_ERROR.getErrorMessage() + AuthConstant.WHITESPACE
							+ exception.getMessage());
		}
		AccessTokenResponseDTO accessTokenResponseDTO = new AccessTokenResponseDTO();
		accessTokenResponseDTO.setAccessToken(accessTokenResponse.getAccess_token());
		accessTokenResponseDTO.setExpiresIn(accessTokenResponse.getExpires_in());
		return accessTokenResponseDTO;
	}

	private KeycloakErrorResponseDto parseKeyClockErrorResponse(HttpStatusCodeException exception) {
		KeycloakErrorResponseDto keycloakErrorResponseDto = null;
		try {
			keycloakErrorResponseDto = objectmapper.readValue(exception.getResponseBodyAsString(),
					KeycloakErrorResponseDto.class);
		} catch (IOException e) {
			throw new LoginException(AuthErrorCode.RESPONSE_PARSE_ERROR.getErrorCode(),
					AuthErrorCode.RESPONSE_PARSE_ERROR.getErrorMessage() + AuthConstant.WHITESPACE + e.getMessage());
		}
		return keycloakErrorResponseDto;
	}

	@Override
	public String getKeycloakURI(String redirectURI, String state) {
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(authorizationEndpoint);
		uriComponentsBuilder.queryParam(KeycloakConstants.CLIENT_ID, clientID);
		uriComponentsBuilder.queryParam(KeycloakConstants.REDIRECT_URI, this.redirectURI + redirectURI);
		uriComponentsBuilder.queryParam(KeycloakConstants.STATE, state);
		uriComponentsBuilder.queryParam(KeycloakConstants.RESPONSE_TYPE, responseType);
		uriComponentsBuilder.queryParam(KeycloakConstants.SCOPE, scope);
		return uriComponentsBuilder.build().toString();
	}

}
