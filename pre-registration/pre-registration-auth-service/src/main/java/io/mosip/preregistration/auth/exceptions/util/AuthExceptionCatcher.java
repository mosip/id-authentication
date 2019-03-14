package io.mosip.preregistration.auth.exceptions.util;

import org.springframework.web.client.RestClientException;

import io.mosip.preregistration.auth.errorcodes.ErrorCodes;
import io.mosip.preregistration.auth.errorcodes.ErrorMessages;
import io.mosip.preregistration.auth.exceptions.InvalidateTokenException;
import io.mosip.preregistration.auth.exceptions.SendOtpFailedException;
import io.mosip.preregistration.auth.exceptions.UserIdOtpFaliedException;

/**
 * This class is use to catch the exception while login
 * @author Akshay 
 *@since 1.0.0
 */
public class AuthExceptionCatcher {

	public void handle(Exception ex,String serviceType) {
		if(ex instanceof RestClientException && (serviceType !=null && serviceType.equals("sendOtp"))) {
			throw new SendOtpFailedException(ErrorCodes.PRG_AUTH_001.name(),(ErrorMessages.SEND_OTP_FAILED.name()+": "+ex.getMessage()) );
		}
		else if(ex instanceof RestClientException && (serviceType != null && serviceType.equals("userIdOtp"))) {
			throw new UserIdOtpFaliedException(ErrorCodes.PRG_AUTH_002.name(),( ErrorMessages.USERID_OTP_VALIDATION_FAILED.name()+" :"+ex.getMessage()));
		}
		else if(ex instanceof IllegalArgumentException && (serviceType != null && serviceType.equals("sendOtp"))) {
			throw new SendOtpFailedException(ErrorCodes.PRG_AUTH_001.name(), (ErrorMessages.SEND_OTP_FAILED.name()+": "+ex.getMessage()) );
		}
		else if(ex instanceof NullPointerException && (serviceType != null) && serviceType.equals("sendOtp")) {
			throw new SendOtpFailedException(ErrorCodes.PRG_AUTH_001.name(),(ErrorMessages.SEND_OTP_FAILED.name()+": "+ex.getMessage()));
		}
		else if (ex instanceof NullPointerException && (serviceType != null && serviceType.equals("userIdOtp"))) {
			throw new UserIdOtpFaliedException(ErrorCodes.PRG_AUTH_002.name(), (ErrorMessages.USERID_OTP_VALIDATION_FAILED.name()+" :"+ex.getMessage()));
		}
		else if (ex instanceof RestClientException && (serviceType != null && serviceType.equals("invalidateToken"))) {
			throw new InvalidateTokenException(ErrorCodes.PRG_AUTH_003.name(), (ErrorMessages.INVALIDATE_TOKEN_FAILED.name()+" :"+ex.getMessage()));
		}
		
	}
}
