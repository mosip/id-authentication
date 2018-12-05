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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.crypto.exception.NullDataException;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.crypto.constant.CryptoConstant;
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

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse<Error>> illegalArgumentException(
			final IllegalArgumentException e) {
		return new ResponseEntity<>(getErrorResponse(CryptoErrorCode.INVALID_DATA_WITHOUT_KEY_BREAKER.getErrorCode(), CryptoErrorCode.INVALID_DATA_WITHOUT_KEY_BREAKER.getErrorMessage()), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<ErrorResponse<Error>> invalidFormatException(
			final InvalidFormatException e) {
		return new ResponseEntity<>(getErrorResponse(CryptoErrorCode.DATE_TIME_PARSE_EXCEPTION.getErrorCode(),e.getMessage()+CryptoConstant.WHITESPACE+CryptoErrorCode.DATE_TIME_PARSE_EXCEPTION.getErrorMessage()), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<ErrorResponse<Error>> invalidDataException(final InvalidDataException e) {
		return new ResponseEntity<>(getErrorResponse(e.getErrorCode(),e.getErrorText()+CryptoErrorCode.INVALID_DATA.getErrorMessage()) ,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ConnectException.class)
	public ResponseEntity<ErrorResponse<Error>> connectException(final ConnectException e) {
		return new ResponseEntity<>(getErrorResponse(CryptoErrorCode.CANNOT_CONNECT_TO_SOFTHSM_SERVICE.getErrorCode(), CryptoErrorCode.CANNOT_CONNECT_TO_SOFTHSM_SERVICE.getErrorMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<ErrorResponse<Error>> httpClientErrorException(final HttpClientErrorException e) {
		return new ResponseEntity<>(getErrorResponse(CryptoErrorCode.KEYMANAGER_SERVICE_ERROR.getErrorCode(),CryptoErrorCode.KEYMANAGER_SERVICE_ERROR.getErrorMessage()+CryptoConstant.WHITESPACE+e.getResponseBodyAsString()), e.getStatusCode());
	}
	
	@ExceptionHandler(HttpServerErrorException.class)
	public ResponseEntity<ErrorResponse<Error>> httpServerErrorException(final HttpServerErrorException e) {
		return new ResponseEntity<>(getErrorResponse(CryptoErrorCode.KEYMANAGER_SERVICE_ERROR.getErrorCode(),CryptoErrorCode.KEYMANAGER_SERVICE_ERROR.getErrorMessage()+CryptoConstant.WHITESPACE+e.getResponseBodyAsString()), e.getStatusCode());
	}
	
    @ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse<Error>> methodArgumentNotValidException(
			final MethodArgumentNotValidException e) {
		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			Error error = new Error(CryptoErrorCode.INVALID_REQUEST.getErrorCode(),x.getField() + CryptoConstant.WHITESPACE + x.getDefaultMessage());
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
