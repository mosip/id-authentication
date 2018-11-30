package io.mosip.kernel.masterdata.exception;

import java.time.format.DateTimeParseException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.constant.RegistrationCenterUserMappingHistoryErrorCode;
import io.mosip.kernel.masterdata.constant.RequestErrorCode;

/**
 * Rest Controller Advice for Master Data
 * 
 * @author Dharmesh Khandelwal
 * @author Bal Vikash Sharma
 *
 * @since 1.0.0
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(MasterDataServiceException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> controlDataServiceException(final MasterDataServiceException e) {
		return getErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> controlDataNotFoundException(final DataNotFoundException e) {
		return getErrorResponseEntity(e, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(RequestException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> controlRequestException(final RequestException e) {
		return getErrorResponseEntity(e, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DateTimeParseException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> numberFormatException(final DateTimeParseException e) {
		ServiceError error = new ServiceError(RegistrationCenterUserMappingHistoryErrorCode.DATE_TIME_PARSE_EXCEPTION.getErrorCode(),
				e.getMessage() + MasterDataConstant.DATETIMEFORMAT);
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return getErrorResponseEntity(ex, HttpStatus.BAD_REQUEST);
	}

	private ResponseEntity<Object> getErrorResponseEntity(MethodArgumentNotValidException ex, HttpStatus httpStatus) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		ex.getBindingResult().getFieldErrors().stream().forEach(e -> {
			ServiceError error = new ServiceError(RequestErrorCode.REQUEST_DATA_NOT_VALID.getErrorCode(),
					e.getField() + ": " + e.getDefaultMessage());
			errorResponse.getErrors().add(error);
		});
		return new ResponseEntity<>(errorResponse, httpStatus);
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