package io.mosip.preregistration.auth.exceptions.util;

import org.springframework.web.client.RestClientException;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.auth.errorcodes.ErrorCodes;
import io.mosip.preregistration.auth.errorcodes.ErrorMessages;
import io.mosip.preregistration.auth.exceptions.AuthServiceException;
import io.mosip.preregistration.auth.exceptions.ConfigFileNotFoundException;
import io.mosip.preregistration.auth.exceptions.InvalidateTokenException;
import io.mosip.preregistration.auth.exceptions.ParseResponseException;
import io.mosip.preregistration.auth.exceptions.SendOtpFailedException;
import io.mosip.preregistration.auth.exceptions.UserIdOtpFaliedException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;

/**
 * This class is use to catch the exception while login
 * @author Akshay 
 *@since 1.0.0
 */
public class AuthExceptionCatcher {

	public void handle(Exception ex,String serviceType) {
		if(ex instanceof RestClientException && (serviceType !=null && serviceType.equals("sendOtp"))) {
			throw new SendOtpFailedException(ErrorCodes.PRG_AUTH_001.name(),(ErrorMessages.SEND_OTP_FAILED.getMessage()) );
		}
		else if(ex instanceof RestClientException && (serviceType != null && serviceType.equals("userIdOtp"))) {
			throw new UserIdOtpFaliedException(ErrorCodes.PRG_AUTH_002.name(),( ErrorMessages.USERID_OTP_VALIDATION_FAILED.getMessage()));
		}
		else if (ex instanceof RestClientException && (serviceType != null && serviceType.equals("invalidateToken"))) {
			throw new InvalidateTokenException(ErrorCodes.PRG_AUTH_003.getCode(), (ErrorMessages.INVALIDATE_TOKEN_FAILED.getMessage()));
		}
		else if (ex instanceof InvalidRequestParameterException && (serviceType !=null && serviceType.equals("sendOtp"))) {
			throw new InvalidRequestParameterException(((InvalidRequestParameterException)ex).getErrorCode(),((InvalidRequestParameterException) ex).getErrorText());
		}
		else if (ex instanceof InvalidRequestParameterException && (serviceType != null && serviceType.equals("userIdOtp"))) {
			throw new InvalidRequestParameterException(((InvalidRequestParameterException)ex).getErrorCode(),((InvalidRequestParameterException) ex).getErrorText());
		}
		else if (ex instanceof AuthServiceException) {
			throw new AuthServiceException(((AuthServiceException) ex).getValidationErrorList(),((AuthServiceException) ex).getMainResposneDTO());
		}
		else if (ex instanceof ParseResponseException) {
			throw new ParseResponseException(((ParseResponseException) ex).getErrorCode(),((ParseResponseException) ex).getErrorText());
		}
		else if (ex instanceof ConfigFileNotFoundException) {
			throw new ConfigFileNotFoundException(((ConfigFileNotFoundException) ex).getErrorCode(),((ConfigFileNotFoundException) ex).getErrorText());
		}
		
		
	}
}
