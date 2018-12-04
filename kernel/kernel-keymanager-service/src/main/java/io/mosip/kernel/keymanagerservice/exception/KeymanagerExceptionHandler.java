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
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.keymanagerservice.constant.KeyManagerConstant;
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
	public ResponseEntity<ErrorResponse<Error>> nullDataException(final NullDataException e) {
		return new ResponseEntity<>(getErrorResponse(e.getErrorCode(),e.getErrorText()), HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(InvalidKeyException.class)
	public ResponseEntity<ErrorResponse<Error>> invalidKeyException(final InvalidKeyException e) {
		return new ResponseEntity<>(getErrorResponse(e.getErrorCode(),e.getErrorText()), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NoSuchAlgorithmException.class)
	public ResponseEntity<ErrorResponse<Error>>  noSuchAlgorithmException(
			final NoSuchAlgorithmException e) {
		return new ResponseEntity<>(getErrorResponse(e.getErrorCode(),e.getErrorText()), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<ErrorResponse<Error>> invalidFormatException(
			final InvalidFormatException e) {
		return new ResponseEntity<>(getErrorResponse(KeymanagerErrorConstant.DATE_TIME_PARSE_EXCEPTION.getErrorCode(),e.getMessage()+KeyManagerConstant.WHITESPACE+KeymanagerErrorConstant.DATE_TIME_PARSE_EXCEPTION.getErrorMessage()), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<ErrorResponse<Error>> invalidDataException(final InvalidDataException e) {
		return new ResponseEntity<>(getErrorResponse(e.getErrorCode(),e.getErrorText()) ,HttpStatus.BAD_REQUEST);
	}
	
    @ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse<Error>> methodArgumentNotValidException(
			final MethodArgumentNotValidException e) {
		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			Error error = new Error(KeymanagerErrorConstant.INVALID_REQUEST.getErrorCode(),x.getField() + KeyManagerConstant.WHITESPACE + x.getDefaultMessage());
			errorResponse.getErrors().add(error);
		});
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
    
    private ErrorResponse<Error> getErrorResponse(String errorCode,String errorMessage) {
		Error error = new Error(errorCode, errorMessage);
		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().add(error);
		return errorResponse;
	}
}
