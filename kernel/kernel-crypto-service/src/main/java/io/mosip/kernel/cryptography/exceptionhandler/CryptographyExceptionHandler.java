/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptography.exceptionhandler;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.crypto.exception.NullDataException;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.cryptography.constant.CryptographyErrorCode;

/**
 * Rest Controller Advice for Cryptographic Service
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@RestControllerAdvice
public class CryptographyExceptionHandler {

	private static final String ERR = "error";
	private static final String WHITESPACE = " ";

	private Map<String, ArrayList<ErrorBean>> setError(ErrorBean error) {
		ArrayList<ErrorBean> errorList = new ArrayList<>();
		errorList.add(error);
		Map<String, ArrayList<ErrorBean>> map = new HashMap<>();
		map.put(ERR, errorList);
		return map;
	}

	@ExceptionHandler(NullDataException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> nullDataException(final NullDataException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(InvalidKeyException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> invalidKeyException(final InvalidKeyException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NoSuchAlgorithmException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> noSuchAlgorithmException(
			final NoSuchAlgorithmException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ArrayIndexOutOfBoundsException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> arrayIndexOutOfBoundsException(
			final ArrayIndexOutOfBoundsException e) {
		ErrorBean error = new ErrorBean(CryptographyErrorCode.INVALID_DATA_WITHOUT_KEY_BREAKER.getErrorCode(),
				CryptographyErrorCode.INVALID_DATA_WITHOUT_KEY_BREAKER.getErrorMessage());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> invalidDataException(final InvalidDataException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ConnectException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> connectException(final ConnectException e) {
		ErrorBean error = new ErrorBean(CryptographyErrorCode.CANNOT_CONNECT_TO_SOFTHSM_SERVICE.getErrorCode(), CryptographyErrorCode.CANNOT_CONNECT_TO_SOFTHSM_SERVICE.getErrorMessage());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	

	
    @ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> methodArgumentNotValidException(
			final MethodArgumentNotValidException e) {
		ArrayList<ErrorBean> errorList = new ArrayList<>();
		BindingResult bindingResult = e.getBindingResult();
		final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		fieldErrors.forEach(x -> {
			ErrorBean error = new ErrorBean(CryptographyErrorCode.INVALID_REQUEST.getErrorCode(),
					x.getField() + WHITESPACE + x.getDefaultMessage());
			errorList.add(error);
		});
		Map<String, ArrayList<ErrorBean>> map = new HashMap<>();
		map.put(ERR, errorList);
		return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
	}
}
