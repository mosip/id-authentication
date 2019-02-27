package io.mosip.kernel.auth.controller;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.entities.*;
import io.mosip.kernel.auth.entities.otp.OtpUser;
import io.mosip.kernel.auth.service.AuthService;
import io.mosip.kernel.auth.service.CustomTokenServices;
import io.swagger.annotations.Api;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller APIs for Authentication and Authorization
 * 
 * @author Ramadurai Pandian
 * @since 1.0.0
 *
 */

@CrossOrigin
@RestController
@RequestMapping("/v1.0")
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
	 * loginUser is of type {@link LoginUser}
	 * 
	 * @return ResponseEntity Cookie value with Auth token
	 */

	@PostMapping(value = "/authenticate/useridPwd")
	public ResponseEntity<AuthNResponse> authenticateUseridPwd(@RequestBody LoginUser loginUser,
			HttpServletResponse res) throws Exception {
		AuthNResponse authNResponse = null;
		AuthNResponseDto authResponseDto = authService.authenticateUser(loginUser);
		if (authResponseDto != null) {
			Cookie cookie = createCookie(authResponseDto.getToken(), mosipEnvironment.getTokenExpiry());
			authNResponse = new AuthNResponse();
			res.addCookie(cookie);
			authNResponse.setMessage(authResponseDto.getMessage());
			AuthToken token = getAuthToken(authResponseDto);
			customTokenServices.StoreToken(token);
		}
		return new ResponseEntity<>(authNResponse, HttpStatus.OK);
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

	@PostMapping(value = "/authenticate/sendotp")
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<AuthNResponse> sendOTP(@RequestBody OtpUser otpUser) throws Exception {
		AuthNResponse authNResponse = null;
		AuthNResponseDto authResponseDto = authService.authenticateWithOtp(otpUser);
		if (authResponseDto != null) {
			authNResponse = new AuthNResponse();
			authNResponse.setMessage(authResponseDto.getMessage());
		}
		return new ResponseEntity<>(authNResponse, HttpStatus.OK);
	}

	/**
	 * API to validate OTP with user Id
	 * 
	 * userOtp is of type {@link UserOtp}
	 * 
	 * @return ResponseEntity with Cookie value with Auth token
	 */

	@PostMapping(value = "/authenticate/useridOTP")
	public ResponseEntity<AuthNResponse> userIdOTP(@RequestBody UserOtp userOtp, HttpServletResponse res)
			throws Exception {
		AuthNResponse authNResponse = null;
		AuthNResponseDto authResponseDto = authService.authenticateUserWithOtp(userOtp);
		if (authResponseDto != null) {
			Cookie cookie = createCookie(authResponseDto.getToken(), mosipEnvironment.getTokenExpiry());
			authNResponse = new AuthNResponse();
			res.addCookie(cookie);
			authNResponse.setMessage(authResponseDto.getMessage());
			AuthToken token = getAuthToken(authResponseDto);
			customTokenServices.StoreToken(token);
		}
		return new ResponseEntity<>(authNResponse, HttpStatus.OK);
	}

	/**
	 * API to authenticate using clientId and secretKey
	 * 
	 * userOtp is of type {@link UserOtp}
	 * 
	 * @return ResponseEntity with Cookie value with Auth token
	 */

	@PostMapping(value = "/authenticate/clientidsecretkey")
	public ResponseEntity<AuthNResponse> clientIdSecretKey(ClientSecret clientSecret, HttpServletResponse res)
			throws Exception {
		AuthNResponse authNResponse = null;
		AuthNResponseDto authResponseDto = authService.authenticateWithSecretKey(clientSecret);
		if (authResponseDto != null) {
			Cookie cookie = createCookie(authResponseDto.getToken(), mosipEnvironment.getTokenExpiry());
			authNResponse = new AuthNResponse();
			res.addCookie(cookie);
			authNResponse.setMessage(authResponseDto.getMessage());
			AuthToken token = getAuthToken(authResponseDto);
			customTokenServices.StoreToken(token);
		}
		return new ResponseEntity<>(authNResponse, HttpStatus.OK);
	}

	/**
	 * API to validate token
	 * 
	 * 
	 * @return ResponseEntity with MosipUserDto
	 */

	@PostMapping(value = "/authorize/validateToken")
	public ResponseEntity<MosipUserDto> validateToken(HttpServletRequest request, HttpServletResponse res)
			throws Exception {
		String authToken = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().contains(AuthConstant.AUTH_COOOKIE_HEADER)) {
				authToken = cookie.getValue();
			}
		}

		MosipUserDtoToken mosipUserDtoToken = authService.validateToken(authToken);
		if (mosipUserDtoToken != null) {
			mosipUserDtoToken.setMessage(AuthConstant.TOKEN_SUCCESS_MESSAGE);
		}
		Cookie cookie = createCookie(mosipUserDtoToken.getToken(), mosipEnvironment.getTokenExpiry());
		res.addCookie(cookie);
		return new ResponseEntity<>(mosipUserDtoToken.getMosipUserDto(), HttpStatus.OK);
	}

	/**
	 * API to retry token when auth token expires
	 * 
	 * 
	 * @return ResponseEntity with MosipUserDto
	 */

	@PostMapping(value = "/authorize/retryToken")
	public ResponseEntity<MosipUserDto> retryToken(HttpServletRequest request, HttpServletResponse res)
			throws Exception {
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
		return new ResponseEntity<>(mosipUserDtoToken.getMosipUserDto(), HttpStatus.OK);
	}

	/**
	 * API to invalidate token when both refresh and auth token expires
	 * 
	 * 
	 * @return ResponseEntity with MosipUserDto
	 */

	@PostMapping(value = "/authorize/invalidateToken")
	public ResponseEntity<AuthNResponse> invalidateToken(HttpServletRequest request, HttpServletResponse res)
			throws Exception {
		String authToken = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().contains(AuthConstant.AUTH_COOOKIE_HEADER)) {
				authToken = cookie.getValue();
			}
		}
		AuthNResponse authNResponse = authService.invalidateToken(authToken);
		return new ResponseEntity<>(authNResponse, HttpStatus.OK);
	}

}
