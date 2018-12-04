package io.mosip.registration.processor.manual.adjudication.controller;

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
		return ResponseEntity.badRequest().body(exceptionJSONInfo);
	}
}
