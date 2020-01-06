/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.exception.util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.core.errorcodes.ErrorCodes;
import io.mosip.preregistration.core.errorcodes.ErrorMessages;
import io.mosip.preregistration.core.exception.DecryptionFailedException;
import io.mosip.preregistration.core.exception.EncryptionFailedException;
import io.mosip.preregistration.core.exception.HashingException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.PreIdInvalidForUserIdException;
import io.mosip.preregistration.core.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.core.exception.RestCallException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.core.util.GenericUtil;
import io.mosip.preregistration.demographic.exception.BookingDeletionFailedException;
import io.mosip.preregistration.demographic.exception.CryptocoreException;
import io.mosip.preregistration.demographic.exception.DemographicServiceException;
import io.mosip.preregistration.demographic.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.demographic.exception.DuplicatePridKeyException;
import io.mosip.preregistration.demographic.exception.IdValidationException;
import io.mosip.preregistration.demographic.exception.InvalidDateFormatException;
import io.mosip.preregistration.demographic.exception.MissingRequestParameterException;
import io.mosip.preregistration.demographic.exception.OperationNotAllowedException;
import io.mosip.preregistration.demographic.exception.RecordFailedToUpdateException;
import io.mosip.preregistration.demographic.exception.RecordNotFoundException;
import io.mosip.preregistration.demographic.exception.RecordNotFoundForPreIdsException;
import io.mosip.preregistration.demographic.exception.SchemaValidationException;
import io.mosip.preregistration.demographic.exception.system.JsonParseException;
import io.mosip.preregistration.demographic.exception.system.JsonValidationException;
import io.mosip.preregistration.demographic.exception.system.SystemFileIOException;
import io.mosip.preregistration.demographic.exception.system.SystemIllegalArgumentException;
import io.mosip.preregistration.demographic.exception.system.SystemUnsupportedEncodingException;

/**
 * Exception Handler for demographic service
 * 
 * @author Rajath KR
 * @author Sanober Noor
 * @author Tapaswini Behera
 * @author Jagadishwari S
 * @author Ravi C Balaji
 * @since 1.0.0
 */
@RestControllerAdvice
public class DemographicExceptionHandler {
	
	/**  The Environment. */
	@Autowired
	protected  Environment env;
	
	/** The id. */
	@Resource
	protected Map<String, String> id;

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for TableNotAccessibleException
	 */
	@ExceptionHandler(TableNotAccessibleException.class)
	public ResponseEntity<MainResponseDTO<?>> databaseerror(final TableNotAccessibleException e) {
		return GenericUtil.errorResponse(e, e.getMainResposneDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for JsonValidationException
	 */
	@ExceptionHandler(JsonValidationException.class)
	public ResponseEntity<MainResponseDTO<?>> jsonValidationException(final JsonValidationException e) {
		return GenericUtil.errorResponse(e, e.getMainResposneDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for RecordNotFoundException
	 */
	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<MainResponseDTO<?>> recException(final RecordNotFoundException e) {
		return GenericUtil.errorResponse(e, e.getMainresponseDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for RecordNotFoundException
	 */
	@ExceptionHandler(RecordNotFoundForPreIdsException.class)
	public ResponseEntity<MainResponseDTO<?>> recPreIdsException(final RecordNotFoundForPreIdsException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for SystemIllegalArgumentException
	 */
	@ExceptionHandler(SystemIllegalArgumentException.class)
	public ResponseEntity<MainResponseDTO<?>> illegalArgumentException(final SystemIllegalArgumentException e) {
		return GenericUtil.errorResponse(e, e.getMainresponseDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for DocumentFailedToDeleteException
	 */
	@ExceptionHandler(DocumentFailedToDeleteException.class)
	public ResponseEntity<MainResponseDTO<?>> documentFailedToDeleteException(final DocumentFailedToDeleteException e) {
		return GenericUtil.errorResponse(e, e.getMainresponseDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for RecordFailedToDeleteException
	 */
	@ExceptionHandler(RecordFailedToDeleteException.class)
	public ResponseEntity<MainResponseDTO<?>> recordFailedToDeleteException(final RecordFailedToDeleteException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for OperationNotAllowedException
	 */
	@ExceptionHandler(OperationNotAllowedException.class)
	public ResponseEntity<MainResponseDTO<?>> operationNotAllowedException(final OperationNotAllowedException e) {
		return GenericUtil.errorResponse(e, e.getMainresponseDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for InvalidRequestParameterException
	 */
	@ExceptionHandler(InvalidRequestParameterException.class)
	public ResponseEntity<MainResponseDTO<?>> invalidRequest(final InvalidRequestParameterException e) {
		MainResponseDTO<?> errorRes = e.getMainResponseDto();
		errorRes.setId(id.get(e.getOperation()));
		errorRes.setVersion(env.getProperty("version"));
		errorRes.setErrors(e.getExptionList());
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	
	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for InvalidRequestParameterException
	 */
	@ExceptionHandler(CryptocoreException.class)
	public ResponseEntity<MainResponseDTO<?>> cryptocoreException(final CryptocoreException e) {
		return GenericUtil.errorResponse(e, e.getMainresponseDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for RecordFailedToUpdateException
	 */
	@ExceptionHandler(RecordFailedToUpdateException.class)
	public ResponseEntity<MainResponseDTO<?>> recordFailedToUpdateException(final RecordFailedToUpdateException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for SystemUnsupportedEncodingException
	 */
	@ExceptionHandler(SystemUnsupportedEncodingException.class)
	public ResponseEntity<MainResponseDTO<?>> systemUnsupportedEncodingException(
			final SystemUnsupportedEncodingException e) {
		return GenericUtil.errorResponse(e, e.getMainresponseDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for MissingRequestParameterException
	 */
	@ExceptionHandler(MissingRequestParameterException.class)
	public ResponseEntity<MainResponseDTO<?>> missingRequestParameterException(
			final MissingRequestParameterException e) {
		return GenericUtil.errorResponse(e, e.getMainresponseDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for JsonParseException
	 */
	@ExceptionHandler(JsonParseException.class)
	public ResponseEntity<MainResponseDTO<?>> jsonParseException(final JsonParseException e) {
		return GenericUtil.errorResponse(e, e.getMainresponseDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for SystemFileIOException
	 */
	@ExceptionHandler(SystemFileIOException.class)
	public ResponseEntity<MainResponseDTO<?>> systemFileIOException(final SystemFileIOException e) {
		return GenericUtil.errorResponse(e, e.getMainResposneDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for SystemFileIOException
	 */
	@ExceptionHandler(InvalidDateFormatException.class)
	public ResponseEntity<MainResponseDTO<?>> InvalidDateFormatException(final InvalidDateFormatException e) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainResponseDTO().getId());
		errorRes.setVersion(e.getMainResponseDTO().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for SystemFileIOException
	 */
	@ExceptionHandler(BookingDeletionFailedException.class)
	public ResponseEntity<MainResponseDTO<?>> bookingDeletionFailedException(final BookingDeletionFailedException e) {
		return GenericUtil.errorResponse(e, e.getMainresponseDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for SystemFileIOException
	 */
	@ExceptionHandler(HashingException.class)
	public ResponseEntity<MainResponseDTO<?>> HashingException(final HashingException e) {
		return GenericUtil.errorResponse(e, e.getMainresponseDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for DecryptionFailedException
	 */
	@ExceptionHandler(DecryptionFailedException.class)
	public ResponseEntity<MainResponseDTO<?>> decryptionFailedException(final DecryptionFailedException e) {
		return GenericUtil.errorResponse(e, e.getMainresponseDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for RestCallException
	 */
	@ExceptionHandler(RestCallException.class)
	public ResponseEntity<MainResponseDTO<?>> restCallException(final RestCallException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for SchemaValidationException
	 */
	@ExceptionHandler(SchemaValidationException.class)
	public ResponseEntity<MainResponseDTO<?>> restCallException(final SchemaValidationException e) {
		return GenericUtil.errorResponse(e, e.getMainresponseDTO());
	}

	@ExceptionHandler(PreIdInvalidForUserIdException.class)
	public ResponseEntity<MainResponseDTO<?>> invalidUserException(final PreIdInvalidForUserIdException e) {
		return GenericUtil.errorResponse(e, e.getMainresponseDTO());
	}

	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<MainResponseDTO<?>> DateFormatException(final InvalidFormatException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_CORE_REQ_003.getCode(),
				ErrorMessages.INVALID_REQUEST_DATETIME.getMessage());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	
	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for EncryptionFailedException
	 */
	@ExceptionHandler(EncryptionFailedException.class)
	public ResponseEntity<MainResponseDTO<?>> encryptionFailedException(final EncryptionFailedException e){
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		e.getValidationErrorList().stream().forEach(serviceError->errorList.add(new ExceptionJSONInfoDTO(serviceError.getErrorCode(),serviceError.getMessage())));
		MainResponseDTO<?> errorRes = e.getMainresponseDTO();
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	
	@ExceptionHandler(IdValidationException.class)
	public ResponseEntity<MainResponseDTO<?>> idValidationException(final IdValidationException e){
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		e.getErrorMessageList().stream().forEach(s->errorList.add(new ExceptionJSONInfoDTO(e.getErrorCode(),s)));
		MainResponseDTO<?> errorRes = e.getMainResposneDTO();
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	
	@ExceptionHandler(DemographicServiceException.class)
	public ResponseEntity<MainResponseDTO<?>> demographicServiceException(final DemographicServiceException e){
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		e.getValidationErrorList().stream().forEach(serviceError->errorList.add(new ExceptionJSONInfoDTO(serviceError.getErrorCode(),serviceError.getMessage())));
		MainResponseDTO<?> errorRes = e.getMainResposneDTO();
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
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

	@Autowired
	private ObjectMapper objectMapper;
	
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
	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for DuplicateKeyException
	 */
	@ExceptionHandler(DuplicatePridKeyException.class)
	public ResponseEntity<MainResponseDTO<?>> duplicateKeyException(final DuplicatePridKeyException e) {
		return GenericUtil.errorResponse(e, e.getMainresponseDTO());
	}
}
