package io.mosip.kernel.packetuploader.http.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import io.mosip.kernel.packetuploader.http.constant.PacketUploaderExceptionConstant;
import io.mosip.kernel.packetuploader.http.exception.MosipDirectoryNotEmptyException;
import io.mosip.kernel.packetuploader.http.exception.MosipIOException;
import io.mosip.kernel.packetuploader.http.exception.MosipInvalidFileNameException;
import io.mosip.kernel.packetuploader.http.exception.MosipPacketLocationSecurityException;

/**
 * Class for handling Packet Uploader exceptions
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class PacketUploaderExceptionHandler {
	/**
	 * This method handle MosipUploadDirectoryException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */
	@ExceptionHandler(MosipDirectoryNotEmptyException.class)
	public ResponseEntity<ErrorItem> handle(MosipDirectoryNotEmptyException e) {
		ErrorItem error = new ErrorItem();
		error.setMessage(
				PacketUploaderExceptionConstant.MOSIP_DIRECTORY_NOT_EMPTY_FILE_LOCATION_EXCEPTION.getErrorMessage());
		error.setCode(
				PacketUploaderExceptionConstant.MOSIP_DIRECTORY_NOT_EMPTY_FILE_LOCATION_EXCEPTION.getErrorCode());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * This method handle MosipIOException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */
	@ExceptionHandler(MosipIOException.class)
	public ResponseEntity<ErrorItem> handle(MosipIOException e) {

		ErrorItem error = new ErrorItem();
		error.setMessage(PacketUploaderExceptionConstant.MOSIP_IO_FILE_EXCEPTION.getErrorMessage());
		error.setCode(PacketUploaderExceptionConstant.MOSIP_IO_FILE_EXCEPTION.getErrorCode());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Exception handler to handle MosipPacketLocationSecurity
	 * 
	 * @param e
	 *            The InvalidRequestException
	 * @return The Response entity with error response
	 */
	@ExceptionHandler(MosipPacketLocationSecurityException.class)
	public ResponseEntity<ErrorItem> handle(MosipPacketLocationSecurityException e) {
		ErrorItem error = new ErrorItem();
		error.setMessage(PacketUploaderExceptionConstant.MOSIP_SECURITY_FILE_LOCATION_EXCEPTION.getErrorMessage());
		error.setCode(PacketUploaderExceptionConstant.MOSIP_SECURITY_FILE_LOCATION_EXCEPTION.getErrorCode());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Exception handler to handle MosipInvalidFileName
	 * 
	 * @param e
	 *            The InvalidRequestException
	 * @return The Response entity with error response
	 */
	@ExceptionHandler(MosipInvalidFileNameException.class)
	public ResponseEntity<ErrorItem> handle(MosipInvalidFileNameException e) {
		ErrorItem error = new ErrorItem();
		error.setMessage(e.getErrorText());
		error.setCode(e.getErrorCode());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Exception handler to handle FileUploadBase.FileSizeLimitExceededException
	 * 
	 * @param e
	 *            The InvalidRequestException
	 * @return The Response entity with error response
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorItem> handle(MultipartException e) {
		ErrorItem error = new ErrorItem();
		error.setMessage(PacketUploaderExceptionConstant.MOSIP_PACKET_SIZE_EXCEPTION.getErrorMessage());
		error.setCode(PacketUploaderExceptionConstant.MOSIP_PACKET_SIZE_EXCEPTION.getErrorCode());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

}