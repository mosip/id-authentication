package io.mosip.kernel.applicanttype.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.ServiceError;

/**
 * Rest Controller Advice for Applicant type Data
 * 
 * @author Bal Vikash Sharma
 *
 * @since 1.0.0
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(ApplicantTypeServiceException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> controlDataServiceException(final ApplicantTypeServiceException e) {
		return getErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> controlDataNotFoundException(final DataNotFoundException e) {
		return getErrorResponseEntity(e, HttpStatus.OK);
	}

	@ExceptionHandler(RequestException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> controlRequestException(final RequestException e) {
		return getErrorResponseEntity(e, HttpStatus.OK);
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