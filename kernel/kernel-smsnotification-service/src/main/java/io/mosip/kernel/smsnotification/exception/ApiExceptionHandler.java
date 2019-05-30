package io.mosip.kernel.smsnotification.exception;

import java.io.IOException;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.smsnotification.constant.SmsExceptionConstant;

/**
 * Central class for handling exceptions.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	/**
	 * Reference to ObjectMapper.
	 */
	@Autowired
	private ObjectMapper objectMapper;

	private static final String WHITESPACE = " ";

	/**
	 * This method handles MethodArgumentNotValidException type of exceptions.
	 * 
	 * @param httpServletRequest
	 *            the request
	 * @param exception
	 *            The exception
	 * @return The response entity.
	 * @throws IOException
	 *             the IOException
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> smsInvalidInputsFound(
			final HttpServletRequest httpServletRequest, final MethodArgumentNotValidException exception)
			throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		BindingResult bindingResult = exception.getBindingResult();
		final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError(SmsExceptionConstant.SMS_ILLEGAL_INPUT.getErrorCode(),
					x.getField() + WHITESPACE + x.getDefaultMessage());
			responseWrapper.getErrors().add(error);
		});
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	/**
	 * This method handles MosipInvalidNumberException type of exceptions.
	 * 
	 * @param httpServletRequest
	 *            the request
	 * @param e
	 *            The exception
	 * @return The response entity.
	 * @throws IOException
	 *             the IOException
	 */
	@ExceptionHandler(InvalidNumberException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> smsNotificationInvalidNumber(
			final HttpServletRequest httpServletRequest, final InvalidNumberException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(SmsExceptionConstant.SMS_INVALID_CONTACT_NUMBER.getErrorCode(),
				SmsExceptionConstant.SMS_INVALID_CONTACT_NUMBER.getErrorMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);

	}

	/**
	 * This method handle HttpMessageNotReadableException type of exception.
	 * 
	 * @param httpServletRequest
	 *            the request.
	 * @param e
	 *            the exception.
	 * @return the response entity.
	 * @throws IOException
	 *             IOException.
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onHttpMessageNotReadable(
			final HttpServletRequest httpServletRequest, final HttpMessageNotReadableException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(SmsExceptionConstant.SMS_ILLEGAL_INPUT.getErrorCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * This method handle HttpClientErrorException type of exception.
	 * 
	 * @param httpServletRequest
	 *            the request.
	 * @param e
	 *            the exception.
	 * @return the response entity.
	 * @throws IOException
	 *             IOException.
	 */
	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> smsVendorServiceException(
			final HttpServletRequest httpServletRequest, final HttpClientErrorException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);

		JsonNode responseTree = objectMapper.readTree(e.getResponseBodyAsString());
		String errorMessage = null;
		if (responseTree != null) {
			responseTree = responseTree.get("requestError").get("serviceException");
			errorMessage = responseTree.get("text").asText();
		} else {
			errorMessage = e.getMessage();
		}
		ServiceError error = new ServiceError(SmsExceptionConstant.SMS_INVALID_CREDENTIAL.getErrorCode(), errorMessage);
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(HttpServerErrorException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> httpServerErrorException(HttpServletRequest httpServletRequest,
			final HttpServerErrorException exception) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		JsonNode responseTree = objectMapper.readTree(exception.getResponseBodyAsString());
		String errorMessage = null;
		if (responseTree != null) {
			responseTree = responseTree.get("requestError").get("serviceException");
			errorMessage = responseTree.get("text").asText();
		} else {
			errorMessage = exception.getMessage();
		}
		ServiceError error = new ServiceError("KER-NOS-004", errorMessage);
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultErrorHandler(
			final HttpServletRequest httpServletRequest, Exception e) throws IOException {

		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(SmsExceptionConstant.INTERNAL_SERVER_ERROR.getErrorCode(),
				e.getMessage());
		responseWrapper.getErrors().add(error);
		return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
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

}
