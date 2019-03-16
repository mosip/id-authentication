/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjobservices.exceptions.util;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.batchjobservices.exceptions.NoPreIdAvailableException;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;

/**
 * This class is defines the Exception handler for Document service
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 */
@RestControllerAdvice
public class BatchServiceExceptionHandler {
	
	private boolean responseStatus = false;
	
	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	@ExceptionHandler(NoPreIdAvailableException.class)
	public ResponseEntity<MainListResponseDTO<?>> databaseerror(final NoPreIdAvailableException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(responseStatus);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	@ExceptionHandler(TableNotAccessibleException.class)
	public ResponseEntity<MainListResponseDTO<?>> databaseerror(final TableNotAccessibleException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(responseStatus);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	
	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
	}

}
