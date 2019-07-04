package io.mosip.kernel.auth.adapter.exception;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.auth.adapter.constant.AuthAdapterErrorCode;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.EmptyCheckUtils;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthAdapterExceptionHandler {

	@Autowired
	private ObjectMapper objectMapper;

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onAuthenticationException(
			final HttpServletRequest httpServletRequest, final AuthenticationException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(AuthAdapterErrorCode.UNAUTHORIZED.getErrorCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onAccessDeniedException(
			final HttpServletRequest httpServletRequest, final AccessDeniedException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(AuthAdapterErrorCode.FORBIDDEN.getErrorCode(),
				AuthAdapterErrorCode.FORBIDDEN.getErrorMessage());
		errorResponse.getErrors().add(error);
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(AuthManagerException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> authManagerException(
			final HttpServletRequest httpServletRequest, final AuthManagerException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(AuthAdapterErrorCode.UNAUTHORIZED.getErrorCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(AuthNException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> authNException(final HttpServletRequest httpServletRequest,
			final AuthNException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().addAll(e.getList());
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(AuthZException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> authZException(final HttpServletRequest httpServletRequest,
			final AuthZException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().addAll(e.getList());
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
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

}
