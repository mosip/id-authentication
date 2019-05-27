/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjobservices.exception.util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.preregistration.batchjobservices.exception.NoPreIdAvailableException;
import io.mosip.preregistration.batchjobservices.exception.NoValidPreIdFoundException;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
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

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> methodArgumentNotValidException(
			final HttpServletRequest httpServletRequest, final MethodArgumentNotValidException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError(io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_015.getCode(),
					x.getField() + ": " + x.getDefaultMessage());
			errorResponse.getErrors().add(error);
		});
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onHttpMessageNotReadable(
			final HttpServletRequest httpServletRequest, final HttpMessageNotReadableException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_015.getCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}
	
	


	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultErrorHandler(
			final HttpServletRequest httpServletRequest, Exception e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_016.getCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Autowired
	private ObjectMapper objectMapper;
	
	private ResponseWrapper<ServiceError> setErrors(HttpServletRequest httpServletRequest) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponsetime(LocalDateTime.now(ZoneId.of("UTC")));
		String requestBody = null;
		if (httpServletRequest instanceof ContentCachingRequestWrapper) {
			requestBody = new String(((ContentCachingRequestWrapper) httpServletRequest).getContentAsByteArray());
		}
		if (EmptyCheckUtils.isNullEmpty(requestBody)) {
			return responseWrapper;
		}
		objectMapper.registerModule(new JavaTimeModule());
		JsonNode reqNode = objectMapper.readTree(requestBody);
		responseWrapper.setId(reqNode.path("id").asText());
		responseWrapper.setVersion(reqNode.path("version").asText());
		return responseWrapper;
	}

}
