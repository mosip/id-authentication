package io.mosip.kernel.otpnotification.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.otpnotification.constant.OtpNotificationConstant;
import io.mosip.kernel.otpnotification.constant.OtpNotificationErrorConstant;

@RestControllerAdvice
public class ApiExceptionalHandler {

	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> httpClientErrorException(final HttpClientErrorException e) {
		return new ResponseEntity<>(
				getErrorResponse(OtpNotificationErrorConstant.NOTIFIER_SERVER_ERROR.getErrorCode(),
						OtpNotificationErrorConstant.NOTIFIER_SERVER_ERROR.getErrorMessage()
								+ OtpNotificationConstant.WHITESPACE + e.getResponseBodyAsString(),
						HttpStatus.OK),
				HttpStatus.OK);
	}
	
	
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> methodArgumentNotValidException(
			final MethodArgumentNotValidException e) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError("xxxx",
					x.getField() + ": " + x.getDefaultMessage());
			errorResponse.getErrors().add(error);
			errorResponse.setStatus(HttpStatus.OK.value());
		});
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(OtpNotifierServiceException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> controlDataServiceException(
			final OtpNotifierServiceException e) {
		return getErrorResponseEntity(e, HttpStatus.OK);
	}

	private ErrorResponse<ServiceError> getErrorResponse(String errorCode, String errorMessage, HttpStatus httpStatus) {
		ServiceError error = new ServiceError(errorCode, errorMessage);
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().add(error);
		errorResponse.setStatus(httpStatus.value());
		return errorResponse;
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
