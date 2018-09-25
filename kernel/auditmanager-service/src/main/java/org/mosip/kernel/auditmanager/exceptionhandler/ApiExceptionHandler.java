package org.mosip.kernel.auditmanager.exceptionhandler;

import org.mosip.kernel.auditmanager.constants.AuditErrorCodes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

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
	public ResponseEntity<ErrorItem> handle(MethodArgumentNotValidException e) {

		ErrorItem error = new ErrorItem();
		error.setMessage(AuditErrorCodes.HANDLEREXCEPTION.getErrorMessage());
		error.setCode(AuditErrorCodes.HANDLEREXCEPTION.getErrorCode());

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
	public ResponseEntity<ErrorItem> handle(InvalidFormatException e) {

		ErrorItem error = new ErrorItem();
		error.setMessage(AuditErrorCodes.INVALIDFORMAT.getErrorMessage());
		error.setCode(AuditErrorCodes.INVALIDFORMAT.getErrorCode());

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

	}

}