/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjobservices.exception.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.mosip.preregistration.batchjobservices.exception.NoPreIdAvailableException;
import io.mosip.preregistration.batchjobservices.exception.NoValidPreIdFoundException;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.errorcodes.ErrorCodes;
import io.mosip.preregistration.core.errorcodes.ErrorMessages;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.core.util.GenericUtil;

/**
 * This class is defines the Exception handler for Document service
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 */
@RestControllerAdvice
public class BatchServiceExceptionHandler {

	

	@ExceptionHandler(NoPreIdAvailableException.class)
	public ResponseEntity<MainResponseDTO<?>> databaseerror(final NoPreIdAvailableException e) {
		return GenericUtil.errorResponse(e, e.getResponse());
	}

	@ExceptionHandler(TableNotAccessibleException.class)
	public ResponseEntity<MainResponseDTO<?>> databaseerror(final TableNotAccessibleException e) {
		return GenericUtil.errorResponse(e, e.getMainResposneDTO());
	}

	@ExceptionHandler(NoValidPreIdFoundException.class)
	public ResponseEntity<MainResponseDTO<?>> noValidPreIdFoundException(final NoValidPreIdFoundException e) {
		return GenericUtil.errorResponse(e, e.getResponse());

	}

	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<MainResponseDTO<?>> DateFormatException(final InvalidFormatException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_CORE_REQ_003.getCode(),
				ErrorMessages.INVALID_REQUEST_DATETIME.getMessage());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}


}
