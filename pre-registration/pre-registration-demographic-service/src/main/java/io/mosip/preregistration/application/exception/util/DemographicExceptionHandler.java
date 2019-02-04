/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.exception.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.preregistration.application.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.application.exception.MissingRequestParameterException;
import io.mosip.preregistration.application.exception.OperationNotAllowedException;
import io.mosip.preregistration.application.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.application.exception.RecordFailedToUpdateException;
import io.mosip.preregistration.application.exception.RecordNotFoundException;
import io.mosip.preregistration.application.exception.system.JsonParseException;
import io.mosip.preregistration.application.exception.system.JsonValidationException;
import io.mosip.preregistration.application.exception.system.SystemFileIOException;
import io.mosip.preregistration.application.exception.system.SystemIllegalArgumentException;
import io.mosip.preregistration.application.exception.system.SystemUnsupportedEncodingException;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.core.util.GenericUtil;

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

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for TableNotAccessibleException
	 */
	@ExceptionHandler(TableNotAccessibleException.class)
	public ResponseEntity<MainListResponseDTO> databaseerror(final TableNotAccessibleException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(Boolean.FALSE);
		errorRes.setResTime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for JsonValidationException
	 */
	@ExceptionHandler(JsonValidationException.class)
	public ResponseEntity<MainListResponseDTO> jsonValidationException(final JsonValidationException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(Boolean.FALSE);
		errorRes.setResTime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for RecordNotFoundException
	 */
	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<MainListResponseDTO> recException(final RecordNotFoundException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(Boolean.FALSE);
		errorRes.setResTime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for SystemIllegalArgumentException
	 */
	@ExceptionHandler(SystemIllegalArgumentException.class)
	public ResponseEntity<MainListResponseDTO> illegalArgumentException(final SystemIllegalArgumentException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(Boolean.FALSE);
		errorRes.setResTime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for DocumentFailedToDeleteException
	 */
	@ExceptionHandler(DocumentFailedToDeleteException.class)
	public ResponseEntity<MainListResponseDTO> documentFailedToDeleteException(
			final DocumentFailedToDeleteException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(Boolean.FALSE);
		errorRes.setResTime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for RecordFailedToDeleteException
	 */
	@ExceptionHandler(RecordFailedToDeleteException.class)
	public ResponseEntity<MainListResponseDTO> recordFailedToDeleteException(final RecordFailedToDeleteException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(Boolean.FALSE);
		errorRes.setResTime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for OperationNotAllowedException
	 */
	@ExceptionHandler(OperationNotAllowedException.class)
	public ResponseEntity<MainListResponseDTO> operationNotAllowedException(final OperationNotAllowedException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(Boolean.FALSE);
		errorRes.setResTime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for InvalidRequestParameterException
	 */
	@ExceptionHandler(InvalidRequestParameterException.class)
	public ResponseEntity<MainListResponseDTO> invalidRequest(final InvalidRequestParameterException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(Boolean.FALSE);
		errorRes.setResTime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	
	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for RecordFailedToUpdateException
	 */
	@ExceptionHandler(RecordFailedToUpdateException.class)
	public ResponseEntity<MainListResponseDTO> recordFailedToUpdateException(final RecordFailedToUpdateException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(Boolean.FALSE);
		errorRes.setResTime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	
	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for SystemUnsupportedEncodingException
	 */
	@ExceptionHandler(SystemUnsupportedEncodingException.class)
	public ResponseEntity<MainListResponseDTO> systemUnsupportedEncodingException(final SystemUnsupportedEncodingException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(Boolean.FALSE);
		errorRes.setResTime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for MissingRequestParameterException
	 */
	@ExceptionHandler(MissingRequestParameterException.class)
	public ResponseEntity<MainListResponseDTO> missingRequestParameterException(final MissingRequestParameterException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(Boolean.FALSE);
		errorRes.setResTime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	
	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for JsonParseException
	 */
	@ExceptionHandler(JsonParseException.class)
	public ResponseEntity<MainListResponseDTO> jsonParseException(final JsonParseException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(Boolean.FALSE);
		errorRes.setResTime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	
	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for SystemFileIOException
	 */
	@ExceptionHandler(SystemFileIOException.class)
	public ResponseEntity<MainListResponseDTO> systemFileIOException(final SystemFileIOException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(Boolean.FALSE);
		errorRes.setResTime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
}
