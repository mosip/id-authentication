/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.exception.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.errorcodes.ErrorCodes;
import io.mosip.preregistration.core.errorcodes.ErrorMessages;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.GenericUtil;
import io.mosip.preregistration.transliteration.exception.IllegalParamException;
import io.mosip.preregistration.transliteration.exception.JsonValidationException;
import io.mosip.preregistration.transliteration.exception.MandatoryFieldRequiredException;
import io.mosip.preregistration.transliteration.exception.UnSupportedLanguageException;

/**
 * Exception Handler for transliteration application.
 * 
 * @author Kishan rathore
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class TransliterationExceptionHandler {
	
	@Value("${mosip.utc-datetime-pattern}")
	private String utcDateTimepattern;
	
	@Value("${ver}")
	String versionUrl;

	@Value("${id}")
	String idUrl;
	
	/**
	 * @param e
	 * @param request
	 * @return response of MandatoryFieldRequiredException
	 */
	@ExceptionHandler(MandatoryFieldRequiredException.class)
	public ResponseEntity<MainResponseDTO<?>> mandatoryFieldrequired(final MandatoryFieldRequiredException e,WebRequest request){
		
		ExceptionJSONInfoDTO errorDetails=new ExceptionJSONInfoDTO(e.getErrorCode(),e.getErrorText());
		MainResponseDTO<?> errorRes=new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setErrors(errorList);
		errorRes.setId(e.getMainResponseDTO().getId());
		errorRes.setVersion(e.getMainResponseDTO().getVersion());
		errorRes.setResponsetime(DateUtils.formatDate(new Date(), utcDateTimepattern));
		
		return new ResponseEntity<>(errorRes,HttpStatus.OK);
	}

	/**
	 * @param e
	 * @param request
	 * @return response of FailedToTransliterateException
	 */
	@ExceptionHandler(JsonValidationException.class)
	public ResponseEntity<MainResponseDTO<?>> translitrationFailed(final JsonValidationException e){
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	}
	
	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for IllegalParamException
	 */
	@ExceptionHandler(IllegalParamException.class)
	public ResponseEntity<MainResponseDTO<?>> recException(final IllegalParamException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	}
	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for UnSupportedLanguageException
	 */
	@ExceptionHandler(UnSupportedLanguageException.class)
	public ResponseEntity<MainResponseDTO<?>> recException(final UnSupportedLanguageException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}
	
	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for InvalidRequestParameterException
	 */
	@ExceptionHandler(InvalidRequestParameterException.class)
	public ResponseEntity<MainResponseDTO<?>> recException(final InvalidRequestParameterException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	}
	
	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<MainResponseDTO<?>> DateFormatException(final InvalidFormatException e,WebRequest request){
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_CORE_REQ_003.getCode(),ErrorMessages.INVALID_REQUEST_DATETIME.getMessage());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
}
