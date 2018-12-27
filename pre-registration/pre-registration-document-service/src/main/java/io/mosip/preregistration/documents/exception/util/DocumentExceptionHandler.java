/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.exception.util;

import java.sql.Timestamp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;

import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TablenotAccessibleException;
import io.mosip.preregistration.documents.code.StatusCodes;
import io.mosip.preregistration.documents.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.documents.dto.ResponseDTO;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.errorcodes.ErrorMessages;
import io.mosip.preregistration.documents.exception.ConnectionUnavailableException;
import io.mosip.preregistration.documents.exception.DTOMappigException;
import io.mosip.preregistration.documents.exception.DocumentFailedToCopyException;
import io.mosip.preregistration.documents.exception.DocumentFailedToUploadException;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.exception.DocumentNotValidException;
import io.mosip.preregistration.documents.exception.DocumentSizeExceedException;
import io.mosip.preregistration.documents.exception.DocumentVirusScanException;
import io.mosip.preregistration.documents.exception.FileNotFoundException;
import io.mosip.preregistration.documents.exception.InvalidConnectionParameters;
import io.mosip.preregistration.documents.exception.MandatoryFieldNotFoundException;
import io.mosip.preregistration.documents.exception.ParsingException;

/**
 * This class is defines the Exception handler for Document service
 * 
 * @author Rajath KR
 * @author Tapaswini Bahera
 * @author Jagadishwari S
 * @author Kishan Rathore
 * @since 1.0.0
 */
@RestControllerAdvice
public class DocumentExceptionHandler {

	/**
	 * @param e
	 * @param request
	 * @return
	 */
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

	/**
	 * @param nv
	 * @param webRequest
	 * @return
	 */
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

	/**
	 * @param nv
	 * @param webRequest
	 * @return
	 */
	@ExceptionHandler(DTOMappigException.class)
	public ResponseEntity<ResponseDTO<?>> DTOMappigExc(final DocumentNotValidException nv, WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(nv.getErrorCode(), nv.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus("false");
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);

	}

	/**
	 * @param nv
	 * @param webRequest
	 * @return
	 */
	@ExceptionHandler(InvalidConnectionParameters.class)
	public ResponseEntity<ResponseDTO<?>> invalidConnectionParameters(final InvalidConnectionParameters nv,
			WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(nv.getErrorCode(), nv.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus("false");
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);

	}

	/**
	 * @param nv
	 * @param webRequest
	 * @return
	 */
	@ExceptionHandler(ConnectionUnavailableException.class)
	public ResponseEntity<ResponseDTO<?>> connectionUnavailableException(final ConnectionUnavailableException nv,
			WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(nv.getErrorCode(), nv.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus("false");
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);

	}

	/**
	 * @param nv
	 * @param webRequest
	 * @return
	 */
	@ExceptionHandler(FileNotFoundException.class)
	public ResponseEntity<ResponseDTO<?>> fileNotFoundException(final FileNotFoundException nv, WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(nv.getErrorCode(), nv.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus("false");
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);

	}

	/**
	 * @param nv
	 * @param webRequest
	 * @return
	 */
	@ExceptionHandler(MandatoryFieldNotFoundException.class)
	public ResponseEntity<ResponseDTO<?>> mandatoryFieldNotFoundException(final MandatoryFieldNotFoundException nv,
			WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(nv.getErrorCode(), nv.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus("false");
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);

	}

	/**
	 * @param nv
	 * @param webRequest
	 * @return
	 */
	@ExceptionHandler(ParsingException.class)
	public ResponseEntity<ResponseDTO<?>> parsingException(final ParsingException nv, WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(nv.getErrorCode(), nv.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus("false");
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);

	}

	/**
	 * @param me
	 * @param webRequest
	 * @return
	 */
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

	/**
	 * @param e
	 * @param request
	 * @return
	 */
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
	
	/**
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(DocumentSizeExceedException.class)
	public ResponseEntity<ResponseDTO<?>> documentSizeExceedException(final DocumentSizeExceedException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_007.toString(),
				ErrorMessages.DOCUMENT_EXCEEDING_PREMITTED_SIZE.toString());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setStatus("false");
		errorRes.setErr(errorDetails);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(DocumentFailedToUploadException.class)
	public ResponseEntity<ResponseDTO<?>> documentFailedToUploadException(final DocumentFailedToUploadException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_009.toString(),
				ErrorMessages.DOCUMENT_FAILED_TO_UPLOAD.toString());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setStatus("false");
		errorRes.setErr(errorDetails);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.NOT_FOUND);
	}
	
	/**
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(InvalidRequestParameterException.class)
	public ResponseEntity<ResponseDTO<?>> invalidRequestParameterException(final InvalidRequestParameterException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_018.toString(),
				ErrorMessages.INVALID_REQUEST_PARAMETER.toString());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setStatus("false");
		errorRes.setErr(errorDetails);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(DocumentVirusScanException.class)
	public ResponseEntity<ResponseDTO<?>> documentVirusScanException(final DocumentVirusScanException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_010.toString(),
				ErrorMessages.DOCUMENT_FAILED_IN_VIRUS_SCAN.toString());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setStatus("false");
		errorRes.setErr(errorDetails);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	/**
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(DocumentFailedToCopyException.class)
	public ResponseEntity<ResponseDTO<?>> documentFailedToCopyException(final DocumentFailedToCopyException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_011.toString(),
				ErrorMessages.DOCUMENT_FAILED_TO_COPY.toString());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setStatus("false");
		errorRes.setErr(errorDetails);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
