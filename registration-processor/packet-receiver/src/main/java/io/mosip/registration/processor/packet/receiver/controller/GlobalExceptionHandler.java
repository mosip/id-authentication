package io.mosip.registration.processor.packet.receiver.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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
@RestControllerAdvice
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
	@ExceptionHandler(DuplicateUploadRequestException.class)
	public ResponseEntity<ExceptionJSONInfo> duplicateentry(final DuplicateUploadRequestException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getErrorText());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getCause().toString());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle packet not available exception.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(MissingServletRequestPartException.class)
	public ResponseEntity<ExceptionJSONInfo> handlePacketNotAvailableException(
			final MissingServletRequestPartException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(PlatformErrorMessages.RPR_PKR_PACKET_NOT_AVAILABLE.getCode(),
				PlatformErrorMessages.RPR_PKR_PACKET_NOT_AVAILABLE.getMessage());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),errorDetails.getErrorcode(), e.getCause().toString());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle packet not valid exception.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(PacketNotValidException.class)
	public ResponseEntity<ExceptionJSONInfo> handlePacketNotValidException(final PacketNotValidException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getErrorText());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getCause().toString());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle file size exceed exception.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(FileSizeExceedException.class)
	public ResponseEntity<ExceptionJSONInfo> handleFileSizeExceedException(final FileSizeExceedException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getErrorText());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getCause().toString());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle file size exceed exception.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(PacketNotSyncException.class)
	public ResponseEntity<ExceptionJSONInfo> handleFileSizeExceedException(final PacketNotSyncException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getErrorText());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getCause().toString());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle tablenot accessible exception.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(TablenotAccessibleException.class)
	public ResponseEntity<ExceptionJSONInfo> handleTablenotAccessibleException(final TablenotAccessibleException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getErrorText());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getCause().toString());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle timeout exception.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(TimeoutException.class)
	public ResponseEntity<ExceptionJSONInfo> handleTimeoutException(final TimeoutException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getMessage());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getCause().toString());
		return new ResponseEntity<>(errorDetails, HttpStatus.REQUEST_TIMEOUT);
	}

	/**
	 * Handle unexpected exception.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(UnexpectedException.class)
	public ResponseEntity<ExceptionJSONInfo> handleUnexpectedException(final UnexpectedException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getMessage());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getCause().toString());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle validation exception.
	 *
	 * @param e the e
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ExceptionJSONInfo> handleValidationException(final TimeoutException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getMessage());
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getCause().toString());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ExceptionJSONInfo> dataExceptionHandler(final DataIntegrityViolationException e, WebRequest request) {
		ExceptionJSONInfo exe = new ExceptionJSONInfo( "RPR-DBE-001","Data Integrity Violation Exception");
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),"RPR-DBE-001 Data integrity violation exception",e.getMessage());
		return new ResponseEntity<>(exe, HttpStatus.BAD_REQUEST);
	}

}
