package io.mosip.kernel.applicanttype.exception;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.EmptyCheckUtils;

/**
 * Rest Controller Advice for Applicant type Data
 * 
 * @author Bal Vikash Sharma
 *
 * @since 1.0.0
 */
@RestControllerAdvice
public class ApiExceptionHandler {
	@Autowired
	private ObjectMapper objectMapper;

	@ExceptionHandler(ApplicantTypeServiceException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlDataServiceException(
			HttpServletRequest httpServletRequest, final ApplicantTypeServiceException e) throws IOException {
		return getErrorResponseEntity(httpServletRequest, e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlDataNotFoundException(
			HttpServletRequest httpServletRequest, final DataNotFoundException e) throws IOException {
		return getErrorResponseEntity(httpServletRequest, e, HttpStatus.OK);
	}

	@ExceptionHandler(RequestException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlRequestException(HttpServletRequest httpServletRequest,
			final RequestException e) throws IOException {
		return getErrorResponseEntity(httpServletRequest, e, HttpStatus.OK);
	}

	private ResponseEntity<ResponseWrapper<ServiceError>> getErrorResponseEntity(HttpServletRequest httpServletRequest,
			BaseUncheckedException e, HttpStatus httpStatus) throws IOException {
		ServiceError error = new ServiceError(e.getErrorCode(), e.getErrorText());
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, httpStatus);
	}

	private ResponseWrapper<ServiceError> setErrors(HttpServletRequest httpServletRequest) throws IOException {
		RequestWrapper<?> requestWrapper = null;
		ResponseWrapper<ServiceError> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponsetime(LocalDateTime.now(ZoneId.of("UTC")));
		String requestBody = null;
		if (httpServletRequest instanceof ContentCachingRequestWrapper) {
			requestBody = new String(((ContentCachingRequestWrapper) httpServletRequest).getContentAsByteArray());
		}
		if (EmptyCheckUtils.isNullEmpty(requestBody)) {
			objectMapper.registerModule(new JavaTimeModule());
			requestWrapper = objectMapper.readValue(requestBody, RequestWrapper.class);
			responseWrapper.setId(requestWrapper.getId());
			responseWrapper.setVersion(requestWrapper.getVersion());
		} else {
			responseWrapper.setId(null);
			responseWrapper.setVersion(null);
		}
		return responseWrapper;
	}

}