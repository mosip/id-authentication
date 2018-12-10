package io.mosip.kernel.emailnotification.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.ServiceError;

/**
 * Central exception handler for mail-notifier service.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
@RestControllerAdvice
public class ApiExceptionHandler {
	/**
	 * @param exception
	 *            the exception to be handled.
	 * @return the error map.
	 */
	@ExceptionHandler(InvalidArgumentsException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> mailNotifierArgumentsValidation(final InvalidArgumentsException exception) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().addAll(exception.getList());
		errorResponse.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
	}
}
