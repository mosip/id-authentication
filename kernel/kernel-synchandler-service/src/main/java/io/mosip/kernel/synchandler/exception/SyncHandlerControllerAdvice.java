package io.mosip.kernel.synchandler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * synch handler controller advice
 * 
 * @author Abhishek Kumar
 * @since 29-11-2018
 *
 */
@RestControllerAdvice
public class SyncHandlerControllerAdvice {
	@ExceptionHandler(MasterDataServiceException.class)
	public ResponseEntity<ErrorResponse<Error>> controlDataServiceException(final MasterDataServiceException e) {
		return new ResponseEntity<>(getErrorResponse(e), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(DateParsingException.class)
	public ResponseEntity<ErrorResponse<Error>> controlDataServiceException(final DateParsingException e) {
		return new ResponseEntity<>(getErrorResponse(e), HttpStatus.BAD_REQUEST);
	}

	private ErrorResponse<Error> getErrorResponse(BaseUncheckedException e) {
		Error error = new Error(e.getErrorCode(), e.getErrorText());
		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().add(error);
		return errorResponse;
	}
}
