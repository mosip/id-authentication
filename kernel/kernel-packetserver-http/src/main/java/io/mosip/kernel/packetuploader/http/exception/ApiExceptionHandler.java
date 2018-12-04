package io.mosip.kernel.packetuploader.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.DirectoryNotEmptyException;
import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.packetuploader.http.constant.PacketUploaderExceptionConstant;

/**
 * Class for handling Packet Uploader exceptions
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class ApiExceptionHandler {
	/**
	 * This method handle MosipUploadDirectoryException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */
	@ExceptionHandler(DirectoryNotEmptyException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> handle(DirectoryNotEmptyException e) {
		return getErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * This method handle IOException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */
	@ExceptionHandler(IOException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> handle(IOException e) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		ServiceError error = new ServiceError(e.getErrorCode(), e.getErrorText());
		errorResponse.getErrors().add(error);
		errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Exception handler to handle MosipPacketLocationSecurity
	 * 
	 * @param e
	 *            The InvalidRequestException
	 * @return The Response entity with error response
	 */
	@ExceptionHandler(PacketLocationSecurityException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> handle(PacketLocationSecurityException e) {
		return getErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Exception handler to handle FileUploadBase.FileSizeLimitExceededException
	 * 
	 * @param e
	 *            The InvalidRequestException
	 * @return The Response entity with error response
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> handle(MultipartException e) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		ServiceError error = new ServiceError(
				PacketUploaderExceptionConstant.MOSIP_PACKET_SIZE_EXCEPTION.getErrorCode(),
				PacketUploaderExceptionConstant.MOSIP_PACKET_SIZE_EXCEPTION.getErrorMessage());
		errorResponse.getErrors().add(error);
		errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	private ResponseEntity<ErrorResponse<ServiceError>> getErrorResponseEntity(BaseUncheckedException e,
			HttpStatus httpStatus) {
		ServiceError error = new ServiceError(e.getErrorCode(), e.getErrorText());
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().add(error);
		errorResponse.setStatus(httpStatus.value());
		return new ResponseEntity<>(errorResponse, httpStatus);
	}

}