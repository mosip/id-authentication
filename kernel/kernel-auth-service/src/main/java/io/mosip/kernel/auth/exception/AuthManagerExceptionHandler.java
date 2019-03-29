/**
 * 
 */
package io.mosip.kernel.auth.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.ServiceError;

/**
 * @author M1049825
 *
 */
@RestControllerAdvice
public class AuthManagerExceptionHandler {
	
	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ErrorResponse<ServiceError>> defaultErrorHandler(HttpServletRequest request, Exception e) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		ServiceError error = new ServiceError("500", e.getMessage());
		errorResponse.getErrors().add(error);
		errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(value = { AuthManagerException.class })
	public ResponseEntity<ErrorResponse<ServiceError>> customErrorMessage(HttpServletRequest request, AuthManagerException e) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		ServiceError error = new ServiceError(e.getErrorCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		errorResponse.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}
	
	@ExceptionHandler(value = { AuthManagerServiceException.class })
	public ResponseEntity<ErrorResponse<ServiceError>> customErrorMessageList(HttpServletRequest request, AuthManagerServiceException e) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().addAll(e.getList());
		errorResponse.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

}
