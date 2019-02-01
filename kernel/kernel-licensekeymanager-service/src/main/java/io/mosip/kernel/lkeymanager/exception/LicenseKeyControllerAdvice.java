package io.mosip.kernel.lkeymanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.ServiceError;

/**
 * Controller Advice class to handle {@link LicenseKeyServiceException},
 * {@link InvalidArgumentsException}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class LicenseKeyControllerAdvice {

	/**
	 * Method to handle {@link InvalidArgumentsException}.
	 * 
	 * @param exception
	 *            the exception.
	 * @return {@link ErrorResponse}.
	 */
	@ExceptionHandler(InvalidArgumentsException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> validateInputArguments(
			final InvalidArgumentsException exception) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().addAll(exception.getList());
		errorResponse.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * Method to handle {@link LicenseKeyServiceException}.
	 * 
	 * @param exception
	 *            the exception.
	 * @return {@link ErrorResponse}.
	 */
	@ExceptionHandler(LicenseKeyServiceException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> handleServiceException(
			final LicenseKeyServiceException exception) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().addAll(exception.getList());
		errorResponse.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}
}
