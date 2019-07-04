package io.mosip.kernel.syncdata.exception;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
import io.mosip.kernel.core.signatureutil.exception.SignatureUtilClientException;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.syncdata.constant.MasterDataErrorCode;
import io.mosip.kernel.syncdata.constant.SyncDataConstant;


/**
 * synch handler controller advice
 * 
 * @author Abhishek Kumar
 * @author Neha Sinha
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class SyncHandlerControllerAdvice {

	@Autowired
	private ObjectMapper objectMapper;

	@ExceptionHandler(SyncDataServiceException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlDataServiceException(final SyncDataServiceException e,
			final HttpServletRequest httpServletRequest) throws IOException {
		ExceptionUtils.logRootCause(e);
		return getServiceErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR, httpServletRequest);
	}

	@ExceptionHandler(DateParsingException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlDataServiceException(final DateParsingException e,
			final HttpServletRequest httpServletRequest) throws IOException {
		return getServiceErrorResponseEntity(e, HttpStatus.OK, httpServletRequest);
	}

	@ExceptionHandler(RequestException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlRequestException(final RequestException e,
			final HttpServletRequest httpServletRequest) throws IOException {
		return getServiceErrorResponseEntity(e, HttpStatus.OK, httpServletRequest);
	}

	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlDataNotFoundException(final DataNotFoundException e,
			final HttpServletRequest httpServletRequest) throws IOException {
		ExceptionUtils.logRootCause(e);
		return getServiceErrorResponseEntity(e, HttpStatus.OK, httpServletRequest);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onHttpMessageNotReadable(
			final HttpMessageNotReadableException e, final HttpServletRequest httpServletRequest) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(MasterDataErrorCode.REQUEST_DATA_NOT_VALID.getErrorCode(),
				e.getMessage());
		responseWrapper.getErrors().add(error);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultServiceErrorHandler(HttpServletRequest request,
			Exception e) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(request);
		ServiceError error = new ServiceError(MasterDataErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(), e.getMessage());
		responseWrapper.getErrors().add(error);
		ExceptionUtils.logRootCause(e);
		return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(SyncServiceException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> syncServiceException(HttpServletRequest httpServletRequest,
			final SyncServiceException exception) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().addAll(exception.getList());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(SyncInvalidArgumentException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> syncInvalidArgumentException(
			HttpServletRequest httpServletRequest, final SyncInvalidArgumentException exception) throws IOException {
		ExceptionUtils.logRootCause(exception);
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().addAll(exception.getList());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(CryptoManagerServiceException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> cryptoManagerServiceException(
			HttpServletRequest httpServletRequest, final CryptoManagerServiceException exception) throws IOException {
		ExceptionUtils.logRootCause(exception);
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().addAll(exception.getList());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(SignatureUtilClientException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> signatureUtilClientException(
			HttpServletRequest httpServletRequest, final SignatureUtilClientException exception) throws IOException {
		ExceptionUtils.logRootCause(exception);
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().addAll(exception.getList());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> methodArgumentNotValidException(
			HttpServletRequest httpServletRequest, final MethodArgumentNotValidException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError(MasterDataErrorCode.INVALID_INPUT_REQUEST.getErrorCode(),
					x.getField() + SyncDataConstant.WHITESPACE+ x.getDefaultMessage());
			errorResponse.getErrors().add(error);
		});
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);	
	}
	
	private ResponseEntity<ResponseWrapper<ServiceError>> getServiceErrorResponseEntity(BaseUncheckedException e,
			HttpStatus httpStatus, HttpServletRequest httpServletRequest) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(e.getErrorCode(), e.getErrorText());
		responseWrapper.getErrors().add(error);
		return new ResponseEntity<>(responseWrapper, httpStatus);
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
