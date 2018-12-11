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
import io.mosip.preregistration.documents.dto.ExceptionJSONInfo;
import io.mosip.preregistration.documents.dto.ResponseDto;
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
	public ResponseEntity<ResponseDto<?>> databaseerror(final TablenotAccessibleException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_PAM_DOC_007.toString(),
				StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());
		ResponseDto<?> errorRes = new ResponseDto<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus("false");
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(DocumentNotValidException.class)
	public ResponseEntity<ResponseDto<?>> notValidExceptionhadler(final DocumentNotValidException nv,
			WebRequest webRequest) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(nv.getErrorCode(), nv.getErrorText());
		ResponseDto<?> errorRes = new ResponseDto<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus("false");
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<ResponseDto<?>> sizeExceedException(final MultipartException me, WebRequest webRequest) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_PAM_DOC_004.toString(),
				StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());
		ResponseDto<?> errorRes = new ResponseDto<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus("false");
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(DocumentNotFoundException.class)
	public ResponseEntity<ResponseDto<?>> documentNotFound(final DocumentNotFoundException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_PAM_DOC_005.toString(),
				StatusCodes.DOCUMENT_IS_MISSING.toString());
		ResponseDto<?> errorRes = new ResponseDto<>();
		errorRes.setStatus("false");
		errorRes.setErr(errorDetails);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.NOT_FOUND);
	}

}
