package io.mosip.kernel.auditmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.mosip.kernel.auditmanager.constant.AuditErrorCode;

/**
 * Class for handling API exceptions
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	/**
	 * This method handle MethodArgumentNotValidException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Error> handle(MethodArgumentNotValidException e) {

		Error error = new Error();
		error.setMessage(AuditErrorCode.HANDLEREXCEPTION.getErrorMessage());
		error.setCode(AuditErrorCode.HANDLEREXCEPTION.getErrorCode());

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

	}
	
	/**
	 * This method handle InvalidFormatException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */
	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<Error> handle(InvalidFormatException e) {

		Error error = new Error();
		error.setMessage(AuditErrorCode.INVALIDFORMAT.getErrorMessage());
		error.setCode(AuditErrorCode.INVALIDFORMAT.getErrorCode());

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

	}

}