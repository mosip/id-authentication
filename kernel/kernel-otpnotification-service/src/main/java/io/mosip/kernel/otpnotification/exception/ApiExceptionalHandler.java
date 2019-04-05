package io.mosip.kernel.otpnotification.exception;

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
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.otpnotification.constant.OtpNotificationErrorConstant;

/**
 * Central class for handling exceptions.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@RestControllerAdvice
public class ApiExceptionalHandler {
	@Autowired
	private ObjectMapper objectMapper;

	public static final String WHITESPACE = " ";

	/**
	 * This method handles HttpClientErrorException.
	 * 
	 * @param e the exception.
	 * @return the response entity.
	 * @throws IOException
	 */
	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> httpClientErrorException(
			final HttpServletRequest httpServletRequest, final HttpClientErrorException e) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(OtpNotificationErrorConstant.NOTIFIER_SERVER_ERROR.getErrorCode(),
				OtpNotificationErrorConstant.NOTIFIER_SERVER_ERROR.getErrorMessage());
		responseWrapper.getErrors().add(error);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	/**
	 * This method handles OtpInvalidArgumentException.
	 * 
	 * @param exception The exception.
	 * @return The response entity.
	 * @throws IOException
	 */
	@ExceptionHandler(OtpNotificationInvalidArgumentException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> otpValidationArgumentValidity(
			final HttpServletRequest httpServletRequest, final OtpNotificationInvalidArgumentException exception)
			throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().addAll(exception.getList());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * This method handles MethodArgumentNotValidException.
	 * 
	 * @param e The exception.
	 * @return The response entity.
	 * @throws IOException
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> methodArgumentNotValidException(
			final HttpServletRequest httpServletRequest, final MethodArgumentNotValidException e) throws IOException {

		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		BindingResult bindingResult = e.getBindingResult();
		final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError(
					OtpNotificationErrorConstant.NOTIFIER_INVALID_REQUEST_ERROR.getErrorCode(),
					x.getField() + ": " + x.getDefaultMessage());
			responseWrapper.getErrors().add(error);
		});
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	/**
	 * This method handles OtpNotifierServiceException.
	 * 
	 * @param e the exception.
	 * @return the response entity.
	 * @throws IOException
	 */
	@ExceptionHandler(OtpNotifierServiceException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlDataServiceException(
			final HttpServletRequest httpServletRequest, final OtpNotifierServiceException e) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(e.getErrorCode(), e.getErrorText());
		responseWrapper.getErrors().add(error);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	/**
	 * Method to handle {@link HttpMessageNotReadableException}.
	 * 
	 * @param exception the exception.
	 * @return {@link ErrorResponse}.
	 * @throws IOException
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onHttpMessageNotReadable(
			final HttpServletRequest httpServletRequest, final HttpMessageNotReadableException exception)
			throws IOException {

		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(OtpNotificationErrorConstant.HTTP_MESSAGE_NOT_READABLE.getErrorCode(),
				exception.getMessage());
		responseWrapper.getErrors().add(error);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	/**
	 * Method to handle {@link RuntimeException}.
	 * 
	 * @param request   the servlet request.
	 * @param exception the exception.
	 * @return {@link ErrorResponse}.
	 * @throws IOException
	 */
	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultErrorHandler(
			final HttpServletRequest httpServletRequest, Exception exception) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(OtpNotificationErrorConstant.RUNTIME_EXCEPTION.getErrorCode(),
				exception.getMessage());
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
