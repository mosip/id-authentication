package org.mosip.registration.processor.packet.receiver.controller;

import org.mosip.registration.processor.packet.receiver.dto.ExceptionJSONInfo;
import org.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import org.mosip.registration.processor.packet.receiver.exception.FileSizeExceedException;
import org.mosip.registration.processor.packet.receiver.exception.PacketNotValidException;
import org.mosip.registration.processor.packet.receiver.exception.ValidationException;
import org.mosip.registration.processor.packet.receiver.exception.systemexception.TimeoutException;
import org.mosip.registration.processor.packet.receiver.exception.systemexception.UnexpectedException;
import org.mosip.registration.processor.packet.receiver.exception.utils.IISPlatformErrorCodes;
import org.mosip.registration.processor.status.code.RegistrationStatusCode;
import org.mosip.registration.processor.status.exception.TablenotAccessibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(DuplicateUploadRequestException.class)
	public ResponseEntity<ExceptionJSONInfo> duplicateentry(final DuplicateUploadRequestException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getErrorText());
		log.error(e.getErrorCode(), e.getCause());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MissingServletRequestPartException.class)
	public ResponseEntity<ExceptionJSONInfo> handlePacketNotAvailableException(
			final MissingServletRequestPartException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(IISPlatformErrorCodes.IIS_EPU_ATU_PACKET_NOT_AVAILABLE,
				RegistrationStatusCode.PACKET_NOT_PRESENT_IN_REQUEST.toString());
		log.error(errorDetails.getErrorcode(), e.getCause());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(PacketNotValidException.class)
	public ResponseEntity<ExceptionJSONInfo> handlePacketNotValidException(final PacketNotValidException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getErrorText());
		log.error(e.getErrorCode(), e.getCause());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(FileSizeExceedException.class)
	public ResponseEntity<ExceptionJSONInfo> handleFileSizeExceedException(final FileSizeExceedException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getErrorText());
		log.error(errorDetails.getErrorcode(), e.getCause());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(TablenotAccessibleException.class)
	public ResponseEntity<ExceptionJSONInfo> handleTablenotAccessibleException(final TablenotAccessibleException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getErrorText());
		log.error(e.getErrorCode(), e.getCause());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(TimeoutException.class)
	public ResponseEntity<ExceptionJSONInfo> handleTimeoutException(final TimeoutException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getMessage());
		log.error(e.getErrorCode(), e.getCause());
		return new ResponseEntity<>(errorDetails, HttpStatus.REQUEST_TIMEOUT);
	}

	@ExceptionHandler(UnexpectedException.class)
	public ResponseEntity<ExceptionJSONInfo> handleUnexpectedException(final UnexpectedException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getMessage());
		log.error(e.getErrorCode(), e.getCause());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ExceptionJSONInfo> handleValidationException(final TimeoutException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getMessage());
		log.error(e.getErrorCode(), e.getCause());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

}
