package io.mosip.kernel.emailnotification.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.emailnotification.constant.MailNotifierArgumentErrorConstants;

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
	public ResponseEntity<ErrorResponse<ServiceError>> mailNotifierArgumentsValidation(
			final InvalidArgumentsException exception) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().addAll(exception.getList());
		errorResponse.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> onHttpMessageNotReadable(
			final HttpMessageNotReadableException e) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		ServiceError error = new ServiceError(MailNotifierArgumentErrorConstants.REQUEST_DATA_NOT_VALID.getErrorCode(),
				e.getMessage());
		errorResponse.getErrors().add(error);
		errorResponse.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}
}
