package io.mosip.kernel.otpmanager.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.otpmanager.constant.OtpErrorConstants;

/**
 * Central class for handling exceptions.
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
@RestControllerAdvice
public class OtpControllerAdvice {
	/**
	 * This variable represents the errors.
	 */
	String err = "errors";

	/**
	 * This method handles MethodArgumentNotValidException.
	 * 
	 * @param exception
	 *            The exception
	 * @return The response entity.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> otpGeneratorValidity(final MethodArgumentNotValidException e) {
		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			Error error = new Error(OtpErrorConstants.OTP_GEN_ILLEGAL_KEY_INPUT.getErrorCode(),
					OtpErrorConstants.OTP_GEN_ILLEGAL_KEY_INPUT.getErrorMessage());
			errorResponse.getErrors().add(error);
		});
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * This method handles OtpInvalidArgumentException.
	 * 
	 * @param exception
	 *            The exception.
	 * @return The response entity.
	 */
	@ExceptionHandler(OtpInvalidArgumentException.class)
	public ResponseEntity<Object> otpValidationArgumentValidity(final OtpInvalidArgumentException exception) {
		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().addAll(exception.getList());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * This method handles RequiredKeyNotFoundException.
	 * 
	 * @param exception
	 *            The exception.
	 * @return The response entity.
	 */
	@ExceptionHandler(RequiredKeyNotFoundException.class)
	public ResponseEntity<Object> otpValidationKeyNullValidity(final RequiredKeyNotFoundException exception) {
		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().addAll(exception.getList());

		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
	}
}
