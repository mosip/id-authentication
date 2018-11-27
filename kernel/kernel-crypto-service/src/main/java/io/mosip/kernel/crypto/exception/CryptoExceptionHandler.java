/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.exception;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.crypto.exception.NullDataException;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.crypto.constant.CryptoErrorCode;

/**
 * Rest Controller Advice for Cryptographic Service
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@RestControllerAdvice
public class CryptoExceptionHandler {

	private static final String ERR = "error";
	private static final String WHITESPACE = " ";

	private Map<String, ArrayList<Error>> setError(Error error) {
		ArrayList<Error> errorList = new ArrayList<>();
		errorList.add(error);
		Map<String, ArrayList<Error>> map = new HashMap<>();
		map.put(ERR, errorList);
		return map;
	}

	@ExceptionHandler(NullDataException.class)
	public ResponseEntity<Map<String, ArrayList<Error>>> nullDataException(final NullDataException e) {
		Error error = new Error(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<Error>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(InvalidKeyException.class)
	public ResponseEntity<Map<String, ArrayList<Error>>> invalidKeyException(final InvalidKeyException e) {
		Error error = new Error(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<Error>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NoSuchAlgorithmException.class)
	public ResponseEntity<Map<String, ArrayList<Error>>> noSuchAlgorithmException(
			final NoSuchAlgorithmException e) {
		Error error = new Error(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<Error>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ArrayIndexOutOfBoundsException.class)
	public ResponseEntity<Map<String, ArrayList<Error>>> arrayIndexOutOfBoundsException(
			final ArrayIndexOutOfBoundsException e) {
		Error error = new Error(CryptoErrorCode.INVALID_DATA_WITHOUT_KEY_BREAKER.getErrorCode(),
				CryptoErrorCode.INVALID_DATA_WITHOUT_KEY_BREAKER.getErrorMessage());
		Map<String, ArrayList<Error>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<Map<String, ArrayList<Error>>> invalidDataException(final InvalidDataException e) {
		Error error = new Error(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<Error>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ConnectException.class)
	public ResponseEntity<Map<String, ArrayList<Error>>> connectException(final ConnectException e) {
		Error error = new Error(CryptoErrorCode.CANNOT_CONNECT_TO_SOFTHSM_SERVICE.getErrorCode(), CryptoErrorCode.CANNOT_CONNECT_TO_SOFTHSM_SERVICE.getErrorMessage());
		Map<String, ArrayList<Error>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	

	
    @ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse<Error>> methodArgumentNotValidException(
			final MethodArgumentNotValidException e) {
		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			Error error = new Error(CryptoErrorCode.INVALID_REQUEST.getErrorCode(),
					x.getField() + WHITESPACE + x.getDefaultMessage());
			errorResponse.getErrors().add(error);
		});
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
}
