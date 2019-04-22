package io.mosip.kernel.auth.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.entities.AuthNResponse;
import io.mosip.kernel.auth.entities.AuthNResponseDto;
import io.mosip.kernel.auth.entities.AuthToken;
import io.mosip.kernel.auth.entities.BasicTokenDto;
import io.mosip.kernel.auth.entities.ClientSecret;
import io.mosip.kernel.auth.entities.LoginUser;
import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.MosipUserDtoToken;
import io.mosip.kernel.auth.entities.MosipUserListDto;
import io.mosip.kernel.auth.entities.RolesListDto;
import io.mosip.kernel.auth.entities.TimeToken;
import io.mosip.kernel.auth.entities.UserOtp;
import io.mosip.kernel.auth.entities.otp.OtpUser;
import io.mosip.kernel.auth.exception.AuthManagerException;
import io.mosip.kernel.auth.factory.UserStoreFactory;
import io.mosip.kernel.auth.jwtBuilder.TokenGenerator;
import io.mosip.kernel.auth.jwtBuilder.TokenValidator;
import io.mosip.kernel.auth.service.AuthService;
import io.mosip.kernel.auth.service.CustomTokenServices;
import io.mosip.kernel.auth.service.OTPService;
import io.mosip.kernel.auth.service.UinService;
import io.mosip.kernel.core.util.HMACUtils;

/**
 * Auth Service for Authentication and Authorization
 * 
 * @author Ramadurai Pandian
 * 
 *
 */

@Component
public class AuthServiceImpl implements AuthService {

	@Autowired
	UserStoreFactory userStoreFactory;

	@Autowired
	TokenGenerator tokenGenerator;

	@Autowired
	TokenValidator tokenValidator;

	@Autowired
	CustomTokenServices customTokenServices;

	@Autowired
	OTPService oTPService;

	@Autowired
	UinService uinService;

	@Autowired
	MosipEnvironment mosipEnvironment;

	/**
	 * Method used for validating Auth token
	 * 
	 * @param token token
	 * 
	 * @return mosipUserDtoToken is of type {@link MosipUserDtoToken}
	 * 
	 * @throws Exception exception
	 * 
	 */

	@Override
	public MosipUserDtoToken validateToken(String token) throws Exception {
		//long currentTime = Instant.now().toEpochMilli();
		MosipUserDtoToken mosipUserDtoToken = tokenValidator.validateToken(token);
		/*AuthToken authToken = customTokenServices.getTokenDetails(token);
		if (authToken == null) {
			throw new AuthManagerException(AuthConstant.UNAUTHORIZED_CODE,
					"Auth token has been changed,Please try with new login");
		}
		long tenMinsExp = getExpiryTime(authToken.getExpirationTime());
		if (currentTime > tenMinsExp && currentTime < authToken.getExpirationTime()) {
			TimeToken newToken = tokenGenerator.generateNewToken(token);
			mosipUserDtoToken.setToken(newToken.getToken());
			mosipUserDtoToken.setExpTime(newToken.getExpTime());
			AuthToken newAuthToken = getAuthToken(mosipUserDtoToken);
			customTokenServices.StoreToken(newAuthToken);
			return mosipUserDtoToken;
		}*/
		if (mosipUserDtoToken != null /*&& (currentTime < authToken.getExpirationTime())*/) {
			return mosipUserDtoToken;
		} else {
			throw new NonceExpiredException(AuthConstant.AUTH_TOKEN_EXPIRED_MESSAGE);
		}
	}

	private AuthToken getAuthToken(MosipUserDtoToken mosipUserDtoToken) {
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
	 * @param loginUser is of type {@link LoginUser}
	 * 
	 * @return authNResponseDto is of type {@link AuthNResponseDto}
	 * 
	 * @throws Exception exception
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
	 * @param otpUser is of type {@link OtpUser}
	 * 
	 * @return authNResponseDto is of type {@link AuthNResponseDto}
	 * 
	 * @throws Exception exception
	 * 
	 */

	@Override
	public AuthNResponseDto authenticateWithOtp(OtpUser otpUser) throws Exception {
		AuthNResponseDto authNResponseDto = null;
		MosipUserDto mosipUser = null;
		if (AuthConstant.APPTYPE_UIN.equals(otpUser.getUseridtype())) {
			mosipUser = uinService.getDetailsFromUin(otpUser);
			authNResponseDto = oTPService.sendOTPForUin(mosipUser, otpUser.getOtpChannel(), otpUser.getAppId());
		} else if (AuthConstant.APPTYPE_USERID.equals(otpUser.getUseridtype())) {
			mosipUser = userStoreFactory.getDataStoreBasedOnApp(otpUser.getAppId()).authenticateWithOtp(otpUser);
			authNResponseDto = oTPService.sendOTP(mosipUser, otpUser.getOtpChannel(), otpUser.getAppId());
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
	 * @param userOtp is of type {@link UserOtp}
	 * 
	 * @return authNResponseDto is of type {@link AuthNResponseDto}
	 * 
	 * @throws Exception exception
	 * 
	 */

	@Override
	public AuthNResponseDto authenticateUserWithOtp(UserOtp userOtp) throws Exception {
		AuthNResponseDto authNResponseDto = new AuthNResponseDto();
		MosipUserDto mosipUser = userStoreFactory.getDataStoreBasedOnApp(userOtp.getAppId())
				.authenticateUserWithOtp(userOtp);
		MosipUserDtoToken mosipToken = oTPService.validateOTP(mosipUser, userOtp.getOtp());
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
	 * @param clientSecret is of type {@link ClientSecret}
	 * 
	 * @return authNResponseDto is of type {@link AuthNResponseDto}
	 * 
	 * @throws Exception exception
	 * 
	 */

	@Override
	public AuthNResponseDto authenticateWithSecretKey(ClientSecret clientSecret) throws Exception {
		AuthNResponseDto authNResponseDto = null;
		MosipUserDto mosipUser = userStoreFactory.getDataStoreBasedOnApp(clientSecret.getAppId())
				.authenticateWithSecretKey(clientSecret);
		BasicTokenDto basicTokenDto = tokenGenerator.basicGenerate(mosipUser);
		if (basicTokenDto != null) {
			authNResponseDto = new AuthNResponseDto();
			authNResponseDto.setToken(basicTokenDto.getAuthToken());
			authNResponseDto.setUserId(mosipUser.getUserId());
			authNResponseDto.setRefreshToken(basicTokenDto.getRefreshToken());
			authNResponseDto.setExpiryTime(basicTokenDto.getExpiryTime());
			authNResponseDto.setStatus(AuthConstant.SUCCESS_STATUS);
			authNResponseDto.setMessage(AuthConstant.CLIENT_SECRET_SUCCESS_MESSAGE);
		}
		return authNResponseDto;
	}

	/**
	 * Method used for generating refresh token
	 * 
	 * @param existingToken existing token
	 * 
	 * @return mosipUserDtoToken is of type {@link MosipUserDtoToken}
	 * 
	 * @throws Exception exception
	 * 
	 */

	@Override
	public MosipUserDtoToken retryToken(String existingToken) throws Exception {
		MosipUserDtoToken mosipUserDtoToken = null;
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
	 * @param token token
	 * 
	 * @return authNResponse is of type {@link AuthNResponse}
	 * 
	 * @throws Exception exception
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

}
