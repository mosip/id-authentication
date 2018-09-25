package org.mosip.kernel.uingenerator.exceptionhandler;

import org.mosip.kernel.uingenerator.constants.UinGeneratorErrorCodes;
import org.mosip.kernel.uingenerator.exception.UinNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
	@ExceptionHandler(UinNotFoundException.class)
	public ResponseEntity<ErrorItem> handle(MethodArgumentNotValidException e) {

		ErrorItem error = new ErrorItem();
		error.setMessage(UinGeneratorErrorCodes.UIN_NOT_FOUND.getErrorMessage());
		error.setCode(UinGeneratorErrorCodes.UIN_NOT_FOUND.getErrorCode());

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

	}

}