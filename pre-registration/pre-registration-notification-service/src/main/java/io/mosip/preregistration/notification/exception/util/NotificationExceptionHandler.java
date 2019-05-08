
package io.mosip.preregistration.notification.exception.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.errorcodes.ErrorCodes;
import io.mosip.preregistration.core.errorcodes.ErrorMessages;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.GenericUtil;
import io.mosip.preregistration.notification.exception.IllegalParamException;
import io.mosip.preregistration.notification.exception.MandatoryFieldException;
import io.mosip.preregistration.notification.exception.NotificationSeriveException;

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
	public ResponseEntity<MainResponseDTO<?>> mandatoryFieldrequired(final MandatoryFieldException e) {

		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	 /**
	 * @param e
	 * @param request
	 * @return response of InvalidRequestParameterException
	 */
	 @ExceptionHandler(InvalidRequestParameterException.class)
	 public ResponseEntity<MainResponseDTO<?>> notificationFailed(final
			 InvalidRequestParameterException e){
		 return GenericUtil.errorResponse(e, e.getMainResponseDto());
	 }

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for NotificationSeriveException
	 */
	
	
	@ExceptionHandler(NotificationSeriveException.class)
	public ResponseEntity<MainResponseDTO<?>> authServiceException(final NotificationSeriveException e,WebRequest request){
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		e.getValidationErrorList().stream().forEach(serviceError->errorList.add(new ExceptionJSONInfoDTO(serviceError.getErrorCode(),serviceError.getMessage())));
		MainResponseDTO<?> errorRes = e.getMainResposneDTO();
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
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
