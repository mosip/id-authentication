package io.mosip.kernel.tokenidgenerator.exception;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.idgenerator.exception.TokenIdGeneratorException;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.tokenidgenerator.constant.TokenIDGeneratorErrorCode;


@RestControllerAdvice
public class ApiExceptionHandler {
	/**
	 * Reference to {@link ObjectMapper}.
	 */
	@Autowired
	private ObjectMapper objectMapper;
	
	@ExceptionHandler(TokenIdGeneratorException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> emptyLengthException(
			final HttpServletRequest httpServletRequest, final TokenIdGeneratorException e) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(e.getErrorCode(), e.getErrorText());
		responseWrapper.getErrors().add(error);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);

	}
	/**
	 * Method to handle {@link RuntimeException}.
	 * 
	 * @param request
	 *            the servlet request.
	 * @param exception
	 *            the exception.
	 * @return {@link ErrorResponse}.
	 * @throws IOException
	 */
	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultErrorHandler(
			final HttpServletRequest httpServletRequest, Exception exception) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(TokenIDGeneratorErrorCode.RUNTIME_EXCEPTION.getErrorCode(),
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
