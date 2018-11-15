package io.mosip.kernel.otpmanager.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class ApiExceptionHandler {
	/**
	 * This variable represents the errors.
	 */
	String err = "errors";

	/**
	 * This method handles MethodArgumentNotValidException type of exceptions.
	 * 
	 * @param exception
	 *            The exception
	 * @return The response entity.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, ArrayList<Error>>> otpGeneratorValidity(
			final MethodArgumentNotValidException exception) {
		Error error = new Error(OtpErrorConstants.OTP_GEN_ILLEGAL_KEY_INPUT.getErrorCode(),
				OtpErrorConstants.OTP_GEN_ILLEGAL_KEY_INPUT.getErrorMessage());
		ArrayList<Error> errorList = new ArrayList<>();
		errorList.add(error);
		Map<String, ArrayList<Error>> map = new HashMap<>();
		map.put(err, errorList);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * This method handles OtpInvalidArgumentExceptionHandler type of
	 * exceptions.
	 * 
	 * @param exception
	 *            The exception.
	 * @return The response entity.
	 */
	@ExceptionHandler(InvalidArgumentExceptionHandler.class)
	public ResponseEntity<Object> otpValidationValidity(final InvalidArgumentExceptionHandler exception) {
		Map<String, List<Error>> map = new HashMap<>();
		map.put(err, exception.getList());
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}
	
	@ExceptionHandler(ResourceNotFoundExceptionHandler.class)
	public ResponseEntity<Object> otpResourceValidity(final ResourceNotFoundExceptionHandler exception) {
		Map<String, List<Error>> map = new HashMap<>();
		map.put(err, exception.getList());
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}
}
