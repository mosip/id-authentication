package io.mosip.kernel.smsnotifier.exceptionhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.MosipIOException;
import io.mosip.kernel.core.util.exception.MosipJsonMappingException;
import io.mosip.kernel.core.util.exception.MosipJsonParseException;
import io.mosip.kernel.smsnotifier.constant.SmsExceptionConstants;
import io.mosip.kernel.smsnotifier.dto.SmsServerResponseDto;

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
	 * This method handles HttpClientErrorException type of exceptions.
	 * 
	 * @param e
	 *            The exception
	 * @return The response entity.
	 */
	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<Map<String, ArrayList<MosipErrors>>> smsNotificationServerResponse(
			final HttpClientErrorException e) {

		SmsServerResponseDto responseDto = null;
		try {
			responseDto = (SmsServerResponseDto) JsonUtils.jsonStringToJavaObject(SmsServerResponseDto.class,
					e.getResponseBodyAsString());
		} catch (MosipJsonParseException | MosipJsonMappingException | MosipIOException e1) {
			throw new JsonParseException(SmsExceptionConstants.SMS_EMPTY_JSON.getErrorCode(),
					SmsExceptionConstants.SMS_EMPTY_JSON.getErrorMessage(), e1.getCause());
		}

		MosipErrors error = new MosipErrors(SmsExceptionConstants.SMS_NUMBER_INVALID.getErrorCode(),
				responseDto.getMessage());
		ArrayList<MosipErrors> errorList = new ArrayList<>();
		errorList.add(error);

		Map<String, ArrayList<MosipErrors>> map = new HashMap<>();
		map.put(err, errorList);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);

	}

}
