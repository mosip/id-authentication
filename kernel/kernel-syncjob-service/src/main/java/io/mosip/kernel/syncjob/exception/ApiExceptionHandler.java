package io.mosip.kernel.syncjob.exception;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.syncjob.constant.AdminServiceErrorCode;

/**
 * synch handler controller advice
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class ApiExceptionHandler {
	@Autowired
	ObjectMapper objectMapper;

	@ExceptionHandler(AdminServiceException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlDataServiceException(final AdminServiceException e,
			HttpServletRequest httpServletException) throws IOException {
		ExceptionUtils.logRootCause(e);
		return getServiceErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR, httpServletException);
	}

	@ExceptionHandler(DateParsingException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlDataServiceException(final DateParsingException e,
			final HttpServletRequest httpServletRequest) throws IOException {
		return getServiceErrorResponseEntity(e, HttpStatus.OK, httpServletRequest);
	}

	@ExceptionHandler(RequestException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlRequestException(final RequestException e,
			final HttpServletRequest httpServletRequest) throws IOException {
		ExceptionUtils.logRootCause(e);
		return getServiceErrorResponseEntity(e, HttpStatus.OK, httpServletRequest);
	}

	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlDataNotFoundException(final DataNotFoundException e,
			final HttpServletRequest httpServletRequest) throws IOException {
		return getServiceErrorResponseEntity(e, HttpStatus.OK, httpServletRequest);
	}

	/*
	 * @ExceptionHandler(HttpMessageNotReadableException.class) public
	 * ResponseEntity<ResponseWrapper<ServiceError>> onHttpMessageNotReadable( final
	 * HttpMessageNotReadableException e, final HttpServletRequest
	 * httpServletRequest) throws IOException { ResponseWrapper<ServiceError>
	 * responseWrapper = setErrors(httpServletRequest); ServiceError error = new
	 * ServiceError(MasterDataErrorCode.REQUEST_DATA_NOT_VALID.getErrorCode(),
	 * e.getMessage()); responseWrapper.getErrors().add(error); return new
	 * ResponseEntity<>(responseWrapper, HttpStatus.OK); }
	 */

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> onHttpMessageNotReadable(
			final HttpMessageNotReadableException e) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		ServiceError error = new ServiceError(AdminServiceErrorCode.REQUEST_DATA_NOT_VALID.getErrorCode(),
				e.getMessage());
		errorResponse.getErrors().add(error);
		errorResponse.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ErrorResponse<ServiceError>> defaultErrorHandler(HttpServletRequest request, Exception e) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		ServiceError error = new ServiceError(AdminServiceErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
				e.getMessage());
		errorResponse.getErrors().add(error);
		errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<ResponseWrapper<ServiceError>> getServiceErrorResponseEntity(BaseUncheckedException e,
			HttpStatus httpStatus, HttpServletRequest httpServletRequest) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(e.getErrorCode(), e.getErrorText());
		responseWrapper.getErrors().add(error);
		return new ResponseEntity<>(responseWrapper, httpStatus);
	}

	private ResponseWrapper<ServiceError> setErrors(HttpServletRequest httpServletRequest) throws IOException {
		RequestWrapper<?> requestWrapper = null;
		ResponseWrapper<ServiceError> responseWrapper = new ResponseWrapper<>();
		String requestBody = null;
		if (httpServletRequest instanceof ContentCachingRequestWrapper) {
			requestBody = new String(((ContentCachingRequestWrapper) httpServletRequest).getContentAsByteArray());
		}
		if (EmptyCheckUtils.isNullEmpty(requestBody)) {
			return responseWrapper;
		}
		objectMapper.registerModule(new JavaTimeModule());
		requestWrapper = objectMapper.readValue(requestBody, RequestWrapper.class);
		responseWrapper.setId(requestWrapper.getId());
		responseWrapper.setVersion(requestWrapper.getVersion());
		responseWrapper.setResponsetime(LocalDateTime.now(ZoneId.of("UTC")));
		return responseWrapper;
	}

}
