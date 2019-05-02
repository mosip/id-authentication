/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjobservices.exception.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.batchjobservices.exception.NoPreIdAvailableException;
import io.mosip.preregistration.batchjobservices.exception.NoValidPreIdFoundException;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;

/**
 * This class is defines the Exception handler for Document service
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 */
@RestControllerAdvice
public class BatchServiceExceptionHandler {
	
	
	@Value("${mosip.utc-datetime-pattern}")
	private String utcDateTimePattern;
	
	@Value("${ver}")
	String versionUrl;

	@Value("${id}")
	String idUrl;
	
	@ExceptionHandler(NoPreIdAvailableException.class)
	public ResponseEntity<MainResponseDTO<?>> databaseerror(final NoPreIdAvailableException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails=new ExceptionJSONInfoDTO(e.getErrorCode(),e.getErrorText());
		MainResponseDTO<?> errorRes=new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setErrors(errorList);
		errorRes.setId(idUrl);
		errorRes.setVersion(versionUrl);
		errorRes.setResponsetime(DateUtils.formatDate(new Date(), utcDateTimePattern));
		
		return new ResponseEntity<>(errorRes,HttpStatus.OK);
	}
	@ExceptionHandler(TableNotAccessibleException.class)
	public ResponseEntity<MainResponseDTO<?>> databaseerror(final TableNotAccessibleException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails=new ExceptionJSONInfoDTO(e.getErrorCode(),e.getErrorText());
		MainResponseDTO<?> errorRes=new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setErrors(errorList);
		errorRes.setId(idUrl);
		errorRes.setVersion(versionUrl);
		errorRes.setResponsetime(DateUtils.formatDate(new Date(), utcDateTimePattern));
		
		return new ResponseEntity<>(errorRes,HttpStatus.OK);
	}
	@ExceptionHandler(NoValidPreIdFoundException.class)
	public ResponseEntity<MainResponseDTO<?>> noValidPreIdFoundException(final NoValidPreIdFoundException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails=new ExceptionJSONInfoDTO(e.getErrorCode(),e.getErrorText());
		MainResponseDTO<?> errorRes=new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setErrors(errorList);
		errorRes.setId(idUrl);
		errorRes.setVersion(versionUrl);
		errorRes.setResponsetime(DateUtils.formatDate(new Date(), utcDateTimePattern));
		
		return new ResponseEntity<>(errorRes,HttpStatus.OK);
	}
	
	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), utcDateTimePattern);
	}

}
