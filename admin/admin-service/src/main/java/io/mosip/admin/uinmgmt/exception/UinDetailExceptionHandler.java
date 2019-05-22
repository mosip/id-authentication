package io.mosip.admin.uinmgmt.exception;

import java.util.Arrays;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;

/**
 * 
 * Rest Controller Advice for Exception Handler
 * 
 * @author Megha Tanga
 *
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UinDetailExceptionHandler {

	@ExceptionHandler(UinDetailNotFoundException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> handlerError(UinDetailNotFoundException e) {
		ResponseWrapper<ServiceError> errorResponse = new ResponseWrapper<>();
		ServiceError error = new ServiceError(e.getErrorCode(), e.getErrorText());
		errorResponse.setErrors(Arrays.asList(error));
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

}
