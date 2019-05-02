/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.exception.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.preregistration.application.exception.BookingDeletionFailedException;
import io.mosip.preregistration.application.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.application.exception.InvalidDateFormatException;
import io.mosip.preregistration.application.exception.MissingRequestParameterException;
import io.mosip.preregistration.application.exception.OperationNotAllowedException;
import io.mosip.preregistration.application.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.application.exception.RecordFailedToUpdateException;
import io.mosip.preregistration.application.exception.RecordNotFoundException;
import io.mosip.preregistration.application.exception.RecordNotFoundForPreIdsException;
import io.mosip.preregistration.application.exception.RestCallException;
import io.mosip.preregistration.application.exception.SchemaValidationException;
import io.mosip.preregistration.application.exception.system.JsonParseException;
import io.mosip.preregistration.application.exception.system.JsonValidationException;
import io.mosip.preregistration.application.exception.system.SystemFileIOException;
import io.mosip.preregistration.application.exception.system.SystemIllegalArgumentException;
import io.mosip.preregistration.application.exception.system.SystemUnsupportedEncodingException;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.exception.DecryptionFailedException;
import io.mosip.preregistration.core.exception.EncryptionFailedException;
import io.mosip.preregistration.core.exception.HashingException;
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
	public ResponseEntity<MainResponseDTO> databaseerror(final TableNotAccessibleException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainResposneDTO().getId());
		errorRes.setVersion(e.getMainResposneDTO().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
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
	public ResponseEntity<MainResponseDTO> jsonValidationException(final JsonValidationException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainResposneDTO().getId());
		errorRes.setVersion(e.getMainResposneDTO().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
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
	public ResponseEntity<MainResponseDTO> recException(final RecordNotFoundException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainresponseDTO().getId());
		errorRes.setVersion(e.getMainresponseDTO().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for RecordNotFoundException
	 */
	@ExceptionHandler(RecordNotFoundForPreIdsException.class)
	public ResponseEntity<MainResponseDTO> recPreIdsException(final RecordNotFoundForPreIdsException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> exceptionJSONInfoDTOs = new ArrayList<>();
		exceptionJSONInfoDTOs.add(errorDetails);
		errorRes.setId(e.getMainResponseDTO().getId());
		errorRes.setVersion(e.getMainResponseDTO().getVersion());
		errorRes.setErrors(exceptionJSONInfoDTOs);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
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
	public ResponseEntity<MainResponseDTO> illegalArgumentException(final SystemIllegalArgumentException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> exceptionJSONInfoDTOs = new ArrayList<>();
		exceptionJSONInfoDTOs.add(errorDetails);
		errorRes.setId(e.getMainresponseDTO().getId());
		errorRes.setVersion(e.getMainresponseDTO().getVersion());
		errorRes.setErrors(exceptionJSONInfoDTOs);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
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
	public ResponseEntity<MainResponseDTO> documentFailedToDeleteException(final DocumentFailedToDeleteException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainresponseDTO().getId());
		errorRes.setVersion(e.getMainresponseDTO().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
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
	public ResponseEntity<MainResponseDTO> recordFailedToDeleteException(final RecordFailedToDeleteException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
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
	 * @return response for OperationNotAllowedException
	 */
	@ExceptionHandler(OperationNotAllowedException.class)
	public ResponseEntity<MainResponseDTO> operationNotAllowedException(final OperationNotAllowedException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainresponseDTO().getId());
		errorRes.setVersion(e.getMainresponseDTO().getVersion());
		errorRes.setErrors(errorList);
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
	@ExceptionHandler(InvalidRequestParameterException.class)
	public ResponseEntity<MainResponseDTO> invalidRequest(final InvalidRequestParameterException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainResponseDto().getId());
		errorRes.setVersion(e.getMainResponseDto().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
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
	public ResponseEntity<MainResponseDTO> recordFailedToUpdateException(final RecordFailedToUpdateException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
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
	 * @return response for SystemUnsupportedEncodingException
	 */
	@ExceptionHandler(SystemUnsupportedEncodingException.class)
	public ResponseEntity<MainResponseDTO> systemUnsupportedEncodingException(
			final SystemUnsupportedEncodingException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainresponseDTO().getId());
		errorRes.setVersion(e.getMainresponseDTO().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
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
	public ResponseEntity<MainResponseDTO> missingRequestParameterException(
			final MissingRequestParameterException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainresponseDTO().getId());
		errorRes.setVersion(e.getMainresponseDTO().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
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
	public ResponseEntity<MainResponseDTO> jsonParseException(final JsonParseException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainresponseDTO().getId());
		errorRes.setVersion(e.getMainresponseDTO().getVersion());
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
	@ExceptionHandler(SystemFileIOException.class)
	public ResponseEntity<MainResponseDTO> systemFileIOException(final SystemFileIOException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainResposneDTO().getId());
		errorRes.setVersion(e.getMainResposneDTO().getVersion());
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
	@ExceptionHandler(InvalidDateFormatException.class)
	public ResponseEntity<MainResponseDTO> InvalidDateFormatException(final InvalidDateFormatException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
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
	public ResponseEntity<MainResponseDTO> bookingDeletionFailedException(final BookingDeletionFailedException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainresponseDTO().getId());
		errorRes.setVersion(e.getMainresponseDTO().getVersion());
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
	@ExceptionHandler(HashingException.class)
	public ResponseEntity<MainResponseDTO> HashingException(final HashingException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainresponseDTO().getId());
		errorRes.setVersion(e.getMainresponseDTO().getVersion());
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
	public ResponseEntity<MainResponseDTO> encryptionFailedException(final EncryptionFailedException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainresponseDTO().getId());
		errorRes.setVersion(e.getMainresponseDTO().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for DecryptionFailedException
	 */
	@ExceptionHandler(DecryptionFailedException.class)
	public ResponseEntity<MainResponseDTO> decryptionFailedException(final DecryptionFailedException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainresponseDTO().getId());
		errorRes.setVersion(e.getMainresponseDTO().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for RestCallException
	 */
	@ExceptionHandler(RestCallException.class)
	public ResponseEntity<MainResponseDTO> restCallException(final RestCallException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainresponseDTO().getId());
		errorRes.setVersion(e.getMainresponseDTO().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for SchemaValidationException
	 */
	@ExceptionHandler(SchemaValidationException.class)
	public ResponseEntity<MainResponseDTO> restCallException(final SchemaValidationException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList=new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainresponseDTO().getId());
		errorRes.setVersion(e.getMainresponseDTO().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

}
