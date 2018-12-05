package io.mosip.registration.processor.manual.adjudication.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.registration.processor.manual.adjudication.dto.ExceptionJSONInfo;
import io.mosip.registration.processor.manual.adjudication.exception.FileNotPresentException;

@RestControllerAdvice
public class ManualAdjudicationExceptionHandler {

	@ExceptionHandler(FileNotPresentException.class)
	public ResponseEntity<ExceptionJSONInfo> invalidFileException(FileNotPresentException e){
		ExceptionJSONInfo exceptionJSONInfo = new ExceptionJSONInfo(e.getErrorCode(), e.getLocalizedMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionJSONInfo);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionJSONInfo> genericException(Exception e){
		ExceptionJSONInfo exceptionJSONInfo = new ExceptionJSONInfo("Internal Server Error", "");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionJSONInfo);
	}
}
