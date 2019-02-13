package io.mosip.registration.processor.print.exception;

import org.springframework.dao.DataIntegrityViolationException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ExceptionJSONInfo;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;

public class PrintGlobalExceptionHandler {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PrintGlobalExceptionHandler.class);

	/**
	 * Handle timeout exception.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	public String handleTimeoutException(final TimeoutException e) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getMessage());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getStackTrace()[0].toString());
		return errorDetails.getMessage();
	}

	/**
	 * Handle unexpected exception.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	public String handleUnexpectedException(final UnexpectedException e) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getMessage());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(),  e.getStackTrace()[0].toString());
		return errorDetails.getMessage();
	}


	/**
	 * Data exception handler.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	public String dataExceptionHandler(final DataIntegrityViolationException e) {
		ExceptionJSONInfo exe = new ExceptionJSONInfo( "RPR-DBE-001","Data Integrity Violation Exception");
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),"RPR-DBE-001 Data integrity violation exception",e.getMessage());
		return exe.getMessage();
	}

	public String handler(Throwable exe) {
		
		if(exe instanceof UnexpectedException)
			return handleUnexpectedException((UnexpectedException)exe);
		if(exe instanceof TimeoutException)
			return handleTimeoutException((TimeoutException)exe);
		else 
			return dataExceptionHandler((DataIntegrityViolationException) exe);
	}
}