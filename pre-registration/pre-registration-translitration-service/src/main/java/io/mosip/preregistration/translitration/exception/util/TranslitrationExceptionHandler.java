package io.mosip.preregistration.translitration.exception.util;

import java.sql.Timestamp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import io.mosip.preregistration.translitration.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.translitration.dto.ResponseDTO;
import io.mosip.preregistration.translitration.exception.FailedToTranslitrateException;
import io.mosip.preregistration.translitration.exception.MandatoryFieldRequiredException;

public class TranslitrationExceptionHandler {
	
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

	@ExceptionHandler(FailedToTranslitrateException.class)
	public ResponseEntity<ResponseDTO<?>> translitrationFailed(final FailedToTranslitrateException e,WebRequest request){
		
		ExceptionJSONInfoDTO errorDetails=new ExceptionJSONInfoDTO(e.getErrorCode(),e.getErrorText());
		ResponseDTO<?> errorRes=new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		errorRes.setStatus(falseStatus);
		
		return new ResponseEntity<>(errorRes,HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
