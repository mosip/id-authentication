package io.mosip.kernel.masterdata.exceptionhandler;

import java.time.format.DateTimeParseException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.constant.RegistrationCenterUserMappingHistoryErrorCode;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;

/**
 * Rest Controller Advice for Master Data
 * 
 * @author Dharmesh Khandelwal
 * @author Bal Vikash Sharma
 *
 * @since 1.0.0
 */
@RestControllerAdvice
public class MasterDataControllerAdvice extends ResponseEntityExceptionHandler {

	@ExceptionHandler(MasterDataServiceException.class)
	public ResponseEntity<ErrorResponse<Error>> controlDataServiceException(final MasterDataServiceException e) {
		return new ResponseEntity<>(getErrorResponse(e), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<ErrorResponse<Error>> controlDataNotFoundException(final DataNotFoundException e) {
		return new ResponseEntity<>(getErrorResponse(e), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(RequestException.class)
	public ResponseEntity<ErrorResponse<Error>> controlRequestException(final RequestException e) {
		return new ResponseEntity<>(getErrorResponse(e), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DateTimeParseException.class)
	public ResponseEntity<ErrorResponse<Error>> numberFormatException(final DateTimeParseException e) {
		Error error = new Error(RegistrationCenterUserMappingHistoryErrorCode.DATE_TIME_PARSE_EXCEPTION.getErrorCode(),
				e.getMessage() + MasterDataConstant.DATETIMEFORMAT);
		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		errorResponse.getErrorList().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return new ResponseEntity<>(getErrorResponse(ex, headers, status, request), HttpStatus.BAD_REQUEST);
	}

	private ErrorResponse<String> getErrorResponse(MethodArgumentNotValidException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		ErrorResponse<String> errorResponse = new ErrorResponse<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errorResponse.getErrorList().add(error.getField() + ": " + error.getDefaultMessage());
		}
		for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errorResponse.getErrorList().add(error.getObjectName() + ": " + error.getDefaultMessage());
		}

		return errorResponse;
	}

	private ErrorResponse<Error> getErrorResponse(BaseUncheckedException e) {
		Error error = new Error(e.getErrorCode(), e.getErrorText());
		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		errorResponse.getErrorList().add(error);
		return errorResponse;
	}

}
