package io.mosip.kernel.packetuploader.http.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import io.mosip.kernel.packetuploader.http.constant.PacketUploaderExceptionConstants;
import io.mosip.kernel.packetuploader.http.exception.MosipDirectoryNotEmpty;
import io.mosip.kernel.packetuploader.http.exception.MosipIOException;
import io.mosip.kernel.packetuploader.http.exception.MosipInvalidFileName;
import io.mosip.kernel.packetuploader.http.exception.MosipPacketLocationSecurity;

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
	@ExceptionHandler(MosipDirectoryNotEmpty.class)
	public ResponseEntity<ErrorItem> handle(MosipDirectoryNotEmpty e) {
		ErrorItem error = new ErrorItem();
		error.setMessage(
				PacketUploaderExceptionConstants.MOSIP_DIRECTORY_NOT_EMPTY_FILE_LOCATION_EXCEPTION.getErrorMessage());
		error.setCode(
				PacketUploaderExceptionConstants.MOSIP_DIRECTORY_NOT_EMPTY_FILE_LOCATION_EXCEPTION.getErrorCode());
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
		error.setMessage(PacketUploaderExceptionConstants.MOSIP_IO_FILE_EXCEPTION.getErrorMessage());
		error.setCode(PacketUploaderExceptionConstants.MOSIP_IO_FILE_EXCEPTION.getErrorCode());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Exception handler to handle MosipPacketLocationSecurity
	 * 
	 * @param e
	 *            The InvalidRequestException
	 * @return The Response entity with error response
	 */
	@ExceptionHandler(MosipPacketLocationSecurity.class)
	public ResponseEntity<ErrorItem> handle(MosipPacketLocationSecurity e) {
		ErrorItem error = new ErrorItem();
		error.setMessage(PacketUploaderExceptionConstants.MOSIP_SECURITY_FILE_LOCATION_EXCEPTION.getErrorMessage());
		error.setCode(PacketUploaderExceptionConstants.MOSIP_SECURITY_FILE_LOCATION_EXCEPTION.getErrorCode());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Exception handler to handle MosipInvalidFileName
	 * 
	 * @param e
	 *            The InvalidRequestException
	 * @return The Response entity with error response
	 */
	@ExceptionHandler(MosipInvalidFileName.class)
	public ResponseEntity<ErrorItem> handle(MosipInvalidFileName e) {
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
		error.setMessage(PacketUploaderExceptionConstants.MOSIP_PACKET_SIZE_EXCEPTION.getErrorMessage());
		error.setCode(PacketUploaderExceptionConstants.MOSIP_PACKET_SIZE_EXCEPTION.getErrorCode());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

}