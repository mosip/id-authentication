/**
 * 
 */
package io.mosip.kernel.auth.exception;

import java.io.IOException;
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

import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.constant.AuthErrorCode;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.EmptyCheckUtils;

/**
 * @author Ramadurai Pandian
 *
 */
@RestControllerAdvice
public class AuthManagerExceptionHandler {

	@Autowired
	private ObjectMapper objectMapper;
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> methodArgumentNotValidException(
			HttpServletRequest httpServletRequest, final MethodArgumentNotValidException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError(AuthErrorCode.INVALID_REQUEST.getErrorCode(),
					x.getField() + AuthConstant.WHITESPACE + x.getDefaultMessage());
			errorResponse.getErrors().add(error);
		});
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onHttpMessageNotReadable(HttpServletRequest httpServletRequest,
			final HttpMessageNotReadableException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(AuthErrorCode.INVALID_REQUEST.getErrorCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	

	private ResponseWrapper<ServiceError> setErrors(HttpServletRequest httpServletRequest) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = new ResponseWrapper<>();
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

	@ExceptionHandler(value = { AuthManagerException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> customErrorMessage(HttpServletRequest request,
			AuthManagerException e) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(request);
		ServiceError error = new ServiceError(e.getErrorCode(), e.getMessage());
		responseWrapper.getErrors().add(error);
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ExceptionHandler(value = { AuthManagerServiceException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> customErrorMessageList(HttpServletRequest request,
			AuthManagerServiceException e) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(request);
		responseWrapper.getErrors().addAll(e.getList());
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultErrorHandler(HttpServletRequest request, Exception e)
			throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(request);
		ServiceError error = new ServiceError("500", e.getMessage());
		responseWrapper.getErrors().add(error);
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
