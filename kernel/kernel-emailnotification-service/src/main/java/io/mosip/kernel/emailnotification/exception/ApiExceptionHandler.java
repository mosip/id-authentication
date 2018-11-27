package io.mosip.kernel.emailnotification.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Central exception handler for mail-notifier service.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	/**
	 * The error variable for error map.
	 */
	String err = "errors";

	/**
	 * @param exception
	 *            the exception to be handled.
	 * @return the error map.
	 */
	@ExceptionHandler(InvalidArgumentsException.class)
	public ResponseEntity<Object> mailNotifierArgumentsValidation(final InvalidArgumentsException exception) {
		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().addAll(exception.getList());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
	}
}
