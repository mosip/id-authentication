package io.mosip.kernel.idgenerator.uin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.idgenerator.uin.constant.UinGeneratorErrorCode;

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
	public ResponseEntity<ErrorResponse<Error>> uinNotFoundHandler(UinNotFoundException e) {

		Error error = new Error(UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorCode(),
				UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorMessage());

		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().add(error);

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
}