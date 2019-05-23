package io.mosip.kernel.auditmanager.exception;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.auditmanager.constant.AuditErrorCode;
import io.mosip.kernel.auditmanager.constant.AuditErrorCodes;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.EmptyCheckUtils;

/**
 * Class for handling API exceptions
 * 
 * @author Dharmesh Khandelwal
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	private static final String WHITESPACE = " ";

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * This method handle MethodArgumentNotValidException.
	 * 
	 * @param httpServletRequest
	 *            the request
	 * @param e
	 *            the exception
	 * @return the response entity.
	 * @throws IOException
	 *             the IO Exception
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> methodArgumentNotValidException(
			HttpServletRequest httpServletRequest, final MethodArgumentNotValidException e) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		BindingResult bindingResult = e.getBindingResult();
		final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError(AuditErrorCodes.HANDLEREXCEPTION.getErrorCode(),
					Character.toUpperCase(x.getField().charAt(0)) + x.getField().substring(1) + WHITESPACE
							+ x.getDefaultMessage());
			responseWrapper.getErrors().add(error);
		});
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);

	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onHttpMessageNotReadable(HttpServletRequest httpServletRequest,
			final HttpMessageNotReadableException e) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(AuditErrorCode.INVALIDFORMAT.getErrorCode(), e.getMessage());
		responseWrapper.getErrors().add(error);
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultErrorHandler(HttpServletRequest httpServletRequest,
			Exception e) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(AuditErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(), e.getMessage());
		responseWrapper.getErrors().add(error);
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
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