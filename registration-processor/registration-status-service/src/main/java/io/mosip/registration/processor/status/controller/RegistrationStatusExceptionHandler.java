package io.mosip.registration.processor.status.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.status.dto.ExceptionJSONInfo;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.netty.handler.codec.http.HttpResponse;
import javassist.tools.web.BadHttpRequest;

/**
 * The Class RegistrationStatusExceptionHandler.
 */
@RestControllerAdvice
public class RegistrationStatusExceptionHandler {
	
	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(RegistrationStatusExceptionHandler.class);

	/**
	 * Duplicateentry.
	 *
	 * @param Exception as e
	 * @param WebRequest as request
	 * @return the response entity
	 */
	@ExceptionHandler(TablenotAccessibleException.class)
	public ResponseEntity<ExceptionJSONInfo> duplicateentry(final TablenotAccessibleException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getErrorText());
		log.error(e.getErrorCode(), e.getCause());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ExceptionJSONInfo> badRequest(JsonMappingException ex) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getCode(),
				"JSON Mapping Exception");
		log.error(PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getCode(), PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getMessage());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ExceptionJSONInfo> badRequest(MethodArgumentNotValidException ex) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getCode(),
				"langCode must be of 3 characters");
		log.error(PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getCode(), PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getMessage());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ExceptionJSONInfo> dataExceptionHandler(final DataIntegrityViolationException e, WebRequest request) {
		ExceptionJSONInfo exe = new ExceptionJSONInfo( "RPR-DBE-001","Data Integrity Violation Exception");
		log.error( "RPR-DBE-001 Data integrity violation exception",e.getMessage());
		return new ResponseEntity<>(exe, HttpStatus.BAD_REQUEST);
	}

}
