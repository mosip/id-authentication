package io.mosip.kernel.auth.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.constant.AuthErrorCode;
import io.mosip.kernel.auth.entities.AuthNResponse;
import io.mosip.kernel.auth.entities.AuthNResponseDto;
import io.mosip.kernel.auth.entities.AuthToken;
import io.mosip.kernel.auth.entities.ClientSecret;
import io.mosip.kernel.auth.entities.ClientSecretDto;
import io.mosip.kernel.auth.entities.LoginUser;
import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.MosipUserDtoToken;
import io.mosip.kernel.auth.entities.MosipUserListDto;
import io.mosip.kernel.auth.entities.RolesListDto;
import io.mosip.kernel.auth.entities.UserDetailsRequest;
import io.mosip.kernel.auth.entities.UserOtp;
import io.mosip.kernel.auth.entities.otp.OtpUser;
import io.mosip.kernel.auth.exception.AuthManagerException;
import io.mosip.kernel.auth.service.AuthService;
import io.mosip.kernel.auth.service.CustomTokenServices;
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
	private CustomTokenServices customTokenServices;

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
		if (authResponseDto != null) {
			Cookie cookie = createCookie(authResponseDto.getToken(), mosipEnvironment.getTokenExpiry());
			authNResponse = new AuthNResponse();
			res.addCookie(cookie);
			authNResponse.setStatus(authResponseDto.getStatus());
			authNResponse.setMessage(authResponseDto.getMessage());
			AuthToken token = getAuthToken(authResponseDto);
			customTokenServices.StoreToken(token);
		}
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
		if(cookies==null)
		{
			throw new AuthManagerException(AuthErrorCode.COOKIE_NOTPRESENT_ERROR.getErrorCode(), AuthErrorCode.COOKIE_NOTPRESENT_ERROR.getErrorMessage());
		}
		MosipUserDtoToken mosipUserDtoToken = null;
		try {
			for (Cookie cookie : cookies) {
				if (cookie.getName().contains(AuthConstant.AUTH_COOOKIE_HEADER)) {
					authToken = cookie.getValue();
				}
			}
			if(authToken==null)
			{
				throw new AuthManagerException(AuthErrorCode.TOKEN_NOTPRESENT_ERROR.getErrorCode(), AuthErrorCode.TOKEN_NOTPRESENT_ERROR.getErrorMessage());
			}
			mosipUserDtoToken = authService.validateToken(authToken);
			if (mosipUserDtoToken != null) {
				mosipUserDtoToken.setMessage(AuthConstant.TOKEN_SUCCESS_MESSAGE);
			}
			Cookie cookie = createCookie(mosipUserDtoToken.getToken(), mosipEnvironment.getTokenExpiry());
			res.addCookie(cookie);
		} catch (NonceExpiredException exp) {
			throw new AuthManagerException(AuthErrorCode.UNAUTHORIZED.getErrorCode(), exp.getMessage());
		} catch (AuthManagerException e) {

			throw new AuthManagerException(AuthErrorCode.UNAUTHORIZED.getErrorCode(), e.getMessage());
		}
		responseWrapper.setResponse(mosipUserDtoToken.getMosipUserDto());
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
		MosipUserDtoToken mosipUserDtoToken = authService.retryToken(authToken);
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
		if(cookies==null)
		{
			throw new AuthManagerException(AuthErrorCode.COOKIE_NOTPRESENT_ERROR.getErrorCode(), AuthErrorCode.COOKIE_NOTPRESENT_ERROR.getErrorMessage());
		}
		for (Cookie cookie : cookies) {
			if (cookie.getName().contains(AuthConstant.AUTH_COOOKIE_HEADER)) {
				authToken = cookie.getValue();
			}
		}
		if(authToken==null)
		{
			throw new AuthManagerException(AuthErrorCode.TOKEN_NOTPRESENT_ERROR.getErrorCode(), AuthErrorCode.TOKEN_NOTPRESENT_ERROR.getErrorMessage());
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
			@RequestBody RequestWrapper<UserDetailsRequest> userDetails, @PathVariable("appid") String appId)
			throws Exception {
		ResponseWrapper<MosipUserListDto> responseWrapper = new ResponseWrapper<>();
		MosipUserListDto mosipUsers = authService.getListOfUsersDetails(userDetails.getRequest().getUserDetails(),
				appId);
		responseWrapper.setResponse(mosipUsers);
		return responseWrapper;
	}

}
