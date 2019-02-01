package io.mosip.registration.processor.packet.receiver.exception.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.packet.receiver.dto.ExceptionJSONInfo;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import io.mosip.registration.processor.packet.receiver.exception.FileSizeExceedException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotSyncException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotValidException;
import io.mosip.registration.processor.packet.receiver.exception.ValidationException;
import io.mosip.registration.processor.packet.receiver.exception.systemexception.TimeoutException;
import io.mosip.registration.processor.packet.receiver.exception.systemexception.UnexpectedException;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;

/**
 * The Class GlobalExceptionHandler.
 */
public class GlobalExceptionHandler {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(GlobalExceptionHandler.class);

	/**
	 * Duplicateentry.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	public String duplicateentry(final DuplicateUploadRequestException e) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getMessage());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(),  e.getStackTrace()[0].toString());
		return errorDetails.getMessage();
	}

	/**
	 * Handle packet not available exception.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	public String handlePacketNotAvailableException(
			final MissingServletRequestPartException e) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(PlatformErrorMessages.RPR_PKR_PACKET_NOT_AVAILABLE.getCode(), PlatformErrorMessages.RPR_PKR_PACKET_NOT_AVAILABLE.getMessage());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),errorDetails.getErrorcode(),  e.getStackTrace()[0].toString());
		return errorDetails.getMessage();
	}

	/**
	 * Handle packet not valid exception.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	public String handlePacketNotValidException(final PacketNotValidException e) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getMessage());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(),  e.getStackTrace()[0].toString());
		return errorDetails.getMessage();
	}

	/**
	 * Handle file size exceed exception.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	public String handleFileSizeExceedException(final FileSizeExceedException e) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getMessage());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(),  e.getStackTrace()[0].toString());
		return errorDetails.getMessage();
	}

	/**
	 * Handle file size exceed exception.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	public String handlePacketNotSyncException(final PacketNotSyncException e) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getMessage());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getStackTrace()[0].toString());
		return errorDetails.getMessage();
	}

	/**
	 * Handle tablenot accessible exception.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	public String handleTablenotAccessibleException(final TablenotAccessibleException e) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getMessage());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(),  e.getStackTrace()[0].toString());
		return errorDetails.getMessage();
	}

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
	 * Handle validation exception.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	public String handleValidationException(final ValidationException e) {
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
		if(exe instanceof ValidationException)
			return handleValidationException((ValidationException) exe);
		if(exe instanceof UnexpectedException)
			return handleUnexpectedException((UnexpectedException)exe);
		if(exe instanceof TimeoutException)
			return handleTimeoutException((TimeoutException)exe);
		if(exe instanceof TablenotAccessibleException)
			return handleTablenotAccessibleException((TablenotAccessibleException)exe);
		if(exe instanceof PacketNotSyncException)
			return handlePacketNotSyncException((PacketNotSyncException)exe);
		if(exe instanceof FileSizeExceedException)
			return handleFileSizeExceedException((FileSizeExceedException)exe);
		if(exe instanceof PacketNotValidException)
			return handlePacketNotValidException((PacketNotValidException)exe);
		if(exe instanceof DuplicateUploadRequestException)
			return duplicateentry((DuplicateUploadRequestException)exe);
		if(exe instanceof MissingServletRequestPartException)
			return handlePacketNotAvailableException((MissingServletRequestPartException)exe);
		else return dataExceptionHandler((DataIntegrityViolationException) exe);
	}
}