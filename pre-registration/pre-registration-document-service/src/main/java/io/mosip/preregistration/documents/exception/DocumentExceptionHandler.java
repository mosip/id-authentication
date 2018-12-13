package io.mosip.preregistration.documents.exception;

import java.sql.Timestamp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;

import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;
import io.mosip.preregistration.documents.code.StatusCodes;
import io.mosip.preregistration.documents.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.documents.dto.ResponseDTO;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * Exception Handler
 * 
 * @author M1037717
 *
 */
@RestControllerAdvice
public class DocumentExceptionHandler {

	@ExceptionHandler(TablenotAccessibleException.class)
	public ResponseEntity<ResponseDTO<?>> databaseerror(final TablenotAccessibleException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_007.toString(),
				StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus("false");
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(DocumentNotValidException.class)
	public ResponseEntity<ResponseDTO<?>> notValidExceptionhadler(final DocumentNotValidException nv,
			WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(nv.getErrorCode(), nv.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus("false");
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<ResponseDTO<?>> sizeExceedException(final MultipartException me, WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_004.toString(),
				StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus("false");
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(DocumentNotFoundException.class)
	public ResponseEntity<ResponseDTO<?>> documentNotFound(final DocumentNotFoundException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_005.toString(),
				StatusCodes.DOCUMENT_IS_MISSING.toString());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setStatus("false");
		errorRes.setErr(errorDetails);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.NOT_FOUND);
	}

}
