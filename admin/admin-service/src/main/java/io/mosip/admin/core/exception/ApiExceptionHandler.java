package io.mosip.admin.core.exception;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.admin.accountmgmt.constant.AccountManagementErrorCode;
import io.mosip.admin.accountmgmt.exception.AccountManagementServiceException;
import io.mosip.admin.accountmgmt.exception.AccountServiceException;
import io.mosip.admin.accountmgmt.exception.RequestException;
import io.mosip.admin.constant.AdminConstant;
import io.mosip.admin.constant.AdminErrorCode;
import io.mosip.admin.usermgmt.exception.UsermanagementServiceException;
import io.mosip.admin.usermgmt.exception.UsermanagementServiceResponseException;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.EmptyCheckUtils;

/**
 * Rest Controller Advice for Admin Service Controllers.
 * 
 * @author Urvil Joshi
 * @author Bal Vikash Sharma
 *
 * @since 1.0.0
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	@Autowired
	private ObjectMapper objectMapper;
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> methodArgumentNotValidException(
			HttpServletRequest httpServletRequest, final MethodArgumentNotValidException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError(AdminErrorCode.INVALID_REQUEST.getErrorCode(),
					x.getField() + AdminConstant.WHITESPACE + x.getDefaultMessage());
			errorResponse.getErrors().add(error);
		});
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}
	

	@ExceptionHandler(RequestException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlRequestException(
			final HttpServletRequest httpServletRequest, final RequestException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().add(new ServiceError());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(AccountServiceException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> accountServiceException(HttpServletRequest httpServletRequest,
			final AccountServiceException exception) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().addAll(exception.getList());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(AccountManagementServiceException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> accountManagementServiceException(
			HttpServletRequest httpServletRequest, final AccountManagementServiceException exception)
			throws IOException {

		return getServiceErrorResponseEntity(exception, HttpStatus.INTERNAL_SERVER_ERROR, httpServletRequest);
	}
	
	@ExceptionHandler(UsermanagementServiceException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> usermanagementServiceException(
			HttpServletRequest httpServletRequest, final UsermanagementServiceException exception)
			throws IOException {
		return getServiceErrorResponseEntity(exception, HttpStatus.INTERNAL_SERVER_ERROR, httpServletRequest);
	}
	
	
	@ExceptionHandler(UsermanagementServiceResponseException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> usermanagementServiceResponseException(
			HttpServletRequest httpServletRequest, final UsermanagementServiceResponseException exception)
			throws IOException {

		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().addAll(exception.getList());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}
	
	
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onHttpMessageNotReadable(HttpServletRequest httpServletRequest,
			final HttpMessageNotReadableException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(AdminErrorCode.INVALID_REQUEST.getErrorCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}


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


	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultServiceErrorHandler(HttpServletRequest request,
			Exception e) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(request);
		ServiceError error = new ServiceError(AccountManagementErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
				e.getMessage());
		responseWrapper.getErrors().add(error);
		return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<ResponseWrapper<ServiceError>> getServiceErrorResponseEntity(BaseUncheckedException e,
			HttpStatus httpStatus, HttpServletRequest httpServletRequest) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(e.getErrorCode(), e.getErrorText());
		responseWrapper.getErrors().add(error);
		return new ResponseEntity<>(responseWrapper, httpStatus);
	}

}