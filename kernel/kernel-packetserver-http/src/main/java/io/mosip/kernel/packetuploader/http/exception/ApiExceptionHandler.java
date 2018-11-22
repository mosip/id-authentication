package io.mosip.kernel.packetuploader.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import io.mosip.kernel.core.exception.BaseUncheckedException;
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
	public ResponseEntity<ErrorResponse<Error>> handle(DirectoryNotEmptyException e) {
		return new ResponseEntity<>(getErrorResponse(e), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * This method handle IOException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */
	@ExceptionHandler(IOException.class)
	public ResponseEntity<ErrorResponse<Error>> handle(IOException e) {
		 ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		Error error = new Error();
		error.setMessage(e.getErrorText());
		error.setCode(e.getErrorCode());
		errorResponse.getErrors().add(error);
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
	public ResponseEntity<ErrorResponse<Error>> handle(PacketLocationSecurityException e) {
		return new ResponseEntity<>(getErrorResponse(e) , HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Exception handler to handle FileUploadBase.FileSizeLimitExceededException
	 * 
	 * @param e
	 *            The InvalidRequestException
	 * @return The Response entity with error response
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorResponse<Error>> handle(MultipartException e) {
       ErrorResponse<Error> errorResponse = new ErrorResponse<>();
	    Error error = new Error();
		error.setMessage(PacketUploaderExceptionConstant.MOSIP_PACKET_SIZE_EXCEPTION
						.getErrorMessage());
		error.setCode(PacketUploaderExceptionConstant.MOSIP_PACKET_SIZE_EXCEPTION
						.getErrorCode());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	private ErrorResponse<Error> getErrorResponse(BaseUncheckedException e) {
		Error error = new Error(e.getErrorCode(), e.getErrorText());
		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().add(error);
		return errorResponse;
	}

}