/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.exception.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;

import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.exception.DecryptionFailedException;
import io.mosip.preregistration.core.exception.EncryptionFailedException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.documents.code.DocumentStatusMessages;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.exception.DTOMappigException;
import io.mosip.preregistration.documents.exception.DemographicGetDetailsException;
import io.mosip.preregistration.documents.exception.DocumentFailedToCopyException;
import io.mosip.preregistration.documents.exception.DocumentFailedToUploadException;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.exception.DocumentNotValidException;
import io.mosip.preregistration.documents.exception.DocumentSizeExceedException;
import io.mosip.preregistration.documents.exception.DocumentVirusScanException;
import io.mosip.preregistration.documents.exception.FSServerException;
import io.mosip.preregistration.documents.exception.FileNotFoundException;
import io.mosip.preregistration.documents.exception.InvalidDocumentIdExcepion;
import io.mosip.preregistration.documents.exception.MandatoryFieldNotFoundException;
import io.mosip.preregistration.documents.exception.ParsingException;
import io.mosip.preregistration.documents.exception.PrimaryKeyValidationException;

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

	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	/**
	 * Reference for ${mosip.preregistration.document.upload.id} from property file
	 */
	@Value("${mosip.preregistration.document.upload.id}")
	private String uploadId;

	/**
	 * Reference for ${mosip.preregistration.document.copy.id} from property file
	 */
	@Value("${mosip.preregistration.document.copy.id}")
	private String copyId;
	
	/**
	 * Reference for ${mosip.preregistration.document.fetch.metadata.id} from property file
	 */
	@Value("${mosip.preregistration.document.fetch.metadata.id}")
	private String fetchMetaDataId;
	
	/**
	 * Reference for ${mosip.preregistration.document.fetch.content.id} from property file
	 */
	@Value("${mosip.preregistration.document.fetch.content.id}")
	private String fetchContentId;
	
	/**
	 * Reference for ${mosip.preregistration.document.delete.id} from property file
	 */
	@Value("${mosip.preregistration.document.delete.id}")
	private String deleteId;
	
	/**
	 * Reference for ${mosip.preregistration.document.delete.specific.id} from property file
	 */
	@Value("${mosip.preregistration.document.delete.specific.id}")
	private String deleteSpecificId;

	/**
	 * Reference for ${ver} from property file
	 */
	@Value("${version}")
	private String ver;

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for TablenotAccessibleException
	 */
	@ExceptionHandler(TableNotAccessibleException.class)
	public ResponseEntity<MainListResponseDTO> databaseerror(final TableNotAccessibleException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for DemographicGetDetailsException
	 */
	@ExceptionHandler(DemographicGetDetailsException.class)
	public ResponseEntity<MainListResponseDTO> databaseerror(final DemographicGetDetailsException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
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
	public ResponseEntity<MainListResponseDTO> notValidExceptionhadler(final DocumentNotValidException e,
			WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
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
	public ResponseEntity<MainListResponseDTO> dtoMappingExc(final DTOMappigException e, WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
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
	public ResponseEntity<MainListResponseDTO> fileNotFoundException(final FileNotFoundException e,
			WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
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
	public ResponseEntity<MainListResponseDTO> mandatoryFieldNotFoundException(final MandatoryFieldNotFoundException e,
			WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
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
	public ResponseEntity<MainListResponseDTO> parsingException(final ParsingException e, WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
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
	public ResponseEntity<MainListResponseDTO> sizeExceedException(final MultipartException e, WebRequest webRequest) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_004.toString(),
				DocumentStatusMessages.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
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
	public ResponseEntity<MainListResponseDTO> documentNotFound(final DocumentNotFoundException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
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
	public ResponseEntity<MainListResponseDTO> documentSizeExceedException(final DocumentSizeExceedException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
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
	public ResponseEntity<MainListResponseDTO> documentFailedToUploadException(final DocumentFailedToUploadException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
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
	public ResponseEntity<MainListResponseDTO> invalidRequestParameterException(
			final InvalidRequestParameterException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
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
	public ResponseEntity<MainListResponseDTO> documentVirusScanException(final DocumentVirusScanException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
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
	public ResponseEntity<MainListResponseDTO> documentFailedToCopyException(final DocumentFailedToCopyException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for InvalidDocumnetIdExcepion
	 */
	@ExceptionHandler(InvalidDocumentIdExcepion.class)
	public ResponseEntity<MainListResponseDTO> invalidDocumnetIdExcepion(final InvalidDocumentIdExcepion e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for CephServerException
	 */
	@ExceptionHandler(FSServerException.class)
	public ResponseEntity<MainListResponseDTO> cephServerException(final FSServerException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for PrimaryKeyValidationException
	 */
	@ExceptionHandler(PrimaryKeyValidationException.class)
	public ResponseEntity<MainListResponseDTO> primaryKeyValidationException(final PrimaryKeyValidationException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for FSAdapterException
	 */
	@ExceptionHandler(FSAdapterException.class)
	public ResponseEntity<MainListResponseDTO> fSAdapterException(final FSAdapterException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for EncryptionFailedException
	 */
	@ExceptionHandler(EncryptionFailedException.class)
	public ResponseEntity<MainListResponseDTO> encryptionFailedException(final EncryptionFailedException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
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
	public ResponseEntity<MainListResponseDTO> decryptionFailedException(final DecryptionFailedException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainListResponseDTO<?> errorRes = new MainListResponseDTO<>();
		errorRes.setErrors(errorDetails);
		errorRes.setResponsetime(getCurrentResponseTime());
//		errorRes.setId(id);
		errorRes.setVersion(ver);
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
}