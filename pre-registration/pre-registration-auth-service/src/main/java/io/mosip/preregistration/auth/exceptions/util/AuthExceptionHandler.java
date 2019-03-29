package io.mosip.preregistration.auth.exceptions.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.auth.dto.MainResponseDTO;
import io.mosip.preregistration.auth.exceptions.AuthServiceException;
import io.mosip.preregistration.auth.exceptions.ConfigFileNotFoundException;
import io.mosip.preregistration.auth.exceptions.InvalidateTokenException;
import io.mosip.preregistration.auth.exceptions.ParseResponseException;
import io.mosip.preregistration.auth.exceptions.SendOtpFailedException;
import io.mosip.preregistration.auth.exceptions.UserIdOtpFaliedException;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.GenericUtil;

/**
 *This class handles the exception caught while login
 *
 * @author Akshay
 *@since 1.0.0
 */

@RestControllerAdvice
public class AuthExceptionHandler {
	
	@Value("${mosip.utc-datetime-pattern}")
	private String utcDateTimePattern;

	@ExceptionHandler(SendOtpFailedException.class)
	public ResponseEntity<MainResponseDTO<?>> sendOtpException(final SendOtpFailedException e,WebRequest request){
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(),
				e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	
	@ExceptionHandler(UserIdOtpFaliedException.class)
	public ResponseEntity<MainResponseDTO<?>> userIdOtpException(final UserIdOtpFaliedException e,WebRequest request){
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(),
				e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	@ExceptionHandler(InvalidateTokenException.class)
	public ResponseEntity<MainResponseDTO<?>> invalidateTokenException(final InvalidateTokenException e,WebRequest request){
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(),
				e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	
	@ExceptionHandler(InvalidRequestParameterException.class)
	public ResponseEntity<MainResponseDTO<?>> invalidRequestParameterException(final InvalidRequestParameterException e,WebRequest request){
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(),
				e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	} 
	
	@ExceptionHandler(AuthServiceException.class)
	public ResponseEntity<MainResponseDTO<?>> authServiceException(final AuthServiceException e,WebRequest request){
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		e.getValidationErrorList().stream().forEach(serviceError->errorList.add(new ExceptionJSONInfoDTO(serviceError.getErrorCode(),serviceError.getErrorMessage())));
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		errorRes.setId(e.getMainResposneDTO().getId());
		errorRes.setVersion(e.getMainResposneDTO().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	
	@ExceptionHandler(ParseResponseException.class)
	public ResponseEntity<MainResponseDTO<?>> parseResponseException(final ParseResponseException e,WebRequest request){
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(),
				e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
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
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	private String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), utcDateTimePattern);

	}
}
