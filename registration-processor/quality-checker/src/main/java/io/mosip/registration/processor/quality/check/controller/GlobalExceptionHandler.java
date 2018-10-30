package io.mosip.registration.processor.quality.check.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.registration.processor.quality.check.dto.ExceptionJSONInfo;
import io.mosip.registration.processor.quality.check.exception.QcUserNotFoundException;
import io.mosip.registration.processor.quality.check.exception.RegistrationIdNotFoundException;
import io.mosip.registration.processor.quality.check.exception.ResultNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResultNotFoundException.class)
	public ResponseEntity<ExceptionJSONInfo> resultNotFound(final ResultNotFoundException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getErrorText());
		log.error(e.getErrorCode(), e.getCause());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(QcUserNotFoundException.class)
	public ResponseEntity<ExceptionJSONInfo> qcUserIdNotFound(final QcUserNotFoundException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getErrorText());
		log.error(e.getErrorCode(), e.getCause());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RegistrationIdNotFoundException.class)
	public ResponseEntity<ExceptionJSONInfo> regIdNotFound(final RegistrationIdNotFoundException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getErrorText());
		log.error(e.getErrorCode(), e.getCause());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}
}
