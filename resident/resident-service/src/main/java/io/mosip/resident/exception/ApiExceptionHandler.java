package io.mosip.resident.exception;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import io.mosip.kernel.core.exception.BaseCheckedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.EmptyCheckUtils;

@RestControllerAdvice
public class ApiExceptionHandler {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	Environment env;

	private static final String CHECK_STATUS = "resident.checkstatus.id";
	private static final String EUIN = "resident.euin.id";
	private static final String PRINT_UIN = "resident.printuin.id";
	private static final String UIN = "resident.uin.id";
	private static final String RID = "resident.rid.id";
	private static final String UPDATE_UIN = "resident.updateuin.id";
	private static final String VID = "resident.vid.id";
	private static final String AUTH_LOCK = "resident.authlock.id";
	private static final String AUTH_UNLOCK = "resident.authunlock.id";
	private static final String AUTH_HISTORY = "resident.authhistory.id";
	private static final String VERSION = "1.0";

	@ExceptionHandler(ResidentServiceException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlDataServiceException(
			HttpServletRequest httpServletRequest, final ResidentServiceException e) throws IOException {
		ExceptionUtils.logRootCause(e);
		return getErrorResponseEntity(httpServletRequest, e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlDataNotFoundException(
			HttpServletRequest httpServletRequest, final DataNotFoundException e) throws IOException {
		ExceptionUtils.logRootCause(e);
		return getErrorResponseEntity(httpServletRequest, e, HttpStatus.OK);
	}

	@ExceptionHandler(RequestException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlRequestException(HttpServletRequest httpServletRequest,
			final RequestException e) throws IOException {
		ExceptionUtils.logRootCause(e);
		return getErrorResponseEntity(httpServletRequest, e, HttpStatus.OK);
	}

	private ResponseEntity<ResponseWrapper<ServiceError>> getErrorResponseEntity(HttpServletRequest httpServletRequest,
			BaseUncheckedException e, HttpStatus httpStatus) throws IOException {
		ServiceError error = new ServiceError(e.getErrorCode(), e.getErrorText());
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, httpStatus);
	}

	private ResponseEntity<ResponseWrapper<ServiceError>> getCheckedErrorEntity(HttpServletRequest httpServletRequest,
																				BaseCheckedException e, HttpStatus httpStatus) throws IOException {
		ServiceError error = new ServiceError(e.getErrorCode(), e.getErrorText());
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, httpStatus);
	}

	@ExceptionHandler(InvalidInputException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlRequestException(HttpServletRequest httpServletRequest,
			final InvalidInputException e) throws IOException {
		ExceptionUtils.logRootCause(e);
		return getErrorResponseEntity(httpServletRequest, e, HttpStatus.OK);
	}

	@ExceptionHandler(IdRepoAppException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlRequestException(HttpServletRequest httpServletRequest,
			final IdRepoAppException e) throws IOException {
		ExceptionUtils.logRootCause(e);
		return getErrorResponseEntity(httpServletRequest, e, HttpStatus.OK);
	}

	@ExceptionHandler(OtpValidationFailedException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlRequestException(HttpServletRequest httpServletRequest,
			final OtpValidationFailedException e) throws IOException {
		ExceptionUtils.logRootCause(e);
		return getCheckedErrorEntity(httpServletRequest, e, HttpStatus.OK);
	}

	@ExceptionHandler(TokenGenerationFailedException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> controlRequestException(HttpServletRequest httpServletRequest,
			final TokenGenerationFailedException e) throws IOException {
		ExceptionUtils.logRootCause(e);
		return getErrorResponseEntity(httpServletRequest, e, HttpStatus.OK);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> methodArgumentNotValidException(
			final HttpServletRequest httpServletRequest, final MethodArgumentNotValidException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError("RES-500", x.getField() + ": " + x.getDefaultMessage());
			errorResponse.getErrors().add(error);
		});
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onHttpMessageNotReadable(
			final HttpServletRequest httpServletRequest, final HttpMessageNotReadableException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError("RES-500", e.getMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultErrorHandler(HttpServletRequest httpServletRequest,
			Exception exception) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError("RES-500", exception.getMessage());
		errorResponse.getErrors().add(error);
		ExceptionUtils.logRootCause(exception);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
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
		responseWrapper.setId(setId(httpServletRequest.getRequestURI()));
		responseWrapper.setVersion(VERSION);
		return responseWrapper;
	}

	private String setId(String requestURI) {
		if (requestURI.contains("/check-status")) {
			return env.getProperty(CHECK_STATUS);
		}
		if (requestURI.contains("/euin")) {
			return env.getProperty(EUIN);
		}
		if (requestURI.contains("/print-uin")) {
			return env.getProperty(PRINT_UIN);
		}
		if (requestURI.contains("/uin")) {
			return env.getProperty(UIN);
		}
		if (requestURI.contains("/rid")) {
			return env.getProperty(RID);
		}
		if (requestURI.contains("/update-uin")) {
			return env.getProperty(UPDATE_UIN);
		}
		if (requestURI.contains("/vid")) {
			return env.getProperty(VID);
		}
		if (requestURI.contains("/auth-lock")) {
			return env.getProperty(AUTH_LOCK);
		}
		if (requestURI.contains("/auth-unlock")) {
			return env.getProperty(AUTH_UNLOCK);
		}
		if (requestURI.contains("/auth-history")) {
			return env.getProperty(AUTH_HISTORY);
		}
		return null;
	}

}