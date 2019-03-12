package io.mosip.kernel.auth.service.impl;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.entities.AuthNResponse;
import io.mosip.kernel.auth.entities.AuthNResponseDto;
import io.mosip.kernel.auth.entities.AuthToken;
import io.mosip.kernel.auth.entities.BasicTokenDto;
import io.mosip.kernel.auth.entities.ClientSecret;
import io.mosip.kernel.auth.entities.LoginUser;
import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.MosipUserDtoToken;
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

	/**
	 * Method used for validating Auth token
	 * 
	 * @param token
	 * 
	 * @return mosipUserDtoToken is of type {@link MosipUserDtoToken}
	 * 
	 * @throws Exception
	 * 
	 */

	@Override
	public MosipUserDtoToken validateToken(String token) throws Exception {
		long currentTime = new Date().getTime();
		MosipUserDtoToken mosipUserDtoToken = tokenValidator.validateToken(token);
		AuthToken authToken = customTokenServices.getTokenDetails(token);
		if(authToken==null)
		{
			throw new AuthManagerException(AuthConstant.UNAUTHORIZED_CODE,"Auth token is not present");
		}
		long tenMinsExp = getExpiryTime(authToken.getExpirationTime());
		/*if(currentTime==tenMinsExp)
		{
			TimeToken newToken = tokenGenerator.generateNewToken(token);
			mosipUserDtoToken.setToken(newToken.getToken());
			mosipUserDtoToken.setExpTime(newToken.getExpTime());
			return mosipUserDtoToken;
		}*/
		if (mosipUserDtoToken != null && (currentTime < authToken.getExpirationTime())) {
			return mosipUserDtoToken;
		} else {
			throw new NonceExpiredException(AuthConstant.AUTH_TOKEN_EXPIRED_MESSAGE);
		}
	}

	private long getExpiryTime(long expirationTime) {
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(new Date(expirationTime));
	    calendar.add(Calendar.MINUTE, AuthConstant.RETURN_EXP_TIME);
	    Date result = calendar.getTime();
		return result.getTime();
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
	 * 
	 */

	@Override
	public AuthNResponseDto authenticateWithOtp(OtpUser otpUser) throws Exception {
		AuthNResponseDto authNResponseDto = null;
		MosipUserDto mosipUser = null;
		if (AuthConstant.APPTYPE_UIN.equals(otpUser.getUseridtype())) {
			mosipUser = uinService.getDetailsFromUin(otpUser);
			authNResponseDto = oTPService.sendOTPForUin(mosipUser, otpUser.getOtpChannel(), otpUser.getAppId());
		} else {
			mosipUser = userStoreFactory.getDataStoreBasedOnApp(otpUser.getAppId()).authenticateWithOtp(otpUser);
			authNResponseDto = oTPService.sendOTP(mosipUser, otpUser.getOtpChannel(), otpUser.getAppId());
			authNResponseDto.setMessage(authNResponseDto.getMessage());
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
	 * 
	 */

	@Override
	public AuthNResponseDto authenticateUserWithOtp(UserOtp userOtp) throws Exception {
		AuthNResponseDto authNResponseDto = new AuthNResponseDto();
		MosipUserDto mosipUser = userStoreFactory.getDataStoreBasedOnApp(userOtp.getAppId())
				.authenticateUserWithOtp(userOtp);
		MosipUserDtoToken mosipToken = oTPService.validateOTP(mosipUser, userOtp.getOtp());
		if(mosipToken!=null)
		{
		authNResponseDto.setMessage(AuthConstant.OTP_VALIDATION_MESSAGE);
		authNResponseDto.setToken(mosipToken.getToken());
		authNResponseDto.setExpiryTime(mosipToken.getExpTime());
		authNResponseDto.setRefreshToken(mosipToken.getRefreshToken());
		authNResponseDto.setUserId(mosipToken.getMosipUserDto().getUserId());
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
			authNResponseDto.setMessage(AuthConstant.CLIENT_SECRET_SUCCESS_MESSAGE);
		}
		return authNResponseDto;
	}

	/**
	 * Method used for generating refresh token
	 * 
	 * @param token
	 * 
	 * @return mosipUserDtoToken is of type {@link MosipUserDtoToken}
	 * 
	 * @throws Exception
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
	 * @param token
	 * 
	 * @return authNResponse is of type {@link AuthNResponse}
	 * 
	 * @throws Exception
	 * 
	 */

	@Override
	public AuthNResponse invalidateToken(String token) throws Exception {
		AuthNResponse authNResponse = null;
		customTokenServices.revokeToken(token);
		authNResponse = new AuthNResponse();
		authNResponse.setMessage(AuthConstant.TOKEN_INVALID_MESSAGE);
		return authNResponse;
	}

}
