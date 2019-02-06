package io.mosip.kernel.lkeymanager.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.lkeymanager.constant.LicenseKeyManagerErrorCodes;

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

	/**
	 * Method to handle {@link HttpMessageNotReadableException}.
	 * 
	 * @param exception
	 *            the exception.
	 * @return {@link ErrorResponse}.
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> onHttpMessageNotReadable(
			final HttpMessageNotReadableException exception) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		ServiceError error = new ServiceError(LicenseKeyManagerErrorCodes.HTTP_MESSAGE_NOT_READABLE.getErrorCode(),
				exception.getMessage());
		errorResponse.getErrors().add(error);
		errorResponse.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * Method to handle {@link RuntimeException}.
	 * 
	 * @param request
	 *            the servlet request.
	 * @param exception
	 *            the exception.
	 * @return {@link ErrorResponse}.
	 */
	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ErrorResponse<ServiceError>> defaultErrorHandler(HttpServletRequest request,
			Exception exception) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		ServiceError error = new ServiceError(LicenseKeyManagerErrorCodes.RUNTIME_EXCEPTION.getErrorCode(),
				exception.getMessage());
		errorResponse.getErrors().add(error);
		errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
