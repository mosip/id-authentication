package io.mosip.preregistration.transliteration.exception.util;

import java.sql.Timestamp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import io.mosip.preregistration.transliteration.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.transliteration.dto.ResponseDTO;
import io.mosip.preregistration.transliteration.exception.FailedToTransliterateException;
import io.mosip.preregistration.transliteration.exception.MandatoryFieldRequiredException;

public class TransliterationExceptionHandler {
	
	protected String falseStatus = "false";
	
	@ExceptionHandler(MandatoryFieldRequiredException.class)
	public ResponseEntity<ResponseDTO<?>> mandatoryFieldrequired(final MandatoryFieldRequiredException e,WebRequest request){
		
		ExceptionJSONInfoDTO errorDetails=new ExceptionJSONInfoDTO(e.getErrorCode(),e.getErrorText());
		ResponseDTO<?> errorRes=new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		errorRes.setStatus(falseStatus);
		
		return new ResponseEntity<>(errorRes,HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(FailedToTransliterateException.class)
	public ResponseEntity<ResponseDTO<?>> translitrationFailed(final FailedToTransliterateException e,WebRequest request){
		
		ExceptionJSONInfoDTO errorDetails=new ExceptionJSONInfoDTO(e.getErrorCode(),e.getErrorText());
		ResponseDTO<?> errorRes=new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		errorRes.setStatus(falseStatus);
		
		return new ResponseEntity<>(errorRes,HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
