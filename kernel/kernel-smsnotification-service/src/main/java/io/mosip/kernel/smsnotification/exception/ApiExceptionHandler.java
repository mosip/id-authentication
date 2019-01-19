package io.mosip.kernel.smsnotification.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.smsnotification.constant.SmsExceptionConstant;

/**
 * Central class for handling exceptions.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class ApiExceptionHandler {
	private static final String WHITESPACE = " ";

	/**
	 * This method handles MethodArgumentNotValidException type of exceptions.
	 * 
	 * @param exception
	 *            The exception
	 * @return The response entity.
	 * 
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse<Error>> smsInvalidInputsFound(final MethodArgumentNotValidException exception) {

		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		BindingResult bindingResult = exception.getBindingResult();
		final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		fieldErrors.forEach(x -> {
			Error error = new Error(SmsExceptionConstant.SMS_ILLEGAL_INPUT.getErrorCode(),
					x.getField() + WHITESPACE + x.getDefaultMessage());
			errorResponse.getErrors().add(error);
		});

		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);

	}

	/**
	 * This method handles MosipInvalidNumberException type of exceptions.
	 * 
	 * @param e
	 *            The exception
	 * @return The response entity.
	 */
	@ExceptionHandler(InvalidNumberException.class)
	public ResponseEntity<ErrorResponse<Error>> smsNotificationInvalidNumber(final InvalidNumberException e) {

		return new ResponseEntity<>(getErrorResponse(e), HttpStatus.NOT_ACCEPTABLE);

	}

	private ErrorResponse<Error> getErrorResponse(BaseUncheckedException e) {
		Error error = new Error(e.getErrorCode(), e.getErrorText());
		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().add(error);
		return errorResponse;
	}
}
