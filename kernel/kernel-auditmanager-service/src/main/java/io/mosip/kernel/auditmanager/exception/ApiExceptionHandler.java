package io.mosip.kernel.auditmanager.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.mosip.kernel.auditmanager.constant.AuditErrorCode;

/**
 * Class for handling API exceptions
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	private static final String WHITESPACE = " ";

	/**
	 * This method handle MethodArgumentNotValidException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse<Error>> methodArgumentNotValidException(
			final MethodArgumentNotValidException e) {

		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		BindingResult bindingResult = e.getBindingResult();
		final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		fieldErrors.forEach(x -> {
			Error error = new Error(AuditErrorCode.HANDLEREXCEPTION.getErrorCode(),
					Character.toUpperCase(x.getField().charAt(0)) + x.getField().substring(1) + WHITESPACE
							+ x.getDefaultMessage());
			errorResponse.getErrors().add(error);
		});

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

	}

	/**
	 * This method handle InvalidFormatException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */
	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<ErrorResponse<Error>> methodArgumentFormatException(InvalidFormatException e) {

		Error error = new Error(AuditErrorCode.INVALIDFORMAT.getErrorCode(),
				AuditErrorCode.INVALIDFORMAT.getErrorMessage());
		ErrorResponse<Error> errorResponse = new ErrorResponse<>();
		errorResponse.getErrors().add(error);

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

	}

}