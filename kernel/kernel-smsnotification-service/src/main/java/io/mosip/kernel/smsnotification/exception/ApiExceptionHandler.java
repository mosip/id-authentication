package io.mosip.kernel.smsnotification.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.smsnotification.constant.SmsExceptionConstant;

/**
 * Central class for handling exceptions.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
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
	public ResponseEntity<Map<String, ArrayList<Error>>> smsNotificationValidation(
			final MethodArgumentNotValidException exception) {
		Error error = new Error(SmsExceptionConstant.SMS_ILLEGAL_INPUT.getErrorCode(),
				SmsExceptionConstant.SMS_ILLEGAL_INPUT.getErrorMessage());
		ArrayList<Error> errorList = new ArrayList<>();
		errorList.add(error);

		Map<String, ArrayList<Error>> map = new HashMap<>();
		map.put(err, errorList);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);

	}

	/**
	 * This method handles MosipInvalidNumberException type of exceptions.
	 * 
	 * @param e
	 *            The exception
	 * @return The response entity.
	 */
	@ExceptionHandler(InvalidNumberException.class)
	public ResponseEntity<Map<String, ArrayList<Error>>> smsNotificationInvalidNumber(
			final InvalidNumberException e) {
		Error error = new Error(e.getErrorCode(), e.getErrorText());
		ArrayList<Error> errorList = new ArrayList<>();
		errorList.add(error);

		Map<String, ArrayList<Error>> map = new HashMap<>();
		map.put(err, errorList);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);

	}

}
