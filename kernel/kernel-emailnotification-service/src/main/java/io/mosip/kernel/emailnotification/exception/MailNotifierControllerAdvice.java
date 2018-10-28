package io.mosip.kernel.emailnotification.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Central exception handler for mail-notifier service.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
@RestControllerAdvice
public class MailNotifierControllerAdvice {

	/**
	 * The error variable for error map.
	 */
	String err = "errors";

	/**
	 * @param exception
	 *            the exception to be handled.
	 * @return the error map.
	 */
	@ExceptionHandler(MosipMailNotifierInvalidArgumentsException.class)
	public ResponseEntity<Object> mailNotifierArgumentsValidation(
			final MosipMailNotifierInvalidArgumentsException exception) {
		Map<String, List<MosipErrors>> map = new HashMap<>();
		map.put(err, exception.getList());
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}
}
