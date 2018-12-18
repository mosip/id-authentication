package io.mosip.registration.processor.manual.verification.controller;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.PacketNotFoundException;
import io.mosip.registration.processor.manual.verification.exception.FileNotPresentException;
import io.mosip.registration.processor.manual.verification.exception.InvalidFileNameException;
import io.mosip.registration.processor.manual.verification.exception.InvalidUpdateException;
import io.mosip.registration.processor.manual.verification.exception.NoRecordAssignedException;
import io.mosip.registration.processor.status.dto.ExceptionJSONInfo;

/**
 * The Exception Handler class for Manual Verification
 * 
 * @author Pranav
 * @since 0.0.1
 *
 */
@RestControllerAdvice
public class ManualVerificationExceptionHandler {

	/**
	 * Method to handle Invalid File Name request Exception
	 * 
	 * @param e
	 *            {@link InvalidFileNameException}
	 * @param request
	 *            WebRequest
	 * @return The built ResponseEntity with error code and error message
	 */
	@ExceptionHandler(InvalidFileNameException.class)
	public ResponseEntity<ExceptionJSONInfo> invalidFileNameExceptionHandler(final InvalidFileNameException e,
			WebRequest request) {
		ExceptionJSONInfo exceptionJSONInfo = new ExceptionJSONInfo(e.getErrorCode(), e.getLocalizedMessage());
		return new ResponseEntity<>(exceptionJSONInfo, HttpStatus.NOT_FOUND);
	}

	/**
	 * Method to handle PacketNotFoundException
	 * 
	 * @param e
	 *            {@link PacketNotFoundException}
	 * @param request
	 *            WebRequest
	 * @return The built ResponseEntity with error code and error message
	 */
	@ExceptionHandler(PacketNotFoundException.class)
	public ResponseEntity<ExceptionJSONInfo> packetNotFoundExceptionHandler(final PacketNotFoundException e,
			WebRequest request) {
		FileNotPresentException fileNotPresentException = new FileNotPresentException(
				PlatformErrorMessages.RPR_MVS_FILE_NOT_PRESENT.getCode(),
				PlatformErrorMessages.RPR_MVS_FILE_NOT_PRESENT.getMessage());
		ExceptionJSONInfo exceptionJSONInfo = new ExceptionJSONInfo(fileNotPresentException.getErrorCode(),
				fileNotPresentException.getLocalizedMessage());
		return new ResponseEntity<>(exceptionJSONInfo, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(NoRecordAssignedException.class)
	public ResponseEntity<ExceptionJSONInfo> noRecordAssignedExceptionHandler(final NoRecordAssignedException e,
			WebRequest request) {
		ExceptionJSONInfo exceptionJSONInfo = new ExceptionJSONInfo(e.getErrorCode(),
				e.getLocalizedMessage());
		return new ResponseEntity<>(exceptionJSONInfo, HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler(InvalidUpdateException.class)
	public ResponseEntity<ExceptionJSONInfo> invalidUpdateException(final InvalidUpdateException e,
			WebRequest request) {
		ExceptionJSONInfo exceptionJSONInfo = new ExceptionJSONInfo(e.getErrorCode(), e.getLocalizedMessage());
		return new ResponseEntity<>(exceptionJSONInfo, HttpStatus.FORBIDDEN);
	}

}
