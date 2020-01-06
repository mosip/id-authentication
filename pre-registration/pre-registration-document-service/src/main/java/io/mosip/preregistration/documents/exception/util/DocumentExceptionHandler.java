/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.exception.util;

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
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.errorcodes.ErrorMessages;
import io.mosip.preregistration.core.exception.DecryptionFailedException;
import io.mosip.preregistration.core.exception.EncryptionFailedException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.MasterDataNotAvailableException;
import io.mosip.preregistration.core.exception.PreIdInvalidForUserIdException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.core.util.GenericUtil;
import io.mosip.preregistration.document.code.DocumentStatusMessages;
import io.mosip.preregistration.document.errorcodes.ErrorCodes;
import io.mosip.preregistration.document.exception.DTOMappigException;
import io.mosip.preregistration.document.exception.DemographicGetDetailsException;
import io.mosip.preregistration.document.exception.DocumentFailedToCopyException;
import io.mosip.preregistration.document.exception.DocumentFailedToUploadException;
import io.mosip.preregistration.document.exception.DocumentNotFoundException;
import io.mosip.preregistration.document.exception.DocumentNotValidException;
import io.mosip.preregistration.document.exception.DocumentSizeExceedException;
import io.mosip.preregistration.document.exception.DocumentVirusScanException;
import io.mosip.preregistration.document.exception.FSServerException;
import io.mosip.preregistration.document.exception.FileNotFoundException;
import io.mosip.preregistration.document.exception.InvalidDocumentIdExcepion;
import io.mosip.preregistration.document.exception.MandatoryFieldNotFoundException;
import io.mosip.preregistration.document.exception.ParsingException;
import io.mosip.preregistration.document.exception.PrimaryKeyValidationException;

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
	private static Logger log = LoggerConfiguration.logConfig(DocumentExceptionHandler.class);

	/**
	 * Reference for ${mosip.preregistration.document.upload.id} from property file
	 */
	@Value("${mosip.preregistration.document.upload.id}")
	private String uploadId;

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
	public ResponseEntity<MainResponseDTO<?>> databaseerror(final TableNotAccessibleException e) {
		log.info("sessionId", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return GenericUtil.errorResponse(e, e.getMainResposneDTO());
	}
	
	@ExceptionHandler(PreIdInvalidForUserIdException.class)
	public ResponseEntity<MainResponseDTO<?>> invalidUserException(final PreIdInvalidForUserIdException e) {
		return GenericUtil.errorResponse(e, e.getMainresponseDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for DemographicGetDetailsException
	 */
	@ExceptionHandler(DemographicGetDetailsException.class)
	public ResponseEntity<MainResponseDTO<?>> databaseerror(final DemographicGetDetailsException e) {
		log.info("sessionId1", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return GenericUtil.errorResponse(e, e.getResponse());
	}

	/**
	 * @param nv
	 *            pass the exception
	 * @param webRequest
	 *            pass the request
	 * @return response for DocumentNotValidException
	 */
	@ExceptionHandler(DocumentNotValidException.class)
	public ResponseEntity<MainResponseDTO<?>> notValidExceptionhadler(final DocumentNotValidException e) {
		log.info("sessionId2", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return GenericUtil.errorResponse(e, e.getResponse());
	}

	/**
	 * @param nv
	 *            pass the exception
	 * @param webRequest
	 *            pass the request
	 * @return response for DTOMappigException
	 */
	@ExceptionHandler(DTOMappigException.class)
	public ResponseEntity<MainResponseDTO<?>> dtoMappingExc(final DTOMappigException e) {
		log.info("sessionId3", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return GenericUtil.errorResponse(e, e.getResponse());
	}

	/**
	 * @param nv
	 *            pass the exception
	 * @param webRequest
	 *            pass the request
	 * @return response for FileNotFoundException
	 */
	@ExceptionHandler(FileNotFoundException.class)
	public ResponseEntity<MainResponseDTO<?>> fileNotFoundException(final FileNotFoundException e) {
		log.info("sessionId4", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return GenericUtil.errorResponse(e, e.getResponse());
	}

	/**
	 * @param nv
	 *            pass the exception
	 * @param webRequest
	 *            pass the request
	 * @return response for MandatoryFieldNotFoundException
	 */
	@ExceptionHandler(MandatoryFieldNotFoundException.class)
	public ResponseEntity<MainResponseDTO<?>> mandatoryFieldNotFoundException(final MandatoryFieldNotFoundException e) {
		log.info("sessionId5", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return GenericUtil.errorResponse(e, e.getResponse());

	}

	/**
	 * @param nv
	 *            pass the exception
	 * @param webRequest
	 *            pass the request
	 * @return response for ParsingException
	 */
	@ExceptionHandler(ParsingException.class)
	public ResponseEntity<MainResponseDTO<?>> parsingException(final ParsingException e) {
		log.info("sessionId6", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return GenericUtil.errorResponse(e, e.getResponse());

	}

	/**
	 * @param me
	 *            pass the exception
	 * @param webRequest
	 *            pass the request
	 * @return response for MultipartException
	 */
	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<MainResponseDTO<?>> sizeExceedException(final MultipartException e) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_004.toString(),
				DocumentStatusMessages.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList= new ArrayList<>();
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(getCurrentResponseTime());
		errorRes.setId(uploadId);
		errorRes.setVersion(ver);
		log.info("sessionId7", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
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
	public ResponseEntity<MainResponseDTO<?>> documentNotFound(final DocumentNotFoundException e) {
		log.info("sessionId8", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return GenericUtil.errorResponse(e, e.getResponse());
	}
	
	
	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for MasterDataNotAvailableException
	 */
	@ExceptionHandler(MasterDataNotAvailableException.class)
	public ResponseEntity<MainResponseDTO<?>> masterDataNotAvailableException(final MasterDataNotAvailableException e) {
		log.info("sessionId8", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for DocumentSizeExceedException
	 */
	@ExceptionHandler(DocumentSizeExceedException.class)
	public ResponseEntity<MainResponseDTO<?>> documentSizeExceedException(final DocumentSizeExceedException e) {
		log.info("sessionId9", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return GenericUtil.errorResponse(e, e.getResponse());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for DocumentFailedToUploadException
	 */
	@ExceptionHandler(DocumentFailedToUploadException.class)
	public ResponseEntity<MainResponseDTO<?>> documentFailedToUploadException(final DocumentFailedToUploadException e) {
		log.info("sessionId10", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return GenericUtil.errorResponse(e, e.getResponse());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for InvalidRequestParameterException
	 */
	@ExceptionHandler(InvalidRequestParameterException.class)
	public ResponseEntity<MainResponseDTO<?>> invalidRequestParameterException(
			final InvalidRequestParameterException e) {
		log.info("sessionId11", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for DocumentVirusScanException
	 */
	@ExceptionHandler(DocumentVirusScanException.class)
	public ResponseEntity<MainResponseDTO<?>> documentVirusScanException(final DocumentVirusScanException e) {
		log.info("sessionId12", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return GenericUtil.errorResponse(e, e.getResponse());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for DocumentFailedToCopyException
	 */
	@ExceptionHandler(DocumentFailedToCopyException.class)
	public ResponseEntity<MainResponseDTO<?>> documentFailedToCopyException(final DocumentFailedToCopyException e) {
		log.info("sessionId13", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return GenericUtil.errorResponse(e, e.getResponse());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for InvalidDocumnetIdExcepion
	 */
	@ExceptionHandler(InvalidDocumentIdExcepion.class)
	public ResponseEntity<MainResponseDTO<?>> invalidDocumnetIdExcepion(final InvalidDocumentIdExcepion e) {
		log.info("sessionId14", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return GenericUtil.errorResponse(e, e.getResponse());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for CephServerException
	 */
	@ExceptionHandler(FSServerException.class)
	public ResponseEntity<MainResponseDTO<?>> cephServerException(final FSServerException e) {
		log.info("sessionId15", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return GenericUtil.errorResponse(e, e.getResponse());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for PrimaryKeyValidationException
	 */
	@ExceptionHandler(PrimaryKeyValidationException.class)
	public ResponseEntity<MainResponseDTO<?>> primaryKeyValidationException(final PrimaryKeyValidationException e) {
		log.info("sessionId16", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return GenericUtil.errorResponse(e, e.getResponse());
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for FSAdapterException
	 */
//	@ExceptionHandler(FSAdapterException.class)
//	public ResponseEntity<MainResponseDTO> fSAdapterException(final FSAdapterException e, WebRequest request) {
//		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
//		List<ExceptionJSONInfoDTO> errorList= new ArrayList<>();
//		errorList.add(errorDetails);
//		MainResponseDTO<?> errorRes = e.getResponse();
//		errorRes.setErrors(errorList);
//		errorRes.setResponsetime(getCurrentResponseTime());
//		return new ResponseEntity<>(errorRes, HttpStatus.OK);
//	}

	public String getCurrentResponseTime() {
		log.info("sessionId17", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
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
	public ResponseEntity<MainResponseDTO<?>> encryptionFailedException(final EncryptionFailedException e) {
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		e.getValidationErrorList().stream().forEach(serviceError->errorList.add(new ExceptionJSONInfoDTO(serviceError.getErrorCode(),serviceError.getMessage())));
		MainResponseDTO<?> errorRes = e.getMainresponseDTO();
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		log.info("sessionId18", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
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
	public ResponseEntity<MainResponseDTO<?>> decryptionFailedException(final DecryptionFailedException e) {
		log.info("sessionId19", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return GenericUtil.errorResponse(e, e.getMainresponseDTO());
	}
	
	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<MainResponseDTO<?>> DateFormatException(final InvalidFormatException e,WebRequest request){
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_REQ_023.toString(),ErrorMessages.INVALID_REQUEST_DATETIME.getMessage());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		log.info("sessionId20", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> methodArgumentNotValidException(
			final HttpServletRequest httpServletRequest, final MethodArgumentNotValidException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError(io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_015.getCode(),
					x.getField() + ": " + x.getDefaultMessage());
			errorResponse.getErrors().add(error);
		});
		log.info("sessionId21", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onHttpMessageNotReadable(
			final HttpServletRequest httpServletRequest, final HttpMessageNotReadableException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_015.getCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		log.info("sessionId22", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}
	
	


	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultErrorHandler(
			final HttpServletRequest httpServletRequest, Exception e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_016.getCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		log.info("sessionId23", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
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
		log.info("sessionId24", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " );
		return responseWrapper;
	}
}