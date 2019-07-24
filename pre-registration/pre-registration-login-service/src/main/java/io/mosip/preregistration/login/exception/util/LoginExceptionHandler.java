package io.mosip.preregistration.login.exception.util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.exception.ServiceError;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.core.errorcodes.ErrorCodes;
import io.mosip.preregistration.core.errorcodes.ErrorMessages;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.GenericUtil;
import io.mosip.preregistration.login.exception.ConfigFileNotFoundException;
import io.mosip.preregistration.login.exception.InvalidOtpOrUseridException;
import io.mosip.preregistration.login.exception.InvalidateTokenException;
import io.mosip.preregistration.login.exception.LoginServiceException;
import io.mosip.preregistration.login.exception.NoAuthTokenException;
import io.mosip.preregistration.login.exception.ParseResponseException;
import io.mosip.preregistration.login.exception.SendOtpFailedException;
import io.mosip.preregistration.login.exception.UserIdOtpFaliedException;


/**
 *This class handles the exception caught while login
 *
 * @author Akshay
 *@since 1.0.0
 */

@RestControllerAdvice
public class LoginExceptionHandler {
	
	@Value("${mosip.utc-datetime-pattern}")
	private String utcDateTimePattern;
	
	@Autowired
	private ObjectMapper objectMapper;


	@ExceptionHandler(SendOtpFailedException.class)
	public ResponseEntity<MainResponseDTO<?>> sendOtpException(final SendOtpFailedException e){
		return GenericUtil.errorResponse(e, e.getMainResposneDto());
	}
	
	@ExceptionHandler(UserIdOtpFaliedException.class)
	public ResponseEntity<MainResponseDTO<?>> userIdOtpException(final UserIdOtpFaliedException e){
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	}

	@ExceptionHandler(InvalidateTokenException.class)
	public ResponseEntity<MainResponseDTO<?>> invalidateTokenException(final InvalidateTokenException e){
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	}
	
	@ExceptionHandler(InvalidRequestParameterException.class)
	public ResponseEntity<MainResponseDTO<?>> invalidRequestParameterException(final InvalidRequestParameterException e){
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	} 
	
	@ExceptionHandler(LoginServiceException.class)
	public ResponseEntity<MainResponseDTO<?>> authServiceException(final LoginServiceException e){
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		e.getValidationErrorList().stream().forEach(serviceError->errorList.add(new ExceptionJSONInfoDTO(serviceError.getErrorCode(),serviceError.getMessage())));
		MainResponseDTO<?> errorRes = e.getMainResposneDTO();
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	
	@ExceptionHandler(ParseResponseException.class)
	public ResponseEntity<MainResponseDTO<?>> parseResponseException(final ParseResponseException e){
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	} 
	
	
	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for ConfigFileNotFoundException
	 */
	@ExceptionHandler(ConfigFileNotFoundException.class)
	public ResponseEntity<MainResponseDTO<?>> configFileNotFoundException(final ConfigFileNotFoundException e) {
		return GenericUtil.errorResponse(e,e.getMainResposneDto());
	}
	
	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<MainResponseDTO<?>> DateFormatException(final InvalidFormatException e){
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_CORE_REQ_003.getCode(),ErrorMessages.INVALID_REQUEST_DATETIME.getMessage());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	
	@ExceptionHandler(InvalidOtpOrUseridException.class)
	public ResponseEntity<MainResponseDTO<?>> InavlidOtpOrUserIdException(final InvalidOtpOrUseridException e){
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	}
	
	@ExceptionHandler(NoAuthTokenException.class)
	public ResponseEntity<MainResponseDTO<?>> NoAuthTokenException(final NoAuthTokenException e){
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	}
	
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> methodArgumentNotValidException(
			final HttpServletRequest httpServletRequest, final MethodArgumentNotValidException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError(ErrorCodes.PRG_CORE_REQ_015.getCode(),
					x.getField() + ": " + x.getDefaultMessage());
			errorResponse.getErrors().add(error);
		});
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onHttpMessageNotReadable(
			final HttpServletRequest httpServletRequest, final HttpMessageNotReadableException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(ErrorCodes.PRG_CORE_REQ_015.getCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}
	
	


	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultErrorHandler(
			final HttpServletRequest httpServletRequest, Exception e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(ErrorCodes.PRG_CORE_REQ_016.getCode(), e.getMessage());
		errorResponse.getErrors().add(error);
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
