package io.mosip.kernel.lkeymanager.exception;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.lkeymanager.constant.LicenseKeyManagerErrorCodes;

/**
 * Controller Advice class to handle {@link LicenseKeyServiceException},
 * {@link InvalidArgumentsException}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class LicenseKeyControllerAdvice {
	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Method to handle {@link InvalidArgumentsException}.
	 * 
	 * @param httpServletRequest
	 *            the request
	 * @param exception
	 *            the exception.
	 * @return {@link ErrorResponse}.
	 * @throws IOException
	 *             the IO exception
	 */
	@ExceptionHandler(InvalidArgumentsException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> validateInputArguments(HttpServletRequest httpServletRequest,
			final InvalidArgumentsException exception) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().addAll(exception.getList());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * Method to handle {@link LicenseKeyServiceException}.
	 * 
	 * @param httpServletRequest
	 *            the request
	 * @param exception
	 *            the exception.
	 * @return {@link ErrorResponse}.
	 * @throws IOException
	 *             the IO exception
	 */
	@ExceptionHandler(LicenseKeyServiceException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> handleServiceException(HttpServletRequest httpServletRequest,
			final LicenseKeyServiceException exception) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().addAll(exception.getList());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	/**
	 * Method to handle {@link HttpMessageNotReadableException}.
	 * 
	 * @param httpServletRequest
	 *            the request
	 * @param exception
	 *            the exception.
	 * 
	 * @return {@link ErrorResponse}.
	 * @throws IOException
	 *             the IO exception
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onHttpMessageNotReadable(
			final HttpServletRequest httpServletRequest, final HttpMessageNotReadableException exception)
			throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(LicenseKeyManagerErrorCodes.HTTP_MESSAGE_NOT_READABLE.getErrorCode(),
				exception.getMessage());
		responseWrapper.getErrors().add(error);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	/**
	 * Method to handle {@link RuntimeException}.
	 * 
	 * @param httpServletRequest
	 *            the request
	 * 
	 * @param exception
	 *            the exception.
	 * @return {@link ErrorResponse}.
	 * @throws IOException
	 *             the IO exception
	 */
	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultErrorHandler(HttpServletRequest httpServletRequest,
			Exception exception) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(LicenseKeyManagerErrorCodes.RUNTIME_EXCEPTION.getErrorCode(),
				exception.getMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
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
