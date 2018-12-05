package io.mosip.registration.processor.manual.adjudication.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.PacketNotFoundException;
import io.mosip.registration.processor.manual.adjudication.dto.ExceptionJSONInfo;
import io.mosip.registration.processor.manual.adjudication.exception.FileNotPresentException;

@RestControllerAdvice
public class ManualAdjudicationExceptionHandler {

	@ExceptionHandler(FileNotPresentException.class)
	public ResponseEntity<ExceptionJSONInfo> invalidFileExceptionHandler(final FileNotPresentException e, WebRequest request){
		ExceptionJSONInfo exceptionJSONInfo = new ExceptionJSONInfo(e.getErrorCode(), e.getLocalizedMessage());
		return new ResponseEntity<>(exceptionJSONInfo, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(PacketNotFoundException.class)
	public ResponseEntity<ExceptionJSONInfo> packetNotFoundExceptionHandler(final PacketNotFoundException e, WebRequest request){
		ExceptionJSONInfo exceptionJSONInfo = new ExceptionJSONInfo(e.getErrorCode(), e.getLocalizedMessage());
		return new ResponseEntity<>(exceptionJSONInfo, HttpStatus.BAD_REQUEST);
	}
	
}
