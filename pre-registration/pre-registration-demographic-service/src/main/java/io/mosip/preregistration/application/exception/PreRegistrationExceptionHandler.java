package io.mosip.preregistration.application.exception;

import java.sql.Timestamp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.preregistration.application.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.application.dto.ResponseDTO;
import io.mosip.preregistration.application.errorcodes.ErrorCodes;
import io.mosip.preregistration.application.errorcodes.ErrorMessages;
import io.mosip.preregistration.application.exception.system.JsonValidationException;
import io.mosip.preregistration.application.exception.system.SystemIllegalArgumentException;
import io.mosip.preregistration.core.exceptions.InvalidRequestParameterException;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;

/**
 * Exception Handler
 * 
 * @author M1037717
 *
 */
@RestControllerAdvice
public class PreRegistrationExceptionHandler {
	protected String falseStatus = "false";

	@ExceptionHandler(TablenotAccessibleException.class)
	public ResponseEntity<ResponseDTO<?>> databaseerror(final TablenotAccessibleException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_APP_002.toString(),
				ErrorMessages.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(falseStatus);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(JsonValidationException.class)
	public ResponseEntity<ResponseDTO<?>> jsonValidationException(final JsonValidationException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_APP_007.toString(),
				e.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(falseStatus);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<ResponseDTO<?>> recException(final RecordNotFoundException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getMessage());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(falseStatus);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(SystemIllegalArgumentException.class)
	public ResponseEntity<ResponseDTO<?>> illegalArgumentException(final SystemIllegalArgumentException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(falseStatus);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DocumentFailedToDeleteException.class)
	public ResponseEntity<ResponseDTO<?>> documentFailedToDeleteException(final DocumentFailedToDeleteException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(falseStatus);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(RecordFailedToDeleteException.class)
	public ResponseEntity<ResponseDTO<?>> recordFailedToDeleteException(final RecordFailedToDeleteException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(falseStatus);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(OperationNotAllowedException.class)
	public ResponseEntity<ResponseDTO<?>> operationNotAllowedException(final OperationNotAllowedException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(falseStatus);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidRequestParameterException.class)
	public ResponseEntity<ResponseDTO<?>> invalidRequest(final InvalidRequestParameterException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(),
				e.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(falseStatus);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
