package io.mosip.kernel.masterdata.exception;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
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

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.constant.RegistrationCenterUserMappingHistoryErrorCode;
import io.mosip.kernel.masterdata.constant.RequestErrorCode;

/**
 * Rest Controller Advice for Master Data
 * 
 * @author Dharmesh Khandelwal
 * @author Bal Vikash Sharma
 * @author Neha Sinha
 *
 * @since 1.0.0
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {

	@Autowired
	private ObjectMapper objectMapper;

	@ExceptionHandler(MasterDataServiceException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlDataServiceException(
			final HttpServletRequest httpServletRequest, final MasterDataServiceException e) throws IOException {
		return getErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR, httpServletRequest);
	}

	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlDataNotFoundException(
			final HttpServletRequest httpServletRequest, final DataNotFoundException e) throws IOException {
		return getErrorResponseEntity(e, HttpStatus.OK, httpServletRequest);
	}

	@ExceptionHandler(RequestException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlRequestException(
			final HttpServletRequest httpServletRequest, final RequestException e) throws IOException {
		return getErrorResponseEntity(e, HttpStatus.OK, httpServletRequest);
	}

	@ExceptionHandler(DateTimeParseException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> numberFormatException(
			final HttpServletRequest httpServletRequest, final DateTimeParseException e) throws IOException {
		ServiceError error = new ServiceError(
				RegistrationCenterUserMappingHistoryErrorCode.DATE_TIME_PARSE_EXCEPTION.getErrorCode(),
				e.getMessage() + MasterDataConstant.DATETIMEFORMAT);
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> methodArgumentNotValidException(
			final HttpServletRequest httpServletRequest, final MethodArgumentNotValidException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError(RequestErrorCode.REQUEST_DATA_NOT_VALID.getErrorCode(),
					x.getField() + ": " + x.getDefaultMessage());
			errorResponse.getErrors().add(error);
		});
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onHttpMessageNotReadable(
			final HttpServletRequest httpServletRequest, final HttpMessageNotReadableException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(RequestErrorCode.REQUEST_DATA_NOT_VALID.getErrorCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultErrorHandler(
			final HttpServletRequest httpServletRequest, Exception e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(RequestErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<ResponseWrapper<ServiceError>> getErrorResponseEntity(BaseUncheckedException e,
			HttpStatus httpStatus, HttpServletRequest httpServletRequest) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(e.getErrorCode(), e.getErrorText());
		responseWrapper.getErrors().add(error);
		return new ResponseEntity<>(responseWrapper, httpStatus);
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

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> validationException(HttpServletRequest httpServletRequest,
			final ValidationException exception) throws IOException {
		//ExceptionUtils.logRootCause(exception);
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().addAll(exception.getErrors());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
