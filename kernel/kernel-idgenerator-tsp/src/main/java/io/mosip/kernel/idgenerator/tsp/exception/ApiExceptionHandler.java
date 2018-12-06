package io.mosip.kernel.idgenerator.tsp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.ServiceError;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(TspIdServiceException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> controlDataServiceException(final TspIdServiceException e) {
		return getErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
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
