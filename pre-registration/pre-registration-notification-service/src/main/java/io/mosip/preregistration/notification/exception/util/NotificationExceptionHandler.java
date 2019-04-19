/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.notification.exception.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.util.GenericUtil;
import io.mosip.preregistration.notification.exception.IllegalParamException;
import io.mosip.preregistration.notification.exception.MandatoryFieldException;

/**
 * Exception Handler for acknowledgement application.
 * 
 * @author Sanober Noor
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class NotificationExceptionHandler {

	protected boolean falseStatus = false;

	/**
	 * @param e
	 * @param request
	 * @return response of MandatoryFieldRequiredException
	 */
	@ExceptionHandler(MandatoryFieldException.class)
	public ResponseEntity<MainResponseDTO<?>> mandatoryFieldrequired(final MandatoryFieldException e,
			WebRequest request) {

		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = e.getMainResponseDTO();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());

		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	// /**
	// * @param e
	// * @param request
	// * @return response of FailedToTransliterateException
	// */
	// @ExceptionHandler(JsonValidationException.class)
	// public ResponseEntity<MainResponseDTO<?>> translitrationFailed(final
	// JsonValidationException e,WebRequest request){
	// ExceptionJSONInfoDTO errorDetails=new
	// ExceptionJSONInfoDTO(e.getErrorCode(),e.getErrorText());
	// MainResponseDTO<?> errorRes=new MainResponseDTO<>();
	// errorRes.setErr(errorDetails);
	// errorRes.setResTime(DateUtils.formatDate(new Date(), dateTimeFormat));
	// errorRes.setStatus(falseStatus);
	// return new ResponseEntity<>(errorRes,HttpStatus.OK);
	// }

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for IllegalParamException
	 */
	@ExceptionHandler(IllegalParamException.class)
	public ResponseEntity<MainResponseDTO<?>> recException(final IllegalParamException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes =e.getMainResponseDto();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

}
