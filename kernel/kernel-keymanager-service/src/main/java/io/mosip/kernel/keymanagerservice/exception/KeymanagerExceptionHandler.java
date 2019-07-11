/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.keymanagerservice.exception;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.crypto.exception.NullDataException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.keymanagerservice.constant.KeymanagerConstant;
import io.mosip.kernel.keymanagerservice.constant.KeymanagerErrorConstant;

/**
 * Rest Controller Advice for Keymanager Service
 * 
 * @author Dharmesh Khandelwal
 *
 * @since 1.0.0
 */
@RestControllerAdvice
public class KeymanagerExceptionHandler {

	@Autowired
	private ObjectMapper objectMapper;

	@ExceptionHandler(NullDataException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> nullDataException(HttpServletRequest httpServletRequest,
			final NullDataException e) throws IOException {
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(
				getErrorResponse(httpServletRequest, e.getErrorCode(), e.getErrorText(), HttpStatus.OK), HttpStatus.OK);
	}

	@ExceptionHandler(InvalidKeyException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> invalidKeyException(HttpServletRequest httpServletRequest,
			final InvalidKeyException e) throws IOException {
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(
				getErrorResponse(httpServletRequest, e.getErrorCode(), e.getErrorText(), HttpStatus.OK), HttpStatus.OK);
	}

	@ExceptionHandler(NoSuchAlgorithmException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> noSuchAlgorithmException(HttpServletRequest httpServletRequest,
			final NoSuchAlgorithmException e) throws IOException {
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(getErrorResponse(httpServletRequest, e.getErrorCode(), e.getErrorText(),
				HttpStatus.OK), HttpStatus.OK);
	}

	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> invalidFormatException(HttpServletRequest httpServletRequest,
			final InvalidFormatException e) throws IOException {
		return new ResponseEntity<>(
				getErrorResponse(httpServletRequest, KeymanagerErrorConstant.DATE_TIME_PARSE_EXCEPTION.getErrorCode(),
						e.getMessage() + KeymanagerConstant.WHITESPACE
								+ KeymanagerErrorConstant.DATE_TIME_PARSE_EXCEPTION.getErrorMessage(),
						HttpStatus.OK),
				HttpStatus.OK);
	}

	@ExceptionHandler(DateTimeParseException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> dateTimeParseException(HttpServletRequest httpServletRequest,
			final DateTimeParseException e) throws IOException {
		return new ResponseEntity<>(
				getErrorResponse(httpServletRequest, KeymanagerErrorConstant.DATE_TIME_PARSE_EXCEPTION.getErrorCode(),
						e.getMessage() + KeymanagerConstant.WHITESPACE
								+ KeymanagerErrorConstant.DATE_TIME_PARSE_EXCEPTION.getErrorMessage(),
						HttpStatus.OK),
				HttpStatus.OK);
	}

	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> invalidDataException(HttpServletRequest httpServletRequest,
			final InvalidDataException e) throws IOException {
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(
				getErrorResponse(httpServletRequest, e.getErrorCode(), e.getErrorText(), HttpStatus.OK), HttpStatus.OK);
	}

	@ExceptionHandler(NoUniqueAliasException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> noUniqueAliasException(HttpServletRequest httpServletRequest,
			final NoUniqueAliasException e) throws IOException {
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(
				getErrorResponse(httpServletRequest, e.getErrorCode(), e.getErrorText(), HttpStatus.OK), HttpStatus.OK);
	}

	@ExceptionHandler(CryptoException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> cryptoException(HttpServletRequest httpServletRequest,
			final CryptoException e) throws IOException {
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(getErrorResponse(httpServletRequest, e.getErrorCode(), e.getErrorText(),
				HttpStatus.OK), HttpStatus.OK);
	}

	@ExceptionHandler(InvalidApplicationIdException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> invalidApplicationIdException(
			HttpServletRequest httpServletRequest, final InvalidApplicationIdException e) throws IOException {
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(
				getErrorResponse(httpServletRequest, e.getErrorCode(), e.getErrorText(), HttpStatus.OK), HttpStatus.OK);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> methodArgumentNotValidException(
			HttpServletRequest httpServletRequest, final MethodArgumentNotValidException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError(KeymanagerErrorConstant.INVALID_REQUEST.getErrorCode(),
					x.getField() + KeymanagerConstant.WHITESPACE + x.getDefaultMessage());
			errorResponse.getErrors().add(error);
		});
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onHttpMessageNotReadable(HttpServletRequest httpServletRequest,
			final HttpMessageNotReadableException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(KeymanagerErrorConstant.INVALID_REQUEST.getErrorCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onMissingServletRequestParameterException(
			HttpServletRequest httpServletRequest, final MissingServletRequestParameterException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(KeymanagerErrorConstant.INVALID_REQUEST.getErrorCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultErrorHandler(HttpServletRequest httpServletRequest,
			Exception e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(KeymanagerErrorConstant.INTERNAL_SERVER_ERROR.getErrorCode(),
				e.getMessage());
		errorResponse.getErrors().add(error);
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseWrapper<ServiceError> getErrorResponse(HttpServletRequest httpServletRequest, String errorCode,
			String errorMessage, HttpStatus httpStatus) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(errorCode, errorMessage);
		errorResponse.getErrors().add(error);
		return errorResponse;
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
