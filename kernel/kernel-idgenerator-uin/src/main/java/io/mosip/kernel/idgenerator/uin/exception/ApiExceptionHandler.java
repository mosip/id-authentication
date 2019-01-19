package io.mosip.kernel.idgenerator.uin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.ServiceError;
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
	public ResponseEntity<ErrorResponse<ServiceError>> uinNotFoundHandler(UinNotFoundException e) {
		ServiceError error = new ServiceError(UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorCode(),
				UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorMessage());
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().add(error);
		errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}
}