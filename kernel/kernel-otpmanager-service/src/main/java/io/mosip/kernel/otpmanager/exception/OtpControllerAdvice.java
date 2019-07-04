package io.mosip.kernel.otpmanager.exception;

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

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.otpmanager.constant.OtpErrorConstants;

/**
 * Central class for handling exceptions.
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
@RestControllerAdvice
public class OtpControllerAdvice {

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * This method handles MethodArgumentNotValidException.
	 * 
	 * @param httpServletRequest
	 *            the request
	 * @param e
	 *            The exception
	 * @return The response entity.
	 * @throws IOException
	 *             the IO Exception
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> otpGeneratorValidity(
			final HttpServletRequest httpServletRequest, final MethodArgumentNotValidException e) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		BindingResult bindingResult = e.getBindingResult();
		final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError(OtpErrorConstants.OTP_GEN_ILLEGAL_KEY_INPUT.getErrorCode(),
					x.getField() + ": " + x.getDefaultMessage());
			responseWrapper.getErrors().add(error);
		});
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	/**
	 * This method handles OtpInvalidArgumentException.
	 * 
	 * @param httpServletRequest
	 *            the request
	 * 
	 * @param exception
	 *            The exception.
	 * @return The response entity.
	 * @throws IOException
	 *             the IO Exception
	 */
	@ExceptionHandler(OtpInvalidArgumentException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> otpValidationArgumentValidity(
			final HttpServletRequest httpServletRequest, final OtpInvalidArgumentException exception)
			throws IOException {
		ExceptionUtils.logRootCause(exception);
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().addAll(exception.getList());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * This method handles RequiredKeyNotFoundException.
	 * 
	 * @param httpServletRequest
	 *            the request
	 * 
	 * @param exception
	 *            The exception.
	 * @return The response entity.
	 * @throws IOException
	 *             the IO Exception
	 */
	@ExceptionHandler(RequiredKeyNotFoundException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> otpValidationKeyNullValidity(
			final HttpServletRequest httpServletRequest, final RequiredKeyNotFoundException exception)
			throws IOException {
		ExceptionUtils.logRootCause(exception);
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().addAll(exception.getList());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onHttpMessageNotReadable(
			final HttpMessageNotReadableException e) {
		ResponseWrapper<ServiceError> responseWrapper = new ResponseWrapper<>();
		ServiceError error = new ServiceError(OtpErrorConstants.HTTP_MESSAGE_NOT_READABLE.getErrorCode(),
				e.getMessage());
		responseWrapper.getErrors().add(error);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultErrorHandler(HttpServletRequest httpServletRequest,
			Exception e) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(OtpErrorConstants.INTERNAL_SERVER_ERROR.getErrorCode(), e.getMessage());
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
