package io.mosip.kernel.packetuploader.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import io.mosip.kernel.core.exception.DirectoryNotEmptyException;
import io.mosip.kernel.core.exception.IOException;
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
	public ResponseEntity<Error> handle(DirectoryNotEmptyException e) {
		Error error = new Error();
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
	@ExceptionHandler(IOException.class)
	public ResponseEntity<Error> handle(IOException e) {

		Error error = new Error();
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
	@ExceptionHandler(PacketLocationSecurityException.class)
	public ResponseEntity<Error> handle(PacketLocationSecurityException e) {
		Error error = new Error();
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
	@ExceptionHandler(InvalidFileNameException.class)
	public ResponseEntity<Error> handle(InvalidFileNameException e) {
		Error error = new Error();
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
	public ResponseEntity<Error> handle(MultipartException e) {
		Error error = new Error();
		error.setMessage(PacketUploaderExceptionConstant.MOSIP_PACKET_SIZE_EXCEPTION.getErrorMessage());
		error.setCode(PacketUploaderExceptionConstant.MOSIP_PACKET_SIZE_EXCEPTION.getErrorCode());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

}