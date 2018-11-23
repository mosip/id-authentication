package io.mosip.kernel.auditmanager.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private static final String ERR = "error";
	private static final String WHITESPACE = " ";

	/**
	 * This method handle MethodArgumentNotValidException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> methodArgumentNotValidException(
			final MethodArgumentNotValidException e) {
		ArrayList<ErrorBean> errorList = new ArrayList<>();
		BindingResult bindingResult = e.getBindingResult();
		final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		fieldErrors.forEach(x -> {
			ErrorBean error = new ErrorBean(AuditErrorCode.HANDLEREXCEPTION.getErrorCode(),
					Character.toUpperCase(x.getField().charAt(0)) + x.getField().substring(1) + WHITESPACE
							+ x.getDefaultMessage());
			errorList.add(error);
		});
		Map<String, ArrayList<ErrorBean>> map = new HashMap<>();
		map.put(ERR, errorList);
		return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
	}

	/**
	 * This method handle InvalidFormatException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */
	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> methodArgumentFormatException(InvalidFormatException e) {

		ArrayList<ErrorBean> errorList = new ArrayList<>();
		ErrorBean error = new ErrorBean(AuditErrorCode.INVALIDFORMAT.getErrorCode(),
				AuditErrorCode.INVALIDFORMAT.getErrorMessage());
		errorList.add(error);
		Map<String, ArrayList<ErrorBean>> map = new HashMap<>();
		map.put(ERR, errorList);
		return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

	}

}