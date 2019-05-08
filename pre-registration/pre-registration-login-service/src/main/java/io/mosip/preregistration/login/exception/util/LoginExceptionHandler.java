package io.mosip.preregistration.login.exception.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.errorcodes.ErrorCodes;
import io.mosip.preregistration.core.errorcodes.ErrorMessages;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.GenericUtil;
import io.mosip.preregistration.login.exception.ConfigFileNotFoundException;
import io.mosip.preregistration.login.exception.InvalidOtpOrUseridException;
import io.mosip.preregistration.login.exception.InvalidateTokenException;
import io.mosip.preregistration.login.exception.LoginServiceException;
import io.mosip.preregistration.login.exception.ParseResponseException;
import io.mosip.preregistration.login.exception.SendOtpFailedException;
import io.mosip.preregistration.login.exception.UserIdOtpFaliedException;


/**
 *This class handles the exception caught while login
 *
 * @author Akshay
 *@since 1.0.0
 */

@RestControllerAdvice
public class LoginExceptionHandler {
	
	@Value("${mosip.utc-datetime-pattern}")
	private String utcDateTimePattern;

	@ExceptionHandler(SendOtpFailedException.class)
	public ResponseEntity<MainResponseDTO<?>> sendOtpException(final SendOtpFailedException e,WebRequest request){
		return GenericUtil.errorResponse(e, e.getMainResposneDto());
	}
	
	@ExceptionHandler(UserIdOtpFaliedException.class)
	public ResponseEntity<MainResponseDTO<?>> userIdOtpException(final UserIdOtpFaliedException e,WebRequest request){
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	}

	@ExceptionHandler(InvalidateTokenException.class)
	public ResponseEntity<MainResponseDTO<?>> invalidateTokenException(final InvalidateTokenException e,WebRequest request){
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	}
	
	@ExceptionHandler(InvalidRequestParameterException.class)
	public ResponseEntity<MainResponseDTO<?>> invalidRequestParameterException(final InvalidRequestParameterException e,WebRequest request){
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	} 
	
	@ExceptionHandler(LoginServiceException.class)
	public ResponseEntity<MainResponseDTO<?>> authServiceException(final LoginServiceException e,WebRequest request){
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		e.getValidationErrorList().stream().forEach(serviceError->errorList.add(new ExceptionJSONInfoDTO(serviceError.getErrorCode(),serviceError.getMessage())));
		MainResponseDTO<?> errorRes = e.getMainResposneDTO();
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	
	@ExceptionHandler(ParseResponseException.class)
	public ResponseEntity<MainResponseDTO<?>> parseResponseException(final ParseResponseException e,WebRequest request){
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	} 
	
	
	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for ConfigFileNotFoundException
	 */
	@ExceptionHandler(ConfigFileNotFoundException.class)
	public ResponseEntity<MainResponseDTO<?>> configFileNotFoundException(final ConfigFileNotFoundException e, WebRequest request) {
		return GenericUtil.errorResponse(e, e.getMainResposneDto());
	}
	
	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<MainResponseDTO<?>> DateFormatException(final InvalidFormatException e,WebRequest request){
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_CORE_REQ_003.getCode(),ErrorMessages.INVALID_REQUEST_DATETIME.getMessage());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	
	@ExceptionHandler(InvalidOtpOrUseridException.class)
	public ResponseEntity<MainResponseDTO<?>> InavlidOtpOrUserIdException(final InvalidOtpOrUseridException e,WebRequest request){
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	}
	
	
}
