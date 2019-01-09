/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.exception.util;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.documents.code.StatusCodes;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.errorcodes.ErrorMessages;
import io.mosip.preregistration.documents.exception.CephConnectionUnavailableException;
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
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.PacketNotFoundException;

/**
 * This class is defines the Exception handler for Document service
 * 
 * @author Rajath KR
 * @author Tapaswini Behera
 * @author Jagadishwari S
 * @author Kishan Rathore
 * @since 1.0.0
 */
@RestControllerAdvice
public class DocumentExceptionHandler {

	private boolean responseStatus = false;

	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for TablenotAccessibleException
	 */
	@ExceptionHandler(TableNotAccessibleException.class)
	public ResponseEntity<MainListResponseDTO<?>> databaseerror(final TableNotAccessibleException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(responseStatus);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param nv
	 *            pass the exception
	 * @param webRequest
	 *            pass the request
	 * @return response for DocumentNotValidException
	 */
	@ExceptionHandler(DocumentNotValidException.class)
	public ResponseEntity<MainListResponseDTO<?>> notValidExceptionhadler(final DocumentNotValidException nv,
			WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(nv.getErrorCode(), nv.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(responseStatus);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);

	}

	/**
	 * @param nv
	 *            pass the exception
	 * @param webRequest
	 *            pass the request
	 * @return response for DTOMappigException
	 */
	@ExceptionHandler(DTOMappigException.class)
	public ResponseEntity<MainListResponseDTO<?>> DTOMappigExc(final DocumentNotValidException nv,
			WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(nv.getErrorCode(), nv.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(responseStatus);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);

	}

	/**
	 * @param nv
	 *            pass the exception
	 * @param webRequest
	 *            pass the request
	 * @return response for InvalidConnectionParameters
	 */
	@ExceptionHandler(InvalidConnectionParameters.class)
	public ResponseEntity<MainListResponseDTO<?>> invalidConnectionParameters(final InvalidConnectionParameters nv,
			WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(nv.getErrorCode(), nv.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(responseStatus);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);

	}

	/**
	 * @param nv
	 *            pass the exception
	 * @param webRequest
	 *            pass the request
	 * @return response for ConnectionUnavailableException
	 */
	@ExceptionHandler(CephConnectionUnavailableException.class)
	public ResponseEntity<MainListResponseDTO<?>> connectionUnavailableException(
			final CephConnectionUnavailableException nv, WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(nv.getErrorCode(), nv.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(responseStatus);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);

	}

	/**
	 * @param nv
	 *            pass the exception
	 * @param webRequest
	 *            pass the request
	 * @return response for FileNotFoundException
	 */
	@ExceptionHandler(FileNotFoundException.class)
	public ResponseEntity<MainListResponseDTO<?>> fileNotFoundException(final FileNotFoundException nv,
			WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(nv.getErrorCode(), nv.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(responseStatus);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);

	}

	/**
	 * @param nv
	 *            pass the exception
	 * @param webRequest
	 *            pass the request
	 * @return response for MandatoryFieldNotFoundException
	 */
	@ExceptionHandler(MandatoryFieldNotFoundException.class)
	public ResponseEntity<MainListResponseDTO<?>> mandatoryFieldNotFoundException(
			final MandatoryFieldNotFoundException nv, WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(nv.getErrorCode(), nv.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(responseStatus);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);

	}

	/**
	 * @param nv
	 *            pass the exception
	 * @param webRequest
	 *            pass the request
	 * @return response for ParsingException
	 */
	@ExceptionHandler(ParsingException.class)
	public ResponseEntity<MainListResponseDTO<?>> parsingException(final ParsingException nv, WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(nv.getErrorCode(), nv.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(responseStatus);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);

	}

	/**
	 * @param me
	 *            pass the exception
	 * @param webRequest
	 *            pass the request
	 * @return response for MultipartException
	 */
	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<MainListResponseDTO<?>> sizeExceedException(final MultipartException me,
			WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_004.toString(),
				StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(responseStatus);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for DocumentNotFoundException
	 */
	@ExceptionHandler(DocumentNotFoundException.class)
	public ResponseEntity<MainListResponseDTO<?>> documentNotFound(final DocumentNotFoundException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_005.toString(),
				StatusCodes.DOCUMENT_IS_MISSING.toString());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(responseStatus);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for DocumentSizeExceedException
	 */
	@ExceptionHandler(DocumentSizeExceedException.class)
	public ResponseEntity<MainListResponseDTO<?>> documentSizeExceedException(final DocumentSizeExceedException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_007.toString(),
				ErrorMessages.DOCUMENT_EXCEEDING_PREMITTED_SIZE.toString());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(responseStatus);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for DocumentFailedToUploadException
	 */
	@ExceptionHandler(DocumentFailedToUploadException.class)
	public ResponseEntity<MainListResponseDTO<?>> documentFailedToUploadException(
			final DocumentFailedToUploadException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_009.toString(),
				ErrorMessages.DOCUMENT_FAILED_TO_UPLOAD.toString());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(responseStatus);
		errorRes.setResTime(getCurrentResponseTime());
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
	public ResponseEntity<MainListResponseDTO<?>> invalidRequestParameterException(
			final InvalidRequestParameterException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_018.toString(),
				ErrorMessages.INVALID_REQUEST_PARAMETER.toString());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(responseStatus);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for DocumentVirusScanException
	 */
	@ExceptionHandler(DocumentVirusScanException.class)
	public ResponseEntity<MainListResponseDTO<?>> documentVirusScanException(final DocumentVirusScanException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_010.toString(),
				ErrorMessages.DOCUMENT_FAILED_IN_VIRUS_SCAN.toString());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(responseStatus);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for DocumentFailedToCopyException
	 */
	@ExceptionHandler(DocumentFailedToCopyException.class)
	public ResponseEntity<MainListResponseDTO<?>> documentFailedToCopyException(final DocumentFailedToCopyException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_011.toString(),
				ErrorMessages.DOCUMENT_FAILED_TO_COPY.toString());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(responseStatus);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for DocumentFailedToCopyException
	 */
	@ExceptionHandler(PacketNotFoundException.class)
	public ResponseEntity<MainListResponseDTO<?>> packetNotFoundException(final PacketNotFoundException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(responseStatus);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
	}

}
