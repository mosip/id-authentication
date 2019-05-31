package io.mosip.preregistration.login.exception.util;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.login.errorcodes.ErrorCodes;
import io.mosip.preregistration.login.errorcodes.ErrorMessages;
import io.mosip.preregistration.login.exception.ConfigFileNotFoundException;
import io.mosip.preregistration.login.exception.InvalidOtpOrUseridException;
import io.mosip.preregistration.login.exception.InvalidateTokenException;
import io.mosip.preregistration.login.exception.LoginServiceException;
import io.mosip.preregistration.login.exception.NoAuthTokenException;
import io.mosip.preregistration.login.exception.ParseResponseException;
import io.mosip.preregistration.login.exception.SendOtpFailedException;
import io.mosip.preregistration.login.exception.UserIdOtpFaliedException;



/**
 * This class is use to catch the exception while login
 * @author Akshay 
 *@since 1.0.0
 */
public class LoginExceptionCatcher {

	public void handle(Exception ex,String serviceType,MainResponseDTO<?> mainResponsedto) {
		if((ex instanceof RestClientException || ex instanceof HttpClientErrorException || ex instanceof HttpServerErrorException )&& (serviceType !=null && serviceType.equals("sendOtp"))) {
			throw new SendOtpFailedException(ErrorCodes.PRG_AUTH_001.name(),(ErrorMessages.SEND_OTP_FAILED.getMessage()),mainResponsedto );
		}
		else if(ex instanceof RestClientException && (serviceType != null && serviceType.equals("userIdOtp"))) {
			throw new UserIdOtpFaliedException(ErrorCodes.PRG_AUTH_002.name(),( ErrorMessages.USERID_OTP_VALIDATION_FAILED.getMessage()),mainResponsedto);
		}
		else if (ex instanceof RestClientException && (serviceType != null && serviceType.equals("invalidateToken"))) {
			throw new InvalidateTokenException(ErrorCodes.PRG_AUTH_003.getCode(), (ErrorMessages.INVALIDATE_TOKEN_FAILED.getMessage()),mainResponsedto);
		}
		else if (ex instanceof InvalidRequestParameterException && (serviceType !=null && serviceType.equals("sendOtp"))) {
			throw new InvalidRequestParameterException(((InvalidRequestParameterException)ex).getErrorCode(),((InvalidRequestParameterException) ex).getErrorText(),mainResponsedto);
		}
		else if (ex instanceof InvalidRequestParameterException && (serviceType != null && serviceType.equals("userIdOtp"))) {
			throw new InvalidRequestParameterException(((InvalidRequestParameterException)ex).getErrorCode(),((InvalidRequestParameterException) ex).getErrorText(),mainResponsedto);
		}
		else if (ex instanceof LoginServiceException) {
			throw new LoginServiceException(((LoginServiceException) ex).getValidationErrorList(),mainResponsedto);
		}
		else if (ex instanceof ParseResponseException) {
			throw new ParseResponseException(((ParseResponseException) ex).getErrorCode(),((ParseResponseException) ex).getErrorText(),mainResponsedto);
		}
		else if (ex instanceof ConfigFileNotFoundException) {
			throw new ConfigFileNotFoundException(((ConfigFileNotFoundException) ex).getErrorCode(),((ConfigFileNotFoundException) ex).getErrorText(),mainResponsedto);
		}
		else if(ex instanceof InvalidOtpOrUseridException) {
			throw new InvalidOtpOrUseridException(ErrorCodes.PRG_AUTH_013.getCode(),((InvalidOtpOrUseridException) ex).getErrorText(),mainResponsedto);
		}
		else if (ex instanceof NoAuthTokenException) {
			throw new NoAuthTokenException(((NoAuthTokenException) ex).getErrorCode(), ((NoAuthTokenException) ex).getErrorText(), mainResponsedto);
		}
		
		
	}
}
