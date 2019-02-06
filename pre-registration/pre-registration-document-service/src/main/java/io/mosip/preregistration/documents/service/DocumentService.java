/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.core.code.AuditLogVariables;
import io.mosip.preregistration.core.code.EventId;
import io.mosip.preregistration.core.code.EventName;
import io.mosip.preregistration.core.code.EventType;
import io.mosip.preregistration.core.common.dto.AuditRequestDto;
import io.mosip.preregistration.core.common.dto.DocumentMultipartResponseDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.util.AuditLogUtil;
import io.mosip.preregistration.core.util.CryptoUtil;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.documents.code.DocumentStatusMessages;
import io.mosip.preregistration.documents.dto.DocumentCopyResponseDTO;
import io.mosip.preregistration.documents.dto.DocumentDeleteResponseDTO;
import io.mosip.preregistration.documents.dto.DocumentRequestDTO;
import io.mosip.preregistration.documents.dto.DocumentResponseDTO;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.errorcodes.ErrorMessages;
import io.mosip.preregistration.documents.exception.CephServerException;
import io.mosip.preregistration.documents.exception.DocumentFailedToCopyException;
import io.mosip.preregistration.documents.exception.DocumentFailedToUploadException;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.exception.DocumentVirusScanException;
import io.mosip.preregistration.documents.exception.util.DocumentExceptionCatcher;
import io.mosip.preregistration.documents.repository.util.DocumentDAO;
import io.mosip.preregistration.documents.service.util.DocumentServiceUtil;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;

/**
 * This class provides the service implementation for Document
 * 
 * @author Rajath KR
 * @author Tapaswini Behera
 * @author Jagadishwari S
 * @author Kishan Rathore
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
	 * Reference for ${id} from property file
	 */
	@Value("${id}")
	private String id;

	/**
	 * Reference for ${ver} from property file
	 */
	@Value("${ver}")
	private String ver;

	private boolean responseStatus = true;

	/**
	 * Autowired reference for {@link #FileSystemAdapter}
	 */
	@Autowired
	private FileSystemAdapter<InputStream, Boolean> ceph;

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
		requiredRequestMap.put("id", id);
		requiredRequestMap.put("ver", ver);
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
	public MainListResponseDTO<DocumentResponseDTO> uploadDocument(MultipartFile file, String documentJsonString) {
		log.info("sessionId", "idType", "id", "In uploadDocument method of document service");
		MainListResponseDTO<DocumentResponseDTO> responseDto = new MainListResponseDTO<>();
		boolean isUploadSuccess = false;
		try {
			MainRequestDTO<DocumentRequestDTO> docReqDto = serviceUtil.createUploadDto(documentJsonString);
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestParamMap(docReqDto), requiredRequestMap)) {
				if (serviceUtil.isVirusScanSuccess(file) && serviceUtil.fileSizeCheck(file.getSize())
						&& serviceUtil.fileExtensionCheck(file)) {
					serviceUtil.isValidRequest(docReqDto.getRequest());
					List<DocumentResponseDTO> docResponseDtos = createDoc(docReqDto.getRequest(), file);
					responseDto.setStatus(responseStatus);
					responseDto.setResTime(serviceUtil.getCurrentResponseTime());
					responseDto.setResponse(docResponseDtos);
				} else {
					throw new DocumentVirusScanException(ErrorCodes.PRG_PAM_DOC_010.toString(),
							ErrorMessages.DOCUMENT_FAILED_IN_VIRUS_SCAN.toString());
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
						AuditLogVariables.NO_ID.toString());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Document upload failed & the respective Pre-Registration data save unsuccessfull ",
						AuditLogVariables.NO_ID.toString());
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
	public List<DocumentResponseDTO> createDoc(DocumentRequestDTO document, MultipartFile file) throws IOException {
		log.info("sessionId", "idType", "id", "In createDoc method of document service");
		DocumentResponseDTO docResponseDto = new DocumentResponseDTO();
		List<DocumentResponseDTO> docResponseDtos = new LinkedList<>();
		if (serviceUtil.callGetPreRegInfoRestService(document.getPreregId())) {
			DocumentEntity getentity = documnetDAO.findSingleDocument(document.getPreregId(), document.getDocCatCode());
			DocumentEntity documentEntity = serviceUtil.dtoToEntity(document);
			if (getentity != null) {
				documentEntity.setDocumentId(String.valueOf(getentity.getDocumentId()));
			}
			documentEntity.setDocName(file.getOriginalFilename());
			documentEntity = documnetDAO.saveDocument(documentEntity);
			if (documentEntity != null) {
				String key = documentEntity.getDocCatCode() + "_" + documentEntity.getDocumentId();
				byte[] encryptedDocument = cryptoUtil.encrypt(file.getBytes(), DateUtils.getUTCCurrentDateTime());
				boolean isStoreSuccess = ceph.storeFile(documentEntity.getPreregId(), key,
						new ByteArrayInputStream(encryptedDocument));
				if (!isStoreSuccess) {
					throw new CephServerException(ErrorCodes.PRG_PAM_DOC_009.toString(),
							ErrorMessages.DOCUMENT_FAILED_TO_UPLOAD.toString());
				}
				docResponseDto.setPreRegistrationId(documentEntity.getPreregId());
				docResponseDto.setDocumnetId(String.valueOf(documentEntity.getDocumentId()));
				docResponseDto.setDocumentName(documentEntity.getDocName());
				docResponseDto.setDocumentCat(documentEntity.getDocCatCode());
				docResponseDto.setDocumentType(documentEntity.getDocTypeCode());
				docResponseDto.setResMsg(DocumentStatusMessages.DOCUMENT_UPLOAD_SUCCESSFUL.toString());
				docResponseDtos.add(docResponseDto);
			} else {
				throw new DocumentFailedToUploadException(ErrorCodes.PRG_PAM_DOC_009.toString(),
						ErrorMessages.DOCUMENT_FAILED_TO_UPLOAD.toString());
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
	public MainListResponseDTO<DocumentCopyResponseDTO> copyDocument(String catCode, String sourcePreId,
			String destinationPreId) {
		log.info("sessionId", "idType", "id", "In copyDocument method of document service");
		String sourceBucketName;
		String sourceKey;
		MainListResponseDTO<DocumentCopyResponseDTO> responseDto = new MainListResponseDTO<>();
		List<DocumentCopyResponseDTO> copyDocumentList = new ArrayList<>();
		boolean isCopySuccess = false;
		try {
			if (ValidationUtil.isvalidPreRegId(sourcePreId) && ValidationUtil.isvalidPreRegId(destinationPreId)
					&& serviceUtil.isValidCatCode(catCode)) {
				DocumentEntity documentEntity = documnetDAO.findSingleDocument(sourcePreId, catCode);
				if (documentEntity != null) {
					DocumentEntity copyDocumentEntity = documnetDAO
							.saveDocument(serviceUtil.documentEntitySetter(destinationPreId, documentEntity));
					sourceKey = documentEntity.getDocCatCode() + "_" + documentEntity.getDocumentId();
					sourceBucketName = documentEntity.getPreregId();
					copyFile(copyDocumentEntity, sourceBucketName, sourceKey);
					DocumentCopyResponseDTO copyDcoResDto = new DocumentCopyResponseDTO();
					copyDcoResDto.setSourcePreRegId(sourcePreId);
					copyDcoResDto.setSourceDocumnetId(String.valueOf(documentEntity.getDocumentId()));
					copyDcoResDto.setDestPreRegId(destinationPreId);
					copyDcoResDto.setDestDocumnetId(String.valueOf(copyDocumentEntity.getDocumentId()));
					copyDocumentList.add(copyDcoResDto);
					responseDto.setStatus(responseStatus);
					responseDto.setResTime(serviceUtil.getCurrentResponseTime());
					responseDto.setResponse(copyDocumentList);
				} else {
					throw new DocumentNotFoundException(DocumentStatusMessages.DOCUMENT_IS_MISSING.toString());
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
						AuditLogVariables.MULTIPLE_ID.toString());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Document failed to copy from source PreId to destination PreId ",
						AuditLogVariables.NO_ID.toString());
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
			boolean isStoreSuccess = ceph.copyFile(sourceBucketName, sourceKey, destinationBucketName, destinationKey);
			if (!isStoreSuccess) {
				throw new CephServerException(ErrorCodes.PRG_PAM_DOC_009.toString(),
						ErrorMessages.DOCUMENT_FAILED_TO_UPLOAD.toString());
			}

		} else {
			throw new DocumentFailedToCopyException(ErrorCodes.PRG_PAM_DOC_011.toString(),
					ErrorMessages.DOCUMENT_FAILED_TO_COPY.toString());
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
		boolean isRetrieveSuccess = false;
		try {
			if (ValidationUtil.isvalidPreRegId(preId)) {
				List<DocumentEntity> documentEntities = documnetDAO.findBypreregId(preId);
				responseDto.setResponse(dtoSetter(documentEntities));
				responseDto.setStatus(responseStatus);
				responseDto.setResTime(serviceUtil.getCurrentResponseTime());
			}
			isRetrieveSuccess = true;
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In getAllDocumentForPreId method of document service - " + ex.getMessage());
			new DocumentExceptionCatcher().handle(ex);
		} finally {
			if (isRetrieveSuccess) {
				setAuditValues(EventId.PRE_401.toString(), EventName.RETRIEVE.toString(), EventType.BUSINESS.toString(),
						"Retrieval of document is successfull", AuditLogVariables.MULTIPLE_ID.toString());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Retrieval of document is failed", AuditLogVariables.NO_ID.toString());
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
				allDocDto.setDoc_cat_code(doc.getDocCatCode());
				allDocDto.setDoc_file_format(doc.getDocFileFormat());
				allDocDto.setDoc_name(doc.getDocName());
				allDocDto.setDoc_id(doc.getDocumentId());
				allDocDto.setDoc_typ_code(doc.getDocTypeCode());
				String key = doc.getDocCatCode() + "_" + doc.getDocumentId();
				byte[] cephBytes = IOUtils.toByteArray(ceph.getFile(doc.getPreregId(), key));
				if (cephBytes == null) {
					throw new CephServerException(ErrorCodes.PRG_PAM_DOC_005.toString(),
							ErrorMessages.DOCUMENT_FAILED_TO_FETCH.toString());
				}
				LocalDateTime decryptionDateTime = DateUtils.getUTCCurrentDateTime();

				allDocDto.setMultipartFile(cryptoUtil.decrypt(cephBytes, decryptionDateTime));
				allDocDto.setPrereg_id(doc.getPreregId());
				allDocRes.add(allDocDto);
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
	public MainListResponseDTO<DocumentDeleteResponseDTO> deleteDocument(String documentId) {
		log.info("sessionId", "idType", "id", "In deleteDocument method of document service");
		List<DocumentDeleteResponseDTO> deleteDocList = new ArrayList<>();
		MainListResponseDTO<DocumentDeleteResponseDTO> delResponseDto = new MainListResponseDTO<>();
		try {
			DocumentEntity documentEntity = documnetDAO.findBydocumentId(documentId);
			if (documnetDAO.deleteAllBydocumentId(documentId) > 0) {
				String key = documentEntity.getDocCatCode() + "_" + documentEntity.getDocumentId();
				boolean isDeleted = ceph.deleteFile(documentEntity.getPreregId(), key);
				if (!isDeleted) {
					throw new CephServerException(ErrorCodes.PRG_PAM_DOC_006.toString(),
							ErrorMessages.DOCUMENT_FAILED_TO_DELETE.toString());
				}
				DocumentDeleteResponseDTO deleteDTO = new DocumentDeleteResponseDTO();
				deleteDTO.setDocumnet_Id(documentId);
				deleteDTO.setResMsg(DocumentStatusMessages.DOCUMENT_DELETE_SUCCESSFUL.toString());
				deleteDocList.add(deleteDTO);
				delResponseDto.setResponse(deleteDocList);
			}
			delResponseDto.setStatus(responseStatus);
			delResponseDto.setResTime(serviceUtil.getCurrentResponseTime());
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
		try {
			if (ValidationUtil.isvalidPreRegId(preregId)) {
				List<DocumentEntity> documentEntityList = documnetDAO.findBypreregId(preregId);
				return deleteFile(documentEntityList, preregId);
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
						AuditLogVariables.MULTIPLE_ID.toString());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Document deletion failed", AuditLogVariables.NO_ID.toString());
			}
		}
		return null;
	}

	public MainListResponseDTO<DocumentDeleteResponseDTO> deleteFile(List<DocumentEntity> documentEntityList,
			String preregId) {
		List<DocumentDeleteResponseDTO> deleteAllList = new ArrayList<>();
		MainListResponseDTO<DocumentDeleteResponseDTO> delResponseDto = new MainListResponseDTO<>();
		if (documnetDAO.deleteAllBypreregId(preregId) >= 0) {
			for (DocumentEntity documentEntity : documentEntityList) {
				String key = documentEntity.getDocCatCode() + "_" + documentEntity.getDocumentId();
				ceph.deleteFile(documentEntity.getPreregId(), key);
				DocumentDeleteResponseDTO deleteDTO = new DocumentDeleteResponseDTO();
				deleteDTO.setDocumnet_Id(String.valueOf(documentEntity.getDocumentId()));
				deleteDTO.setResMsg(DocumentStatusMessages.DOCUMENT_DELETE_SUCCESSFUL.toString());
				deleteAllList.add(deleteDTO);
			}
			delResponseDto.setResponse(deleteAllList);
			delResponseDto.setStatus(responseStatus);
			delResponseDto.setResTime(serviceUtil.getCurrentResponseTime());
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
	public void setAuditValues(String eventId, String eventName, String eventType, String description, String idType) {
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

}
