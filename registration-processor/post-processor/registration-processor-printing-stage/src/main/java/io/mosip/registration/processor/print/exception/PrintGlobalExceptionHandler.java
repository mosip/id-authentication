package io.mosip.registration.processor.print.exception;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.pdfgenerator.exception.PDFGeneratorException;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ExceptionJSONInfo;
import io.mosip.registration.processor.core.exception.TemplateProcessingFailureException;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.queue.impl.exception.ConnectionUnavailableException;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.vertx.core.json.DecodeException;

/**
 * The Class PrintGlobalExceptionHandler.
 */
public class PrintGlobalExceptionHandler {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PrintGlobalExceptionHandler.class);

	/**
	 * Handle timeout exception.
	 *
	 * @param e            the e
	 * @return the response entity
	 */
	public String handleTimeoutException(final TimeoutException e) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getMessage());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				e.getErrorCode(), e.getStackTrace()[0].toString());
		return errorDetails.getMessage();
	}

	/**
	 * Handle unexpected exception.
	 *
	 * @param e            the e
	 * @return the response entity
	 */
	public String handleUnexpectedException(final UnexpectedException e) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getMessage());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				e.getErrorCode(), e.getStackTrace()[0].toString());
		return errorDetails.getMessage();
	}

	/**
	 * Data exception handler.
	 *
	 * @param e            the e
	 * @return the response entity
	 */
	public String dataExceptionHandler(final DecodeException e) {
		ExceptionJSONInfo exe = new ExceptionJSONInfo("RPR-DBE-001", "The Registration Packet Size is invalid");
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				"RPR-DBE-001 Data integrity violation exception", e.getMessage());
		return exe.getMessage();
	}

	/**
	 * Handle table not accessible exception.
	 *
	 * @param e the e
	 * @return the string
	 */
	private String handleTableNotAccessibleException(TablenotAccessibleException e) {
		ExceptionJSONInfo exe = new ExceptionJSONInfo("RPR-RGS-001", "The Registration Packet Size is invalid");
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				"RPR-DBE-003 Data integrity violation exception", e.getMessage());
		return exe.getMessage();
	}

	/**
	 * Internal exception.
	 *
	 * @param e the e
	 * @return the string
	 */
	private String internalException(Exception e) {
		ExceptionJSONInfo exe = new ExceptionJSONInfo("RPR-RGS-001", "The Registration Packet Size is invalid");
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				"RPR-DBE-003 Data integrity violation exception", e.getMessage());
		return exe.getMessage();
	}

	/**
	 * Handle pdf generation exception.
	 *
	 * @param e the e
	 * @return the string
	 */
	private String handlePdfGenerationException(PDFGeneratorException e) {
		ExceptionJSONInfo exe = new ExceptionJSONInfo("RPR-RGS-001", "The Registration Packet Size is invalid");
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				"RPR-DBE-003 Data integrity violation exception", e.getMessage());
		return exe.getMessage();
	}

	/**
	 * Handle template processing failure exception.
	 *
	 * @param e the e
	 * @return the string
	 */
	private String handleTemplateProcessingFailureException(TemplateProcessingFailureException e) {
		ExceptionJSONInfo exe = new ExceptionJSONInfo("RPR-RGS-001", "The Registration Packet Size is invalid");
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				"RPR-DBE-003 Data integrity violation exception", e.getMessage());
		return exe.getMessage();
	}

	/**
	 * Handle queue connection not found.
	 *
	 * @param e the e
	 * @return the string
	 */
	private String handleQueueConnectionNotFound(QueueConnectionNotFound e) {
		ExceptionJSONInfo exe = new ExceptionJSONInfo("RPR-RGS-001", "The Registration Packet Size is invalid");
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				"RPR-DBE-003 Data integrity violation exception", e.getMessage());
		return exe.getMessage();
	}

	/**
	 * Handle connection unavailable exception.
	 *
	 * @param e the e
	 * @return the string
	 */
	private String handleConnectionUnavailableException(ConnectionUnavailableException e) {
		ExceptionJSONInfo exe = new ExceptionJSONInfo("RPR-RGS-001", "The Registration Packet Size is invalid");
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				"RPR-DBE-003 Data integrity violation exception", e.getMessage());
		return exe.getMessage();
	}

	/**
	 * Handler.
	 *
	 * @param exe the exe
	 * @return the string
	 */
	public String handler(Throwable exe) {

		if (exe instanceof UnexpectedException)
			return handleUnexpectedException((UnexpectedException) exe);
		else if (exe instanceof TimeoutException)
			return handleTimeoutException((TimeoutException) exe);
		else if (exe instanceof TablenotAccessibleException)
			return handleTableNotAccessibleException((TablenotAccessibleException) exe);
		else if (exe instanceof DecodeException)
			return dataExceptionHandler((DecodeException) exe);
		else if (exe instanceof PDFGeneratorException)
			return handlePdfGenerationException((PDFGeneratorException) exe);
		else if (exe instanceof TemplateProcessingFailureException)
			return handleTemplateProcessingFailureException((TemplateProcessingFailureException) exe);
		else if (exe instanceof QueueConnectionNotFound)
			return handleQueueConnectionNotFound((QueueConnectionNotFound) exe);
		else if (exe instanceof ConnectionUnavailableException)
			return handleConnectionUnavailableException((ConnectionUnavailableException) exe);
		else
			return internalException((Exception) exe);
	}

}