/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptography.exceptionhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
	
	private Map<String, ArrayList<ErrorBean>> setError(ErrorBean error) {
		ArrayList<ErrorBean> errorList = new ArrayList<>();
		errorList.add(error);
		Map<String, ArrayList<ErrorBean>> map = new HashMap<>();
		map.put(ERR, errorList);
		return map;
	}
	
	@ExceptionHandler(NullDataException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> nullDataException(
			final NullDataException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}
	
	@ExceptionHandler(InvalidKeyException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> invalidKeyException(
			final InvalidKeyException e) {
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
		ErrorBean error = new ErrorBean(CryptographyErrorCode.INVALID_DATA_WITHOUT_KEY_BREAKER.getErrorCode(),CryptographyErrorCode.INVALID_DATA_WITHOUT_KEY_BREAKER.getErrorMessage());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
	}
}
