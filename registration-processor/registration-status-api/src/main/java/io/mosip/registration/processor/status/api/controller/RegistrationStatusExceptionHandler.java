package io.mosip.registration.processor.status.api.controller;

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
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.status.dto.ExceptionJSONInfo;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;

/**
 * The Class RegistrationStatusExceptionHandler.
 */
@RestControllerAdvice
public class RegistrationStatusExceptionHandler {
	

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(RegistrationStatusExceptionHandler.class);

	/**
	 * Duplicateentry.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(TablenotAccessibleException.class)
	public ResponseEntity<ExceptionJSONInfo> duplicateentry(final TablenotAccessibleException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getErrorText());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getCause().toString());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Bad request.
	 *
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ExceptionJSONInfo> badRequest(JsonMappingException ex) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getCode(),
				"JSON Mapping Exception");
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getCode(),PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getMessage());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Bad request.
	 *
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ExceptionJSONInfo> badRequest(MethodArgumentNotValidException ex) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getCode(),
				"langCode must be of 3 characters");
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getCode(),PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getMessage());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Data exception handler.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ExceptionJSONInfo> dataExceptionHandler(final DataIntegrityViolationException e, WebRequest request) {
		ExceptionJSONInfo exe = new ExceptionJSONInfo( "RPR-DBE-001","Data Integrity Violation Exception");
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),"RPR-DBE-001 Data integrity violation exception",e.getMessage());
		return new ResponseEntity<>(exe, HttpStatus.BAD_REQUEST);
	}

}
