package io.mosip.registration.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;

import io.mosip.registration.code.StatusCodes;
import io.mosip.registration.core.exceptions.TablenotAccessibleException;
import io.mosip.registration.dto.ExceptionJSONInfo;
import io.mosip.registration.errorcodes.ErrorCodes;
import io.mosip.registration.exception.DocumentNotValidException;

@RestControllerAdvice
public class RegistrationExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(RegistrationExceptionHandler.class);

	@ExceptionHandler(TablenotAccessibleException.class)
	public ResponseEntity<ExceptionJSONInfo> databaseerror(final TablenotAccessibleException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_PAM‌_007.toString(),
				StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(DocumentNotValidException.class)
	public ResponseEntity<ExceptionJSONInfo> notValidExceptionhadler(final DocumentNotValidException nv,
			WebRequest webRequest){
		
		ExceptionJSONInfo jsonInfo=new ExceptionJSONInfo(nv.getErrorCode(), nv.getErrorText());
		
		return new ResponseEntity<ExceptionJSONInfo>(jsonInfo, HttpStatus.BAD_REQUEST);
		
	}
	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<ExceptionJSONInfo> sizeExceedException(final MultipartException me,
			WebRequest webRequest){
		
		ExceptionJSONInfo jsonInfo=new ExceptionJSONInfo(ErrorCodes.PRG_PAM‌_004.toString(), 
											StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());
		
		return new ResponseEntity<ExceptionJSONInfo>(jsonInfo, HttpStatus.BAD_REQUEST);
	} 

}
