package io.mosip.kernel.otpnotification.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.otpnotification.constant.OtpNotificationErrorConstant;

/**
 * Central class for handling exceptions.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@RestControllerAdvice
public class ApiExceptionalHandler {

	public static final String WHITESPACE = " ";

	/**
	 * This method handles HttpClientErrorException.
	 * 
	 * @param e
	 *            the exception.
	 * @return the response entity.
	 */
	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> httpClientErrorException(final HttpClientErrorException e) {
		return new ResponseEntity<>(getErrorResponse(OtpNotificationErrorConstant.NOTIFIER_SERVER_ERROR.getErrorCode(),
				OtpNotificationErrorConstant.NOTIFIER_SERVER_ERROR.getErrorMessage() + WHITESPACE
						+ e.getResponseBodyAsString(),
				HttpStatus.OK), HttpStatus.OK);
	}

	/**
	 * This method handles OtpInvalidArgumentException.
	 * 
	 * @param exception
	 *            The exception.
	 * @return The response entity.
	 */
	@ExceptionHandler(OtpNotificationInvalidArgumentException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> otpValidationArgumentValidity(
			final OtpNotificationInvalidArgumentException exception) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().addAll(exception.getList());
		errorResponse.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * This method handles MethodArgumentNotValidException.
	 * 
	 * @param e
	 *            The exception.
	 * @return The response entity.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> methodArgumentNotValidException(
			final MethodArgumentNotValidException e) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError(
					OtpNotificationErrorConstant.NOTIFIER_INVALID_REQUEST_ERROR.getErrorCode(),
					x.getField() + ": " + x.getDefaultMessage());
			errorResponse.getErrors().add(error);
			errorResponse.setStatus(HttpStatus.OK.value());
		});
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * This method handles OtpNotifierServiceException.
	 * 
	 * @param e
	 *            the exception.
	 * @return the response entity.
	 */
	@ExceptionHandler(OtpNotifierServiceException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> controlDataServiceException(
			final OtpNotifierServiceException e) {
		return getErrorResponseEntity(e, HttpStatus.OK);
	}

	/**
	 * This method provide error response.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 * @param httpStatus
	 *            the http status of response.
	 * @return the {@link ErrorResponse}.
	 */
	private ErrorResponse<ServiceError> getErrorResponse(String errorCode, String errorMessage, HttpStatus httpStatus) {
		ServiceError error = new ServiceError(errorCode, errorMessage);
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().add(error);
		errorResponse.setStatus(httpStatus.value());
		return errorResponse;
	}

	/**
	 * This method provide error response entity.
	 * 
	 * @param e
	 *            the exception of type {@link BaseUncheckedException}.
	 * @param httpStatus
	 *            the http status.
	 * @return the response entity.
	 */
	private ResponseEntity<ErrorResponse<ServiceError>> getErrorResponseEntity(BaseUncheckedException e,
			HttpStatus httpStatus) {
		ServiceError error = new ServiceError(e.getErrorCode(), e.getErrorText());
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().add(error);
		errorResponse.setStatus(httpStatus.value());
		return new ResponseEntity<>(errorResponse, httpStatus);
	}
}
