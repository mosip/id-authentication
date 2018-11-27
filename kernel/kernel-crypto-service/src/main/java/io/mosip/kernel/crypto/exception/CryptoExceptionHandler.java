/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.exception;

import java.net.ConnectException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.crypto.exception.NullDataException;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.crypto.constant.CryptoErrorCode;

/**
 * Rest Controller Advice for Crypto Service
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@RestControllerAdvice
public class CryptoExceptionHandler {

	
	private static final String WHITESPACE = " ";

	

	@ExceptionHandler(NullDataException.class)
	public ResponseEntity<ErrorResponse<Error>> nullDataException(final NullDataException e) {
		return new ResponseEntity<>(getErrorResponse(e), HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(InvalidKeyException.class)
	public ResponseEntity<ErrorResponse<Error>> invalidKeyException(final InvalidKeyException e) {
		return new ResponseEntity<>(getErrorResponse(e), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NoSuchAlgorithmException.class)
	public ResponseEntity<ErrorResponse<Error>>  noSuchAlgorithmException(
			final NoSuchAlgorithmException e) {
		return new ResponseEntity<>(getErrorResponse(e), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ArrayIndexOutOfBoundsException.class)
	public ResponseEntity<ErrorResponse<Error>> arrayIndexOutOfBoundsException(
			final ArrayIndexOutOfBoundsException e) {
		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		Error error = new Error(CryptoErrorCode.INVALID_DATA_WITHOUT_KEY_BREAKER.getErrorCode(),
				CryptoErrorCode.INVALID_DATA_WITHOUT_KEY_BREAKER.getErrorMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<ErrorResponse<Error>> invalidDataException(final InvalidDataException e) {
		return new ResponseEntity<>(getErrorResponse(e) ,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ConnectException.class)
	public ResponseEntity<ErrorResponse<Error>> connectException(final ConnectException e) {
		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		Error error = new Error(CryptoErrorCode.CANNOT_CONNECT_TO_SOFTHSM_SERVICE.getErrorCode(), CryptoErrorCode.CANNOT_CONNECT_TO_SOFTHSM_SERVICE.getErrorMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	

	
    @ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse<Error>> methodArgumentNotValidException(
			final MethodArgumentNotValidException e) {
		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			Error error = new Error(CryptoErrorCode.INVALID_REQUEST.getErrorCode(),x.getField() + WHITESPACE + x.getDefaultMessage());
			errorResponse.getErrors().add(error);
		});
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
    
    private ErrorResponse<Error> getErrorResponse(BaseUncheckedException e) {
		Error error = new Error(e.getErrorCode(), e.getErrorText());
		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().add(error);
		return errorResponse;
	}
}
