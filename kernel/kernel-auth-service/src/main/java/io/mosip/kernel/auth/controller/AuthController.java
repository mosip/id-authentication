package io.mosip.kernel.auth.controller;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.mosip.kernel.auth.adapter.constant.AuthAdapterConstant;
import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.constant.AuthErrorCode;
import io.mosip.kernel.auth.dto.AccessTokenResponseDTO;
import io.mosip.kernel.auth.dto.AuthNResponse;
import io.mosip.kernel.auth.dto.AuthNResponseDto;
import io.mosip.kernel.auth.dto.AuthResponseDto;
import io.mosip.kernel.auth.dto.AuthToken;
import io.mosip.kernel.auth.dto.AuthZResponseDto;
import io.mosip.kernel.auth.dto.ClientSecret;
import io.mosip.kernel.auth.dto.ClientSecretDto;
import io.mosip.kernel.auth.dto.LoginUser;
import io.mosip.kernel.auth.dto.MosipUserDto;
import io.mosip.kernel.auth.dto.MosipUserListDto;
import io.mosip.kernel.auth.dto.MosipUserSaltListDto;
import io.mosip.kernel.auth.dto.MosipUserTokenDto;
import io.mosip.kernel.auth.dto.PasswordDto;
import io.mosip.kernel.auth.dto.RIdDto;
import io.mosip.kernel.auth.dto.RolesListDto;
import io.mosip.kernel.auth.dto.UserDetailsDto;
import io.mosip.kernel.auth.dto.UserDetailsRequestDto;
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
import io.mosip.kernel.auth.service.AuthService;
import io.mosip.kernel.auth.service.TokenService;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.swagger.annotations.Api;

/**
 * Controller APIs for Authentication and Authorization
 * 
 * @author Ramadurai Pandian
 * @since 1.0.0
 *
 */

@CrossOrigin
@RestController
@Api(value = "Operation related to Authentication and Authorization", tags = { "authmanager" })
public class AuthController {

	/**
	 * Autowired reference for {@link MosipEnvironment}
	 */

	@Autowired
	private MosipEnvironment mosipEnvironment;

	/**
	 * Autowired reference for {@link AuthService}
	 */

	@Autowired
	private AuthService authService;

	/**
	 * Autowired reference for {@link CustomTokenServices}
	 */

	@Autowired
	private TokenService customTokenServices;

	/**
	 * API to authenticate using userName and password
	 * 
	 * request is of type {@link LoginUser}
	 * 
	 * @return ResponseEntity Cookie value with Auth token
	 */

	@ResponseFilter
	@PostMapping(value = "/authenticate/useridPwd")
	public ResponseWrapper<AuthNResponse> authenticateUseridPwd(@RequestBody @Valid RequestWrapper<LoginUser> request,
			HttpServletResponse res) throws Exception {
		ResponseWrapper<AuthNResponse> responseWrapper = new ResponseWrapper<>();
		AuthNResponse authNResponse = null;
		AuthNResponseDto authResponseDto = authService.authenticateUser(request.getRequest());
		if (authResponseDto != null) {
			Cookie cookie = createCookie(authResponseDto.getToken(), mosipEnvironment.getTokenExpiry());
			authNResponse = new AuthNResponse();
			res.addHeader(mosipEnvironment.getAuthTokenHeader(), authResponseDto.getToken());
			res.addCookie(cookie);
			authNResponse.setStatus(authResponseDto.getStatus());
			authNResponse.setMessage(authResponseDto.getMessage());
			AuthToken token = getAuthToken(authResponseDto);
			customTokenServices.StoreToken(token);
		}
		responseWrapper.setResponse(authNResponse);
		return responseWrapper;
	}

	private AuthToken getAuthToken(AuthNResponseDto authResponseDto) {
		return new AuthToken(authResponseDto.getUserId(), authResponseDto.getToken(), authResponseDto.getExpiryTime(),
				authResponseDto.getRefreshToken());
	}

	private Cookie createCookie(final String content, final int expirationTimeSeconds) {
		final Cookie cookie = new Cookie(mosipEnvironment.getAuthTokenHeader(), content);
		cookie.setMaxAge(expirationTimeSeconds);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		return cookie;
	}

	/**
	 * API to send OTP
	 * 
	 * otpUser is of type {@link OtpUser}
	 * 
	 * @return ResponseEntity with OTP Sent message
	 */
	@ResponseFilter
	@PostMapping(value = "/authenticate/sendotp")
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseWrapper<AuthNResponse> sendOTP(@RequestBody @Valid RequestWrapper<OtpUser> otpUserDto)
			throws Exception {
		ResponseWrapper<AuthNResponse> responseWrapper = new ResponseWrapper<>();
		AuthNResponse authNResponse = null;
		AuthNResponseDto authResponseDto = authService.authenticateWithOtp(otpUserDto.getRequest());
		if (authResponseDto != null) {
			authNResponse = new AuthNResponse();
			authNResponse.setStatus(authResponseDto.getStatus());
			authNResponse.setMessage(authResponseDto.getMessage());
		}
		responseWrapper.setResponse(authNResponse);
		return responseWrapper;
	}

	/**
	 * API to validate OTP with user Id
	 * 
	 * userOtp is of type {@link UserOtp}
	 * 
	 * @return ResponseEntity with Cookie value with Auth token
	 */
	@ResponseFilter
	@PostMapping(value = "/authenticate/useridOTP")
	public ResponseWrapper<AuthNResponse> userIdOTP(@RequestBody @Valid RequestWrapper<UserOtp> userOtpDto,
			HttpServletResponse res) throws Exception {
		ResponseWrapper<AuthNResponse> responseWrapper = new ResponseWrapper<>();
		AuthNResponse authNResponse = null;
		AuthNResponseDto authResponseDto = authService.authenticateUserWithOtp(userOtpDto.getRequest());
		if (authResponseDto != null && authResponseDto.getToken() != null) {
			Cookie cookie = createCookie(authResponseDto.getToken(), mosipEnvironment.getTokenExpiry());
			authNResponse = new AuthNResponse();
			res.addCookie(cookie);
			authNResponse.setStatus(authResponseDto.getStatus());
			authNResponse.setMessage(authResponseDto.getMessage());
			AuthToken token = getAuthToken(authResponseDto);
			if (token != null && token.getUserId() != null) {
				customTokenServices.StoreToken(token);
			}
		} else {
			authNResponse = new AuthNResponse();
			authNResponse.setStatus(authResponseDto.getStatus());
			authNResponse.setMessage(
					authResponseDto.getMessage() != null ? authResponseDto.getMessage() : "Otp validation failed");
		}
		responseWrapper.setResponse(authNResponse);
		return responseWrapper;
	}

	/**
	 * API to authenticate using clientId and secretKey
	 * 
	 * clientSecretDto is of type {@link ClientSecretDto}
	 * 
	 * @return ResponseEntity with Cookie value with Auth token
	 */
	@ResponseFilter
	@PostMapping(value = "/authenticate/clientidsecretkey")
	public ResponseWrapper<AuthNResponse> clientIdSecretKey(
			@RequestBody @Valid RequestWrapper<ClientSecret> clientSecretDto, HttpServletResponse res)
			throws Exception {
		ResponseWrapper<AuthNResponse> responseWrapper = new ResponseWrapper<>();
		AuthNResponse authNResponse = null;
		AuthNResponseDto authResponseDto = authService.authenticateWithSecretKey(clientSecretDto.getRequest());
		if (authResponseDto != null && authResponseDto.getToken()!=null) {
			Cookie cookie = createCookie(authResponseDto.getToken(), mosipEnvironment.getTokenExpiry());
			res.addHeader(mosipEnvironment.getAuthTokenHeader(), authResponseDto.getToken());
			authNResponse = new AuthNResponse();
			res.addCookie(cookie);
			authNResponse.setStatus(authResponseDto.getStatus());
			authNResponse.setMessage(authResponseDto.getMessage());
			System.out.println("Token added in response "+authResponseDto.getToken());
			
		}
		System.out.println("Token in response header :::"+res.getHeader("Set-Cookie"));
		responseWrapper.setResponse(authNResponse);
		return responseWrapper;
	}

	/**
	 * API to validate token
	 * 
	 * 
	 * @return ResponseEntity with MosipUserDto
	 */
	@ResponseFilter
	@PostMapping(value = "/authorize/validateToken")
	public ResponseWrapper<MosipUserDto> validateToken(HttpServletRequest request, HttpServletResponse res)
			throws AuthManagerException, Exception {
		ResponseWrapper<MosipUserDto> responseWrapper = new ResponseWrapper<>();
		String authToken = null;
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			throw new AuthManagerException(AuthErrorCode.COOKIE_NOTPRESENT_ERROR.getErrorCode(),
					AuthErrorCode.COOKIE_NOTPRESENT_ERROR.getErrorMessage());
		}
		MosipUserTokenDto mosipUserDtoToken = null;
		try {
			for (Cookie cookie : cookies) {
				if (cookie.getName().contains(AuthConstant.AUTH_COOOKIE_HEADER)) {
					authToken = cookie.getValue();
				}
			}
			if (authToken == null) {
				throw new AuthManagerException(AuthErrorCode.TOKEN_NOTPRESENT_ERROR.getErrorCode(),
						AuthErrorCode.TOKEN_NOTPRESENT_ERROR.getErrorMessage());
			}
			mosipUserDtoToken = authService.validateToken(authToken);
			if (mosipUserDtoToken != null) {
				mosipUserDtoToken.setMessage(AuthConstant.TOKEN_SUCCESS_MESSAGE);
			}
			Cookie cookie = createCookie(mosipUserDtoToken.getToken(), mosipEnvironment.getTokenExpiry());
			res.addCookie(cookie);
		} catch (NonceExpiredException exp) {
			throw new AuthManagerException(AuthErrorCode.UNAUTHORIZED.getErrorCode(), exp.getMessage());
		}
		responseWrapper.setResponse(mosipUserDtoToken.getMosipUserDto());
		return responseWrapper;
	}

	/**
	 * API to validate token
	 * 
	 * 
	 * @return ResponseEntity with MosipUserDto
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@ResponseFilter
	@GetMapping(value = "/authorize/admin/validateToken")
	public ResponseWrapper<MosipUserDto> validateToken(
			@CookieValue(value = "Authorization", required = false) String token)
			throws IOException {
		MosipUserDto mosipUserDto = authService.valdiateToken(token);
		ResponseWrapper<MosipUserDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(mosipUserDto);
		return responseWrapper;
	}

	/**
	 * API to retry token when auth token expires
	 * 
	 * 
	 * @return ResponseEntity with MosipUserDto
	 */
	@ResponseFilter
	@PostMapping(value = "/authorize/refreshToken")
	public ResponseWrapper<MosipUserDto> retryToken(HttpServletRequest request, HttpServletResponse res)
			throws Exception {
		ResponseWrapper<MosipUserDto> responseWrapper = new ResponseWrapper<>();
		String authToken = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().contains(AuthConstant.AUTH_COOOKIE_HEADER)) {
				authToken = cookie.getValue();
			}
		}
		MosipUserTokenDto mosipUserDtoToken = authService.retryToken(authToken);
		Cookie cookie = createCookie(mosipUserDtoToken.getToken(), mosipEnvironment.getTokenExpiry());
		res.addCookie(cookie);
		responseWrapper.setResponse(mosipUserDtoToken.getMosipUserDto());
		return responseWrapper;
	}

	/**
	 * API to invalidate token when both refresh and auth token expires
	 * 
	 * 
	 * @return ResponseEntity with MosipUserDto
	 */
	@ResponseFilter
	@PostMapping(value = "/authorize/invalidateToken")
	public ResponseWrapper<AuthNResponse> invalidateToken(HttpServletRequest request, HttpServletResponse res)
			throws Exception {
		ResponseWrapper<AuthNResponse> responseWrapper = new ResponseWrapper<>();
		String authToken = null;
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			throw new AuthManagerException(AuthErrorCode.COOKIE_NOTPRESENT_ERROR.getErrorCode(),
					AuthErrorCode.COOKIE_NOTPRESENT_ERROR.getErrorMessage());
		}
		for (Cookie cookie : cookies) {
			if (cookie.getName().contains(AuthConstant.AUTH_COOOKIE_HEADER)) {
				authToken = cookie.getValue();
			}
		}
		if (authToken == null) {
			throw new AuthManagerException(AuthErrorCode.TOKEN_NOTPRESENT_ERROR.getErrorCode(),
					AuthErrorCode.TOKEN_NOTPRESENT_ERROR.getErrorMessage());
		}
		AuthNResponse authNResponse = authService.invalidateToken(authToken);
		responseWrapper.setResponse(authNResponse);
		return responseWrapper;
	}

	@ResponseFilter
	@GetMapping(value = "/roles/{appid}")
	public ResponseWrapper<RolesListDto> getAllRoles(@PathVariable("appid") String appId) throws Exception {
		ResponseWrapper<RolesListDto> responseWrapper = new ResponseWrapper<>();
		RolesListDto rolesListDto = authService.getAllRoles(appId);
		responseWrapper.setResponse(rolesListDto);
		return responseWrapper;
	}

	@ResponseFilter
	@PostMapping(value = "/userdetails/{appid}")
	public ResponseWrapper<MosipUserListDto> getListOfUsersDetails(
			@RequestBody RequestWrapper<UserDetailsRequestDto> userDetails, @PathVariable("appid") String appId)
			throws Exception {
		ResponseWrapper<MosipUserListDto> responseWrapper = new ResponseWrapper<>();
		MosipUserListDto mosipUsers = authService.getListOfUsersDetails(userDetails.getRequest().getUserDetails(),
				appId);
		responseWrapper.setResponse(mosipUsers);
		return responseWrapper;
	}

	@ResponseFilter
	@GetMapping(value = "/usersaltdetails/{appid}")
	public ResponseWrapper<MosipUserSaltListDto> getUserDetailsWithSalt(@PathVariable("appid") String appId)
			throws Exception {
		ResponseWrapper<MosipUserSaltListDto> responseWrapper = new ResponseWrapper<>();
		MosipUserSaltListDto mosipUsers = authService.getAllUserDetailsWithSalt(appId);
		responseWrapper.setResponse(mosipUsers);
		return responseWrapper;
	}

	/**
	 * This API will fetch RID based on appId and userId.
	 * 
	 * @param appId
	 *            - application Id
	 * @param userId
	 *            - user Id
	 * @return {@link RIdDto}
	 * @throws Exception
	 */
	@ResponseFilter
	@GetMapping(value = "rid/{appid}/{userid}")
	public ResponseWrapper<RIdDto> getRId(@PathVariable("appid") String appId, @PathVariable("userid") String userId)
			throws Exception {
		ResponseWrapper<RIdDto> responseWrapper = new ResponseWrapper<>();
		RIdDto rIdDto = authService.getRidBasedOnUid(userId, appId);
		responseWrapper.setResponse(rIdDto);
		return responseWrapper;
	}

	/**
	 * Fetch username based on the user id.
	 * 
	 * @param appId
	 *            - application id
	 * @param userId
	 *            - user id
	 * @return {@link UserNameDto}
	 * @throws Exception
	 *             - exception is thrown if
	 */
	@ResponseFilter
	@GetMapping(value = "unblock/{appid}/{userid}")
	public ResponseWrapper<AuthZResponseDto> getUserName(@PathVariable("appid") String appId,
			@PathVariable("userid") String userId) throws Exception {
		AuthZResponseDto authZResponseDto = authService.unBlockUser(userId, appId);
		ResponseWrapper<AuthZResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(authZResponseDto);
		return responseWrapper;
	}

	/**
	 * This API will change the password of the particular user
	 * 
	 * @param appId
	 *            - applicationId
	 * @param passwordDto
	 *            - {@link PasswordDto}
	 * @return {@link AuthZResponseDto}
	 * @throws Exception
	 */
	@ResponseFilter
	@PostMapping(value = "/changepassword/{appid}")
	public ResponseWrapper<AuthZResponseDto> changePassword(@PathVariable("appid") String appId,
			@RequestBody @Valid RequestWrapper<PasswordDto> passwordDto) throws Exception {
		AuthZResponseDto mosipUserDto = authService.changePassword(appId, passwordDto.getRequest());
		ResponseWrapper<AuthZResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(mosipUserDto);
		return responseWrapper;
	}

	/**
	 * This API will reset the password of the particular user
	 * 
	 * @param appId
	 *            - applicationId
	 * @param passwordDto
	 *            -{@link PasswordDto}
	 * @return {@link AuthZResponseDto}
	 * @throws Exception
	 */
	@ResponseFilter
	@PostMapping(value = "/resetpassword/{appid}")
	public ResponseWrapper<AuthZResponseDto> resetPassword(@PathVariable("appid") String appId,
			@RequestBody @Valid RequestWrapper<PasswordDto> passwordDto) throws Exception {
		AuthZResponseDto mosipUserDto = authService.resetPassword(appId, passwordDto.getRequest());
		ResponseWrapper<AuthZResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(mosipUserDto);
		return responseWrapper;
	}

	/**
	 * 
	 * @param mobile
	 *            - mobile number
	 * @param appId
	 *            - applicationId
	 * @return {@link UserNameDto}
	 * @throws Exception
	 */
	@ResponseFilter
	@GetMapping(value = "/username/{appid}/{mobilenumber}")
	public ResponseWrapper<UserNameDto> getUsernameBasedOnMobileNumber(@PathVariable("mobilenumber") String mobile,
			@PathVariable("appid") String appId) throws Exception {
		UserNameDto userNameDto = authService.getUserNameBasedOnMobileNumber(appId, mobile);
		ResponseWrapper<UserNameDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(userNameDto);
		return responseWrapper;
	}

	/**
	 * Create a user account in Data Store
	 * 
	 * @param userCreationRequestDto
	 *            {@link UserRegistrationRequestDto}
	 * @return {@link UserRegistrationResponseDto}
	 */
	@ResponseFilter
	@PostMapping(value = "/user")
	public ResponseWrapper<UserRegistrationResponseDto> registerUser(
			@RequestBody @Valid RequestWrapper<UserRegistrationRequestDto> userCreationRequestDto) {
		ResponseWrapper<UserRegistrationResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(authService.registerUser(userCreationRequestDto.getRequest()));
		return responseWrapper;
	}

	@ResponseFilter
	@PostMapping(value = "/user/addpassword")
	public ResponseWrapper<UserPasswordResponseDto> addPassword(
			@RequestBody @Valid RequestWrapper<UserPasswordRequestDto> userPasswordRequestDto) {
		ResponseWrapper<UserPasswordResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(authService.addUserPassword(userPasswordRequestDto.getRequest()));
		return responseWrapper;
	}

	@GetMapping("/role/{appId}/{userId}")
	@ResponseFilter
	public ResponseWrapper<UserRoleDto> getUserRole(@PathVariable("appId") String appId,
			@PathVariable("userId") String userId) throws Exception {
		ResponseWrapper<UserRoleDto> responseWrapper = new ResponseWrapper<>();
		UserRoleDto userRole = authService.getUserRole(appId, userId);
		responseWrapper.setResponse(userRole);
		return responseWrapper;
	}

	/**
	 * 
	 * @param mobile
	 *            - mobile number
	 * @param appId
	 *            - applicationId
	 * @return {@link MosipUserDto}
	 * @throws Exception
	 */
	@ResponseFilter
	@GetMapping(value = "/userdetail/{appid}/{mobilenumber}")
	public ResponseWrapper<MosipUserDto> getUserDetailBasedOnMobileNumber(@PathVariable("mobilenumber") String mobile,
			@PathVariable("appid") String appId) throws Exception {
		MosipUserDto mosipUserDto = authService.getUserDetailBasedonMobileNumber(appId, mobile);
		ResponseWrapper<MosipUserDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(mosipUserDto);
		return responseWrapper;
	}

	/**
	 * 
	 * @param mobile
	 *            - mobile number
	 * @param appId
	 *            - applicationId
	 * @return {@link MosipUserDto}
	 * @throws Exception
	 */
	@ResponseFilter
	@GetMapping(value = "/validate/{appid}/{userid}")
	public ResponseWrapper<ValidationResponseDto> validateUserName(@PathVariable("userid") String userId,
			@PathVariable("appid") String appId) {
		ValidationResponseDto validationResponseDto = authService.validateUserName(appId, userId);
		ResponseWrapper<ValidationResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(validationResponseDto);
		return responseWrapper;
	}

	
	/**
	 * Gets the user detail based on user id.
	 *
	 * @param appId the app id
	 * @param userId the user id
	 * @return {@link UserDetailsDto}
	 */
	@ResponseFilter
	@PostMapping(value = "/userdetail/regid/{appid}")
	public ResponseWrapper<UserDetailsResponseDto> getUserDetailBasedOnUserId(@PathVariable("appid") String appId,
			@RequestBody RequestWrapper<UserDetailsRequestDto> userDetails) {
		UserDetailsResponseDto userDetailsDto = authService.getUserDetailBasedOnUserId(appId, userDetails.getRequest().getUserDetails());
		ResponseWrapper<UserDetailsResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(userDetailsDto);
		return responseWrapper;
	}

	/**
	 * 
	 * @param req
	 *            - {@link HttpServletRequest}
	 * @param res
	 *            - {@link HttpServletResponse}
	 * @return {@link ResponseWrapper}
	 */
	@ResponseFilter
	@DeleteMapping(value = "/logout/user")
	public ResponseWrapper<AuthResponseDto> logoutUser(@CookieValue(value = "Authorization", required = false) String token, HttpServletResponse res) {
		AuthResponseDto authResponseDto = authService.logoutUser(token);
		ResponseWrapper<AuthResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(authResponseDto);
		return responseWrapper;
	}

	@GetMapping(value = "/login/{redirectURI}")
	public void login(@CookieValue("state") String state, @PathVariable("redirectURI") String redirectURI,
			HttpServletResponse res) throws IOException {
		String uri = authService.getKeycloakURI(redirectURI, state);
		res.setStatus(302);
		res.sendRedirect(uri);
	}

	@GetMapping(value = "/login-redirect/{redirectURI}")
	public void loginRedirect(@PathVariable("redirectURI") String redirectURI, @RequestParam("state") String state,
			@RequestParam("session_state") String sessionState, @RequestParam("code") String code,
			@CookieValue("state") String stateCookie, HttpServletResponse res) throws IOException {
		AccessTokenResponseDTO jwtResponseDTO = authService.loginRedirect(state, sessionState, code, stateCookie,
				redirectURI);
		String uri = new String(Base64.decodeBase64(redirectURI.getBytes()));
		Cookie cookie = createCookie(AuthAdapterConstant.AUTH_ADMIN_COOKIE_PREFIX+jwtResponseDTO.getAccessToken(), Integer.parseInt(jwtResponseDTO.getExpiresIn()));
		res.addCookie(cookie);
		res.setStatus(302);
		res.sendRedirect(uri);
	}

	/**
	 * Gets Access token from cookie
	 * 
	 * @param req
	 *            - {@link HttpServletRequest}
	 * @return {@link String} - accessToken
	 */
	private String getTokenFromCookie(HttpServletRequest req) {
		Cookie[] cookies = req.getCookies();
		String token = null;
		if (cookies == null) {
			throw new AuthManagerException(AuthErrorCode.COOKIE_NOTPRESENT_ERROR.getErrorCode(),
					AuthErrorCode.COOKIE_NOTPRESENT_ERROR.getErrorMessage());
		}
		for (Cookie cookie : cookies) {
			if (cookie.getName().contains(AuthConstant.AUTH_COOOKIE_HEADER)) {
				token = cookie.getValue();
				removeCookie(cookie);
				break;
			}
		}
		if (token == null) {
			throw new AuthManagerException(AuthErrorCode.TOKEN_NOTPRESENT_ERROR.getErrorCode(),
					AuthErrorCode.TOKEN_NOTPRESENT_ERROR.getErrorMessage());
		}

		return token;
	}

	/**
	 * Erases Cookie from browser
	 * 
	 * @param cookie
	 *            - {@link Cookie}
	 */
	private void removeCookie(Cookie cookie) {
		cookie.setValue("");
		cookie.setPath("/");
		cookie.setMaxAge(0);
	}
}
