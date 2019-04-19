/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.auth.adapter.model.AuthUserDetails;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.core.code.AuditLogVariables;
import io.mosip.preregistration.core.code.EventId;
import io.mosip.preregistration.core.code.EventName;
import io.mosip.preregistration.core.code.EventType;
import io.mosip.preregistration.core.code.RequestCodes;
import io.mosip.preregistration.core.common.dto.AuditRequestDto;
import io.mosip.preregistration.core.common.dto.DocumentDeleteResponseDTO;
import io.mosip.preregistration.core.common.dto.DocumentMultipartResponseDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.exception.HashingException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.AuditLogUtil;
import io.mosip.preregistration.core.util.CryptoUtil;
import io.mosip.preregistration.core.util.HashUtill;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.documents.code.DocumentStatusMessages;
import io.mosip.preregistration.documents.dto.DocumentRequestDTO;
import io.mosip.preregistration.documents.dto.DocumentResponseDTO;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.errorcodes.ErrorMessages;
import io.mosip.preregistration.documents.exception.DocumentFailedToCopyException;
import io.mosip.preregistration.documents.exception.DocumentFailedToUploadException;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.exception.DocumentVirusScanException;
import io.mosip.preregistration.documents.exception.FSServerException;
import io.mosip.preregistration.documents.exception.InvalidDocumentIdExcepion;
import io.mosip.preregistration.documents.exception.util.DocumentExceptionCatcher;
import io.mosip.preregistration.documents.repository.util.DocumentDAO;
import io.mosip.preregistration.documents.service.util.DocumentServiceUtil;

/**
 * This class provides the service implementation for Document
 * 
 * @author Kishan Rathore
 * @author Rajath KR
 * @author Tapaswini Behera
 * @author Jagadishwari S
 * @since 1.0.0
 */
@Component
public class DocumentService {

	/**
	 * Autowired reference for {@link #DocumnetDAO}
	 */
	@Autowired
	private DocumentDAO documnetDAO;

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
	 * Reference for ${mosip.preregistration.document.fetch.metadata.id} from
	 * property file
	 */
	@Value("${mosip.preregistration.document.fetch.metadata.id}")
	private String fetchMetaDataId;

	/**
	 * Reference for ${mosip.preregistration.document.fetch.content.id} from
	 * property file
	 */
	@Value("${mosip.preregistration.document.fetch.content.id}")
	private String fetchContentId;

	/**
	 * Reference for ${mosip.preregistration.document.delete.id} from property file
	 */
	@Value("${mosip.preregistration.document.delete.id}")
	private String deleteId;

	/**
	 * Reference for ${mosip.preregistration.document.delete.specific.id} from
	 * property file
	 */
	@Value("${mosip.preregistration.document.delete.specific.id}")
	private String deleteSpecificId;
	/**
	 * Reference for ${version} from property file
	 */
	@Value("${version}")
	private String ver;

	/**
	 * Autowired reference for {@link #FileSystemAdapter}
	 */
	@Autowired
	private FileSystemAdapter fs;

	/**
	 * Autowired reference for {@link #DocumentServiceUtil}
	 */
	@Autowired
	private DocumentServiceUtil serviceUtil;

	/**
	 * Request map to store the id and version and this is to be passed to request
	 * validator method.
	 */
	Map<String, String> requiredRequestMap = new HashMap<>();

	/**
	 * Autowired reference for {@link #AuditLogUtil}
	 */
	@Autowired
	private AuditLogUtil auditLogUtil;

	@Autowired
	private CryptoUtil cryptoUtil;

	/**
	 * Logger configuration for document service
	 */
	private static Logger log = LoggerConfiguration.logConfig(DocumentService.class);

	/**
	 * This method acts as a post constructor to initialize the required request
	 * parameters.
	 */
	@PostConstruct
	public void setup() {
		requiredRequestMap.put("id", uploadId);
		requiredRequestMap.put("version", ver);
	}

	public AuthUserDetails authUserDetails() {
		return (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	/**
	 * This method is used to upload the document by accepting the JsonString and
	 * MultipartFile
	 * 
	 * @param file
	 *            pass the file
	 * @param documentJsonString
	 *            pass document json
	 * @return ResponseDTO
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public MainListResponseDTO<DocumentResponseDTO> uploadDocument(MultipartFile file, String documentJsonString,
			String preRegistrationId) {
		log.info("sessionId", "idType", "id", "In uploadDocument method of document service");
		MainListResponseDTO<DocumentResponseDTO> responseDto = new MainListResponseDTO<>();
		MainRequestDTO<DocumentRequestDTO> docReqDto = new MainRequestDTO<>();
		boolean isUploadSuccess = false;
		try {
			docReqDto = serviceUtil.createUploadDto(documentJsonString, preRegistrationId);
			responseDto.setId(docReqDto.getId());
			responseDto.setVersion(docReqDto.getVersion());
			if (ValidationUtil.requestValidator(prepareRequestParamMap(docReqDto),requiredRequestMap)) {
				if (serviceUtil.isVirusScanSuccess(file) && serviceUtil.fileSizeCheck(file.getSize())
						&& serviceUtil.fileExtensionCheck(file)) {
					serviceUtil.isValidRequest(docReqDto.getRequest(), preRegistrationId);
					List<DocumentResponseDTO> docResponseDtos = createDoc(docReqDto.getRequest(), file,
							preRegistrationId);
					responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());
					responseDto.setResponse(docResponseDtos);
				} else {
					throw new DocumentVirusScanException(ErrorCodes.PRG_PAM_DOC_010.toString(),
							ErrorMessages.DOCUMENT_FAILED_IN_VIRUS_SCAN.getMessage());
				}
			}
			isUploadSuccess = true;

		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In uploadDoucment method of document service - " + ex.getMessage());
			new DocumentExceptionCatcher().handle(ex);
		} finally {

			if (isUploadSuccess) {
				setAuditValues(EventId.PRE_404.toString(), EventName.UPLOAD.toString(), EventType.BUSINESS.toString(),
						"Document uploaded & the respective Pre-Registration data is saved in the document table",
						AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Document upload failed & the respective Pre-Registration data save unsuccessfull ",
						AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername());
			}
		}

		return responseDto;
	}

	/**
	 * This method is used to store the uploaded document into table
	 * 
	 * @param document
	 *            pass the document
	 * @param file
	 *            pass file
	 * @return ResponseDTO
	 * @throws IOException
	 *             on input errors
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	public List<DocumentResponseDTO> createDoc(DocumentRequestDTO document, MultipartFile file,
			String preRegistrationId) throws IOException {
		log.info("sessionId", "idType", "id", "In createDoc method of document service");
		DocumentResponseDTO docResponseDto = new DocumentResponseDTO();
		List<DocumentResponseDTO> docResponseDtos = new LinkedList<>();
		if (serviceUtil.callGetPreRegInfoRestService(preRegistrationId)) {
			DocumentEntity getentity = documnetDAO.findSingleDocument(preRegistrationId, document.getDocCatCode());
			DocumentEntity documentEntity = serviceUtil.dtoToEntity(file, document,"shashank.agrawal@mindtree.com" /*authUserDetails().getUserId()*/,
					preRegistrationId);
			if (getentity != null) {
				documentEntity.setDocumentId(String.valueOf(getentity.getDocumentId()));
			}
			documentEntity.setDocName(file.getOriginalFilename());
			LocalDateTime encryptedTimestamp = DateUtils.getUTCCurrentDateTime();
			documentEntity.setEncryptedDateTime(encryptedTimestamp);
			byte[] encryptedDocument = cryptoUtil.encrypt(file.getBytes(), encryptedTimestamp);
			documentEntity.setDocHash(HashUtill.hashUtill(encryptedDocument));
			documentEntity = documnetDAO.saveDocument(documentEntity);
			if (documentEntity != null) {
				String key = documentEntity.getDocCatCode() + "_" + documentEntity.getDocumentId();

				boolean isStoreSuccess = fs.storeFile(documentEntity.getPreregId(), key,
						new ByteArrayInputStream(encryptedDocument));

				if (!isStoreSuccess) {
					throw new FSServerException(ErrorCodes.PRG_PAM_DOC_009.toString(),
							ErrorMessages.DOCUMENT_FAILED_TO_UPLOAD.getMessage());
				}
				docResponseDto.setPreRegistrationId(documentEntity.getPreregId());
				docResponseDto.setDocumentId(String.valueOf(documentEntity.getDocumentId()));
				docResponseDto.setDocName(documentEntity.getDocName());
				docResponseDto.setDocCatCode(documentEntity.getDocCatCode());
				docResponseDto.setDocTypCode(documentEntity.getDocTypeCode());
				docResponseDto.setDocFileFormat(FilenameUtils.getExtension(documentEntity.getDocName()));
				docResponseDtos.add(docResponseDto);
			} else {
				throw new DocumentFailedToUploadException(ErrorCodes.PRG_PAM_DOC_009.toString(),
						ErrorMessages.DOCUMENT_FAILED_TO_UPLOAD.getMessage());
			}
		}
		return docResponseDtos;
	}

	/**
	 * This method is used to copy the document from source preId to destination
	 * preId
	 * 
	 * @param catCode
	 *            pass category code
	 * @param sourcePreId
	 *            pass source preRegistrationId
	 * @param destinationPreId
	 *            pass destination preRegistrationId
	 * @return ResponseDTO
	 */
	@Transactional(rollbackFor = Exception.class)
	public MainListResponseDTO<DocumentResponseDTO> copyDocument(String catCode, String sourcePreId,
			String destinationPreId) {
		log.info("sessionId", "idType", "id", "In copyDocument method of document service");
		String sourceBucketName;
		String sourceKey;
		MainListResponseDTO<DocumentResponseDTO> responseDto = new MainListResponseDTO<>();
		responseDto.setId(copyId);
		responseDto.setVersion(ver);
		List<DocumentResponseDTO> copyDocumentList = new ArrayList<>();
		boolean isCopySuccess = false;
		try {
			if (sourcePreId == null || sourcePreId.isEmpty() || destinationPreId == null
					|| destinationPreId.isEmpty()) {
				throw new InvalidRequestParameterException(
						io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_001.toString(),
						io.mosip.preregistration.core.errorcodes.ErrorMessages.MISSING_REQUEST_PARAMETER.getMessage());
			} else if (serviceUtil.isValidCatCode(catCode)) {
				boolean sourceStatus = serviceUtil.callGetPreRegInfoRestService(sourcePreId);
				boolean destinationStatus = serviceUtil.callGetPreRegInfoRestService(destinationPreId);

				DocumentEntity documentEntity = documnetDAO.findSingleDocument(sourcePreId, catCode);
				DocumentEntity destEntity = documnetDAO.findSingleDocument(destinationPreId, catCode);
				if (documentEntity != null && sourceStatus && destinationStatus) {
					DocumentEntity copyDocumentEntity = documnetDAO.saveDocument(
							serviceUtil.documentEntitySetter(destinationPreId, documentEntity, destEntity));
					sourceKey = documentEntity.getDocCatCode() + "_" + documentEntity.getDocumentId();
					sourceBucketName = documentEntity.getPreregId();
					copyFile(copyDocumentEntity, sourceBucketName, sourceKey);
					DocumentResponseDTO documentResponseDTO = new DocumentResponseDTO();
					documentResponseDTO.setPreRegistrationId(destinationPreId);
					documentResponseDTO.setDocumentId(copyDocumentEntity.getDocumentId());
					documentResponseDTO.setDocName(copyDocumentEntity.getDocName());
					documentResponseDTO.setDocCatCode(copyDocumentEntity.getDocCatCode());
					documentResponseDTO.setDocTypCode(copyDocumentEntity.getDocTypeCode());
					documentResponseDTO.setDocFileFormat(copyDocumentEntity.getDocFileFormat());
					copyDocumentList.add(documentResponseDTO);
					responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());
					responseDto.setResponse(copyDocumentList);
				} else {
					throw new DocumentNotFoundException(DocumentStatusMessages.DOCUMENT_IS_MISSING.getMessage());
				}
			}
			isCopySuccess = true;

		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In copyDoucment method of document service - " + ex.getMessage());
			new DocumentExceptionCatcher().handle(ex);
		} finally {
			if (isCopySuccess) {
				setAuditValues(EventId.PRE_409.toString(), EventName.COPY.toString(), EventType.BUSINESS.toString(),
						"Document copied from source PreId to destination PreId is successfully saved in the document table",
						AuditLogVariables.MULTIPLE_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Document failed to copy from source PreId to destination PreId ",
						AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername());
			}
		}
		return responseDto;

	}

	/**
	 * This method will copy the file from sourceFile to destinationFile
	 * 
	 * @param copyDocumentEntity
	 * @param sourceBucketName
	 * @param sourceKey
	 */
	public void copyFile(DocumentEntity copyDocumentEntity, String sourceBucketName, String sourceKey) {
		String destinationBucketName;
		String destinationKey;
		if (copyDocumentEntity != null) {
			destinationBucketName = copyDocumentEntity.getPreregId();
			destinationKey = copyDocumentEntity.getDocCatCode() + "_" + copyDocumentEntity.getDocumentId();
			boolean isStoreSuccess = fs.copyFile(sourceBucketName, sourceKey, destinationBucketName, destinationKey);
			if (!isStoreSuccess) {
				throw new FSServerException(ErrorCodes.PRG_PAM_DOC_009.toString(),
						ErrorMessages.DOCUMENT_FAILED_TO_UPLOAD.getMessage());
			}

		} else {
			throw new DocumentFailedToCopyException(ErrorCodes.PRG_PAM_DOC_011.toString(),
					ErrorMessages.DOCUMENT_FAILED_TO_COPY.getMessage());
		}
	}

	/**
	 * This method is used to get all the documents for a preId
	 * 
	 * @param preId
	 *            pass preRegistrationId
	 * @return ResponseDTO
	 */
	public MainListResponseDTO<DocumentMultipartResponseDTO> getAllDocumentForPreId(String preId) {
		log.info("sessionId", "idType", "id", "In getAllDocumentForPreId method of document service");
		MainListResponseDTO<DocumentMultipartResponseDTO> responseDto = new MainListResponseDTO<>();
		responseDto.setId(fetchMetaDataId);
		responseDto.setVersion(ver);
		boolean isRetrieveSuccess = false;
		Map<String, String> requestParamMap = new HashMap<>();
		try {
			requestParamMap.put(RequestCodes.PRE_REGISTRATION_ID, preId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				List<DocumentEntity> documentEntities = documnetDAO.findBypreregId(preId);
				responseDto.setResponse(dtoSetter(documentEntities));
				responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());
			}
			isRetrieveSuccess = true;

		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In getAllDocumentForPreId method of document service - " + ex.getMessage());
			new DocumentExceptionCatcher().handle(ex);
		} finally {
			if (isRetrieveSuccess) {
				setAuditValues(EventId.PRE_401.toString(), EventName.RETRIEVE.toString(), EventType.BUSINESS.toString(),
						"Retrieval of document is successfull", AuditLogVariables.MULTIPLE_ID.toString(),
						authUserDetails().getUserId(), authUserDetails().getUsername());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Retrieval of document is failed", AuditLogVariables.NO_ID.toString(),
						authUserDetails().getUserId(), authUserDetails().getUsername());
			}
		}
		return responseDto;
	}

	/**
	 * This method will set the document Dto from document entity
	 * 
	 * @param entityList
	 * 
	 * @return List<DocumentMultipartResponseDTO>
	 */
	public List<DocumentMultipartResponseDTO> dtoSetter(List<DocumentEntity> entityList) {
		List<DocumentMultipartResponseDTO> allDocRes = new ArrayList<>();
		try {
			for (DocumentEntity doc : entityList) {
				DocumentMultipartResponseDTO allDocDto = new DocumentMultipartResponseDTO();
				allDocDto.setDocCatCode(doc.getDocCatCode());
				allDocDto.setDocName(doc.getDocName());
				allDocDto.setDocumentId(doc.getDocumentId());
				allDocDto.setDocTypCode(doc.getDocTypeCode());
				String key = doc.getDocCatCode() + "_" + doc.getDocumentId();
				InputStream file = fs.getFile(doc.getPreregId(), key);
				if (file == null) {
					throw new FSServerException(ErrorCodes.PRG_PAM_DOC_005.toString(),
							ErrorMessages.DOCUMENT_FAILED_TO_FETCH.getMessage());
				}
				byte[] cephBytes = IOUtils.toByteArray(file);
				if (doc.getDocHash().equals(HashUtill.hashUtill(cephBytes))) {
					allDocDto.setMultipartFile(cryptoUtil.decrypt(cephBytes, doc.getEncryptedDateTime()));
					allDocRes.add(allDocDto);
				} else {
					log.error("sessionId", "idType", "id", "In dtoSetter method of document service - "
							+ io.mosip.preregistration.core.errorcodes.ErrorMessages.HASHING_FAILED.name());
					throw new HashingException(
							io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_010.name(),
							io.mosip.preregistration.core.errorcodes.ErrorMessages.HASHING_FAILED.name());
				}
			}
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In dtoSetter method of document service - " + ex.getMessage());
			new DocumentExceptionCatcher().handle(ex);
		}
		return allDocRes;
	}

	/**
	 * This method is used to delete the document for document Id
	 * 
	 * @param documentId
	 *            pass documentID
	 * @return ResponseDTO
	 */
	@Transactional(rollbackFor = Exception.class)
	public MainListResponseDTO<DocumentDeleteResponseDTO> deleteDocument(String documentId, String preRegistrationId) {
		log.info("sessionId", "idType", "id", "In deleteDocument method of document service");
		List<DocumentDeleteResponseDTO> deleteDocList = new ArrayList<>();
		MainListResponseDTO<DocumentDeleteResponseDTO> delResponseDto = new MainListResponseDTO<>();
		delResponseDto.setId(deleteSpecificId);
		delResponseDto.setVersion(ver);
		try {
			DocumentEntity documentEntity = documnetDAO.findBydocumentId(documentId);
			if (!documentEntity.getPreregId().equals(preRegistrationId)) {
				throw new InvalidDocumentIdExcepion(ErrorCodes.PRG_PAM_DOC_022.name(),
						ErrorMessages.INVALID_DOCUMENT_ID.getMessage());
			}
			if (documnetDAO.deleteAllBydocumentId(documentId) > 0) {
				String key = documentEntity.getDocCatCode() + "_" + documentEntity.getDocumentId();
				boolean isDeleted = fs.deleteFile(documentEntity.getPreregId(), key);
				if (!isDeleted) {
					throw new FSServerException(ErrorCodes.PRG_PAM_DOC_006.toString(),
							ErrorMessages.DOCUMENT_FAILED_TO_DELETE.getMessage());
				}
				DocumentDeleteResponseDTO deleteDTO = new DocumentDeleteResponseDTO();
				deleteDTO.setMessage(DocumentStatusMessages.DOCUMENT_DELETE_SUCCESSFUL.getMessage());
				deleteDocList.add(deleteDTO);
				delResponseDto.setResponse(deleteDocList);
			}
			delResponseDto.setResponsetime(serviceUtil.getCurrentResponseTime());

		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In deleteDocument method of document service - " + ex.getMessage());
			new DocumentExceptionCatcher().handle(ex);
		}
		return delResponseDto;
	}

	/**
	 * This method is used to delete all the documents for a preId
	 * 
	 * @param preregId
	 *            pass preRegistrationId
	 * @return ResponseDTO
	 */
	@Transactional(rollbackFor = Exception.class)
	public MainListResponseDTO<DocumentDeleteResponseDTO> deleteAllByPreId(String preregId) {
		log.info("sessionId", "idType", "id", "In deleteAllByPreId method of document service");
		boolean isDeleteSuccess = false;
		MainListResponseDTO<DocumentDeleteResponseDTO> deleteRes = new MainListResponseDTO<>();
		deleteRes.setId(deleteId);
		deleteRes.setVersion(ver);
		Map<String, String> requestParamMap = new HashMap<>();
		try {
			requestParamMap.put(RequestCodes.PRE_REGISTRATION_ID, preregId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				List<DocumentEntity> documentEntityList = documnetDAO.findBypreregId(preregId);
				deleteRes = deleteFile(documentEntityList, preregId);
			}

			isDeleteSuccess = true;
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In deleteAllByPreId method of document service - " + ex.getMessage());
			new DocumentExceptionCatcher().handle(ex);
		} finally {
			if (isDeleteSuccess) {
				setAuditValues(EventId.PRE_403.toString(), EventName.DELETE.toString(), EventType.BUSINESS.toString(),
						"Document successfully deleted from the document table",
						AuditLogVariables.MULTIPLE_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Document deletion failed", AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername());
			}
		}
		return deleteRes;
	}

	public MainListResponseDTO<DocumentDeleteResponseDTO> deleteFile(List<DocumentEntity> documentEntityList,
			String preregId) {
		log.info("sessionId", "idType", "id", "In pre-registration service inside delete File method " + preregId);
		List<DocumentDeleteResponseDTO> deleteAllList = new ArrayList<>();
		MainListResponseDTO<DocumentDeleteResponseDTO> delResponseDto = new MainListResponseDTO<>();
		if (documnetDAO.deleteAllBypreregId(preregId) >= 0) {
			for (DocumentEntity documentEntity : documentEntityList) {
				DocumentDeleteResponseDTO deleteDTO = new DocumentDeleteResponseDTO();
				String key = documentEntity.getDocCatCode() + "_" + documentEntity.getDocumentId();
				fs.deleteFile(documentEntity.getPreregId(), key);
				deleteDTO.setMessage(DocumentStatusMessages.ALL_DOCUMENT_DELETE_SUCCESSFUL.getMessage());
				deleteAllList.add(deleteDTO);
			}

			delResponseDto.setResponse(deleteAllList);
			delResponseDto.setResponsetime(serviceUtil.getCurrentResponseTime());
		}

		return delResponseDto;
	}

	/**
	 * This method is used to audit all the document events
	 * 
	 * @param eventId
	 * @param eventName
	 * @param eventType
	 * @param description
	 * @param idType
	 */
	public void setAuditValues(String eventId, String eventName, String eventType, String description, String idType,
			String userId, String userName) {
		AuditRequestDto auditRequestDto = new AuditRequestDto();
		auditRequestDto.setEventId(eventId);
		auditRequestDto.setEventName(eventName);
		auditRequestDto.setEventType(eventType);
		auditRequestDto.setDescription(description);
		auditRequestDto.setId(idType);
		auditRequestDto.setModuleId(AuditLogVariables.DOC.toString());
		auditRequestDto.setModuleName(AuditLogVariables.DOCUMENT_SERVICE.toString());
		auditLogUtil.saveAuditDetails(auditRequestDto);
	}

	/**
	 * This method is used to add the initial request values into a map for input
	 * validations.
	 * 
	 * @param MainRequestDTO
	 *            pass requestDTO
	 * @return a map for request input validation
	 */
	public Map<String, String> prepareRequestParamMap(MainRequestDTO<DocumentRequestDTO> requestDTO) {
		Map<String, String> inputValidation = new HashMap<>();
		inputValidation.put(RequestCodes.ID.toString(), requestDTO.getId());
		inputValidation.put(RequestCodes.VER.toString(), requestDTO.getVersion());
		LocalDate date = requestDTO.getRequesttime().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
		inputValidation.put(RequestCodes.REQ_TIME.toString(), date.toString());
		inputValidation.put(RequestCodes.REQUEST.toString(), requestDTO.getRequest().toString());
		return inputValidation;
	}
}
