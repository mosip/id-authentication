package io.mosip.kernel.cryptomanager.exception;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.crypto.exception.NullDataException;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.cryptomanager.constant.CryptomanagerConstant;
import io.mosip.kernel.cryptomanager.constant.CryptomanagerErrorCode;

/**
 * Rest Controller Advice for Crypto-Manager-Service
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@RestControllerAdvice
public class CryptomanagerExceptionHandler {

	@Autowired
	private ObjectMapper objectMapper;

	@ExceptionHandler(NullDataException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> nullDataException(HttpServletRequest httpServletRequest,
			final NullDataException e) throws IOException {
		return new ResponseEntity<>(
				getErrorResponse(httpServletRequest, e.getErrorCode(), e.getErrorText(), HttpStatus.OK), HttpStatus.OK);
	}

	@ExceptionHandler(InvalidKeyException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> invalidKeyException(HttpServletRequest httpServletRequest,
			final InvalidKeyException e) throws IOException {
		return new ResponseEntity<>(
				getErrorResponse(httpServletRequest, e.getErrorCode(), e.getErrorText(), HttpStatus.OK), HttpStatus.OK);
	}

	@ExceptionHandler(NoSuchAlgorithmException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> noSuchAlgorithmException(HttpServletRequest httpServletRequest,
			final NoSuchAlgorithmException e) throws IOException {
		return new ResponseEntity<>(getErrorResponse(httpServletRequest, e.getErrorCode(), e.getErrorText(),
				HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> illegalArgumentException(HttpServletRequest httpServletRequest,
			final IllegalArgumentException e) throws IOException {
		return new ResponseEntity<>(
				getErrorResponse(httpServletRequest,
						CryptomanagerErrorCode.INVALID_DATA_WITHOUT_KEY_BREAKER.getErrorCode(),
						CryptomanagerErrorCode.INVALID_DATA_WITHOUT_KEY_BREAKER.getErrorMessage(), HttpStatus.OK),
				HttpStatus.OK);
	}

	@ExceptionHandler(SocketException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> socketException(HttpServletRequest httpServletRequest,
			final SocketException e) throws IOException {
		return new ResponseEntity<>(getErrorResponse(httpServletRequest,
				CryptomanagerErrorCode.CANNOT_CONNECT_TO_KEYMANAGER_SERVICE.getErrorCode(),
				CryptomanagerErrorCode.CANNOT_CONNECT_TO_KEYMANAGER_SERVICE.getErrorMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> invalidFormatException(HttpServletRequest httpServletRequest,
			final InvalidFormatException e) throws IOException {
		return new ResponseEntity<>(
				getErrorResponse(httpServletRequest, CryptomanagerErrorCode.DATE_TIME_PARSE_EXCEPTION.getErrorCode(),
						e.getMessage() + CryptomanagerConstant.WHITESPACE
								+ CryptomanagerErrorCode.DATE_TIME_PARSE_EXCEPTION.getErrorMessage(),
						HttpStatus.OK),
				HttpStatus.OK);
	}
	
	@ExceptionHandler(DateTimeParseException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> dateTimeParseException(HttpServletRequest httpServletRequest,
			final DateTimeParseException e) throws IOException {
		return new ResponseEntity<>(
				getErrorResponse(httpServletRequest, CryptomanagerErrorCode.DATE_TIME_PARSE_EXCEPTION.getErrorCode(),
						e.getMessage() + CryptomanagerConstant.WHITESPACE
								+ CryptomanagerErrorCode.DATE_TIME_PARSE_EXCEPTION.getErrorMessage(),
						HttpStatus.OK),
				HttpStatus.OK);
	}

	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> invalidDataException(HttpServletRequest httpServletRequest,
			final InvalidDataException e) throws IOException {
		return new ResponseEntity<>(
				getErrorResponse(httpServletRequest, e.getErrorCode(),
						e.getErrorText() + CryptomanagerErrorCode.INVALID_DATA.getErrorMessage(), HttpStatus.OK),
				HttpStatus.OK);
	}

	@ExceptionHandler(ConnectException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> connectException(HttpServletRequest httpServletRequest,
			final ConnectException e) throws IOException {
		return new ResponseEntity<>(getErrorResponse(httpServletRequest,
				CryptomanagerErrorCode.CANNOT_CONNECT_TO_KEYMANAGER_SERVICE.getErrorCode(),
				CryptomanagerErrorCode.CANNOT_CONNECT_TO_KEYMANAGER_SERVICE.getErrorMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> httpClientErrorException(HttpServletRequest httpServletRequest,
			final HttpClientErrorException exception) throws IOException {
		return new ResponseEntity<>(getErrorResponse(httpServletRequest,
				CryptomanagerErrorCode.KEYMANAGER_SERVICE_ERROR.getErrorCode(),
				CryptomanagerErrorCode.KEYMANAGER_SERVICE_ERROR.getErrorMessage() + CryptomanagerConstant.WHITESPACE
						+ exception.getResponseBodyAsString(),
				HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(HttpServerErrorException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> httpServerErrorException(HttpServletRequest httpServletRequest,
			final HttpServerErrorException exception) throws IOException {
		return new ResponseEntity<>(getErrorResponse(httpServletRequest,
				CryptomanagerErrorCode.KEYMANAGER_SERVICE_ERROR.getErrorCode(),
				CryptomanagerErrorCode.KEYMANAGER_SERVICE_ERROR.getErrorMessage() + CryptomanagerConstant.WHITESPACE
						+ exception.getResponseBodyAsString(),
				HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> methodArgumentNotValidException(
			HttpServletRequest httpServletRequest, final MethodArgumentNotValidException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError(CryptomanagerErrorCode.INVALID_REQUEST.getErrorCode(),
					x.getField() + CryptomanagerConstant.WHITESPACE + x.getDefaultMessage());
			errorResponse.getErrors().add(error);
		});
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	private ResponseWrapper<ServiceError> getErrorResponse(HttpServletRequest httpServletRequest, String errorCode,
			String errorMessage, HttpStatus httpStatus) throws IOException {
		ServiceError error = new ServiceError(errorCode, errorMessage);
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().add(error);
		return errorResponse;
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onHttpMessageNotReadable(HttpServletRequest httpServletRequest,
			final HttpMessageNotReadableException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(CryptomanagerErrorCode.INVALID_REQUEST.getErrorCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultErrorHandler(HttpServletRequest httpServletRequest,
			Exception exception) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(CryptomanagerErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
				exception.getMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ParseResponseException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> parseResponseException(HttpServletRequest httpServletRequest,
			final ParseResponseException e) throws IOException {
		return new ResponseEntity<>(getErrorResponse(httpServletRequest, e.getErrorCode(), e.getErrorText(),
				HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(CryptoManagerSerivceException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> cryptoManagerServieException(HttpServletRequest httpServletRequest,
			final CryptoManagerSerivceException e) throws IOException {
		return new ResponseEntity<>(getErrorResponse(httpServletRequest, e.getErrorCode(), e.getErrorText(),
				HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(KeymanagerServiceException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> keymanagerServiceException(
			HttpServletRequest httpServletRequest, final KeymanagerServiceException exception) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		errorResponse.getErrors().addAll(exception.getList());
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
		JsonNode reqNode = objectMapper.readTree(requestBody);
		responseWrapper.setId(reqNode.path("id").asText());
		responseWrapper.setVersion(reqNode.path("version").asText());
		return responseWrapper;
	}
}