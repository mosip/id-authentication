/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.keymanagerservice.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.crypto.exception.NullDataException;
import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.keymanagerservice.constant.KeymanagerConstant;
import io.mosip.kernel.keymanagerservice.constant.KeymanagerErrorConstant;

/**
 * Rest Controller Advice for Keymanager Service
 * 
 * @author Dharmesh Khandelwal
 *
 * @since 1.0.0
 */
@RestControllerAdvice
public class KeymanagerExceptionHandler {

	@ExceptionHandler(NullDataException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> nullDataException(final NullDataException e) {
		return new ResponseEntity<>(getErrorResponse(e.getErrorCode(), e.getErrorText(), HttpStatus.BAD_REQUEST),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidKeyException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> invalidKeyException(final InvalidKeyException e) {
		return new ResponseEntity<>(
				getErrorResponse(e.getErrorCode(), e.getErrorText(), HttpStatus.INTERNAL_SERVER_ERROR),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NoSuchAlgorithmException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> noSuchAlgorithmException(final NoSuchAlgorithmException e) {
		return new ResponseEntity<>(
				getErrorResponse(e.getErrorCode(), e.getErrorText(), HttpStatus.INTERNAL_SERVER_ERROR),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> invalidFormatException(final InvalidFormatException e) {
		return new ResponseEntity<>(getErrorResponse(KeymanagerErrorConstant.DATE_TIME_PARSE_EXCEPTION.getErrorCode(),
				e.getMessage() + KeymanagerConstant.WHITESPACE
						+ KeymanagerErrorConstant.DATE_TIME_PARSE_EXCEPTION.getErrorMessage(),
				HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> invalidDataException(final InvalidDataException e) {
		return new ResponseEntity<>(getErrorResponse(e.getErrorCode(), e.getErrorText(), HttpStatus.BAD_REQUEST),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NoUniqueAliasException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> noUniqueAliasException(final NoUniqueAliasException e) {
		return new ResponseEntity<>(
				getErrorResponse(e.getErrorCode(), e.getErrorText(), HttpStatus.INTERNAL_SERVER_ERROR),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(CryptoException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> cryptoException(final CryptoException e) {
		return new ResponseEntity<>(
				getErrorResponse(e.getErrorCode(), e.getErrorText(), HttpStatus.INTERNAL_SERVER_ERROR),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(InvalidApplicationIdException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> invalidApplicationIdException(
			final InvalidApplicationIdException e) {
		return new ResponseEntity<>(
				getErrorResponse(e.getErrorCode(), e.getErrorText(), HttpStatus.INTERNAL_SERVER_ERROR),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> methodArgumentNotValidException(
			final MethodArgumentNotValidException e) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError(KeymanagerErrorConstant.INVALID_REQUEST.getErrorCode(),
					x.getField() + KeymanagerConstant.WHITESPACE + x.getDefaultMessage());
			errorResponse.getErrors().add(error);
			errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
		});
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	private ErrorResponse<ServiceError> getErrorResponse(String errorCode, String errorMessage, HttpStatus httpStatus) {
		ServiceError error = new ServiceError(errorCode, errorMessage);
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().add(error);
		errorResponse.setStatus(httpStatus.value());
		return errorResponse;
	}
}
