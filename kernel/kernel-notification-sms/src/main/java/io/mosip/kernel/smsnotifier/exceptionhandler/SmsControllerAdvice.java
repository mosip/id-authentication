package io.mosip.kernel.smsnotifier.exceptionhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.smsnotifier.constant.SmsExceptionConstants;
import io.mosip.kernel.smsnotifier.exception.JsonParseException;
import io.mosip.kernel.smsnotifier.exception.MosipHttpClientException;

/**
 * Central class for handling exceptions.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class SmsControllerAdvice {

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
	public ResponseEntity<Map<String, ArrayList<MosipErrors>>> smsNotificationValidation(
			final MethodArgumentNotValidException exception) {
		MosipErrors error = new MosipErrors(SmsExceptionConstants.SMS_ILLEGAL_INPUT.getErrorCode(),
				SmsExceptionConstants.SMS_ILLEGAL_INPUT.getErrorMessage());
		ArrayList<MosipErrors> errorList = new ArrayList<>();
		errorList.add(error);

		Map<String, ArrayList<MosipErrors>> map = new HashMap<>();
		map.put(err, errorList);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);

	}

	/**
	 * This method handles MosipHttpClientException type of exceptions.
	 * 
	 * @param e
	 *            The exception
	 * @return The response entity.
	 */
	@ExceptionHandler(MosipHttpClientException.class)
	public ResponseEntity<Map<String, ArrayList<MosipErrors>>> smsNotificationServerResponse(
			final MosipHttpClientException e) {

		MosipErrors error = new MosipErrors(SmsExceptionConstants.SMS_NUMBER_INVALID.getErrorCode(), e.getErrorText());
		ArrayList<MosipErrors> errorList = new ArrayList<>();
		errorList.add(error);

		Map<String, ArrayList<MosipErrors>> map = new HashMap<>();
		map.put(err, errorList);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);

	}

	@ExceptionHandler(JsonParseException.class)
	public ResponseEntity<Map<String, ArrayList<MosipErrors>>> smsNotificationJsonResponse(final JsonParseException e) {
		MosipErrors error = new MosipErrors(SmsExceptionConstants.SMS_EMPTY_JSON.getErrorCode(),
				SmsExceptionConstants.SMS_EMPTY_JSON.getErrorMessage());
		ArrayList<MosipErrors> errorList = new ArrayList<>();
		errorList.add(error);

		Map<String, ArrayList<MosipErrors>> map = new HashMap<>();
		map.put(err, errorList);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);

	}

}
