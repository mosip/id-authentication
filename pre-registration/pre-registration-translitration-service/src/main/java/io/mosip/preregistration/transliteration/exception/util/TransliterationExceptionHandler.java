package io.mosip.preregistration.transliteration.exception.util;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.transliteration.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.transliteration.dto.MainResponseDTO;
import io.mosip.preregistration.transliteration.exception.FailedToTransliterateException;
import io.mosip.preregistration.transliteration.exception.MandatoryFieldRequiredException;

@RestControllerAdvice
public class TransliterationExceptionHandler {
	
	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	protected boolean falseStatus = false;
	
	@ExceptionHandler(MandatoryFieldRequiredException.class)
	public ResponseEntity<MainResponseDTO<?>> mandatoryFieldrequired(final MandatoryFieldRequiredException e,WebRequest request){
		
		ExceptionJSONInfoDTO errorDetails=new ExceptionJSONInfoDTO(e.getErrorCode(),e.getErrorText());
		MainResponseDTO<?> errorRes=new MainResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setResTime(DateUtils.formatDate(new Date(), dateTimeFormat));
		errorRes.setStatus(falseStatus);
		
		return new ResponseEntity<>(errorRes,HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(FailedToTransliterateException.class)
	public ResponseEntity<MainResponseDTO<?>> translitrationFailed(final FailedToTransliterateException e,WebRequest request){
		ExceptionJSONInfoDTO errorDetails=new ExceptionJSONInfoDTO(e.getErrorCode(),e.getErrorText());
		MainResponseDTO<?> errorRes=new MainResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setResTime(DateUtils.formatDate(new Date(), dateTimeFormat));
		errorRes.setStatus(falseStatus);
		return new ResponseEntity<>(errorRes,HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
