/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.documents.code.StatusCodes;
import io.mosip.preregistration.documents.dto.DocResponseDTO;
import io.mosip.preregistration.documents.dto.DocumentCopyDTO;
import io.mosip.preregistration.documents.dto.DocumentDTO;
import io.mosip.preregistration.documents.dto.DocumentDeleteDTO;
import io.mosip.preregistration.documents.dto.DocumentGetAllDTO;
import io.mosip.preregistration.documents.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.documents.dto.ResponseDTO;
import io.mosip.preregistration.documents.dto.UploadRequestDTO;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.errorcodes.ErrorMessages;
import io.mosip.preregistration.documents.exception.DocumentFailedToCopyException;
import io.mosip.preregistration.documents.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.documents.exception.DocumentFailedToUploadException;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.exception.DocumentNotValidException;
import io.mosip.preregistration.documents.exception.DocumentSizeExceedException;
import io.mosip.preregistration.documents.exception.DocumentVirusScanException;
import io.mosip.preregistration.documents.exception.MandatoryFieldNotFoundException;
import io.mosip.preregistration.documents.exception.util.DocumentExceptionCatcher;
import io.mosip.preregistration.documents.repository.DocumentRepository;
import io.mosip.preregistration.documents.service.util.DocumentServiceUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;

/**
 * This class provides the service implementation for Document
 * 
 * @author Rajath KR
 * @author Tapaswini Bahera
 * @author Jagadishwari S
 * @author Kishan Rathore
 * @since 1.0.0
 */
/**
 * @author M1046129
 *
 */
/**
 * @author M1046129
 *
 */
/**
 * @author M1046129
 *
 */
@Component
public class DocumentUploadService {

	/**
	 * Autowired reference for {@link #DocumentRepository}
	 */
	@Autowired
	@Qualifier("documentRepository")
	private DocumentRepository documentRepository;

	/**
	 * Reference for ${max.file.size} from property file
	 */
	@Value("${max.file.size}")
	private int maxFileSize;

	/**
	 * Reference for ${file.extension} from property file
	 */
	@Value("${file.extension}")
	private String fileExtension;

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

	/**
	 * Autowired reference for {@link #FilesystemCephAdapterImpl}
	 */
	@Autowired
	private FilesystemCephAdapterImpl ceph;

	/* Logger */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Autowired reference for {@link #VirusScanner}
	 */
	@Autowired
	private VirusScanner<Boolean, String> virusScan;

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
	 * This method acts as a post constructor to initialize the required request
	 * parameters.
	 */
	@PostConstruct
	public void setup() {
		requiredRequestMap.put("id", id);
		requiredRequestMap.put("ver", ver);
	}

	/**
	 * This method is used to upload the document by accepting the JsonString &
	 * MultipartFile
	 * 
	 * @param file
	 * @param documentJsonString
	 * @return ResponseDTO
	 */
	public ResponseDTO<DocResponseDTO> uploadDoucment(MultipartFile file, String documentJsonString) {
		Map<String, String> requestParamMap = new HashMap<>();
		Boolean scanFile = true;
		try {
			UploadRequestDTO<DocumentDTO> docReqDto = serviceUtil.createUploadDto(documentJsonString);
			requestParamMap = serviceUtil.prepareRequestParamMap(docReqDto);
			// scanFile = virusScan.scanDocument(file.getBytes());
			if (ValidationUtil.requestValidator(requestParamMap, requiredRequestMap)) {
				if (scanFile) {
					fileSizeCheck(file.getSize());
					fileExtensionCheck(file);
					return createDoc(docReqDto.getRequest(), file);
				}
			} else {
				throw new DocumentVirusScanException(ErrorCodes.PRG_PAM_DOC_010.toString(),
						ErrorMessages.DOCUMENT_FAILED_IN_VIRUS_SCAN.toString());
			}

		} catch (Exception ex) {
			logger.error(" Exception ", Arrays.toString(ex.getStackTrace()));
			new DocumentExceptionCatcher().handle(ex);
		}

		return null;
	}

	/**
	 * This method checks the size of uploaded file
	 * 
	 * @param uploadedFileSize
	 * @return true if file size is within the limit, else false
	 */
	public DocumentSizeExceedException fileSizeCheck(long uploadedFileSize) {
		long maxAllowedSize = getMaxFileSize();
		if (uploadedFileSize > maxAllowedSize) {
			throw new DocumentSizeExceedException(ErrorCodes.PRG_PAM_DOC_007.toString(),
					ErrorMessages.DOCUMENT_EXCEEDING_PREMITTED_SIZE.toString());
		}
		return null;
	}

	public DocumentNotValidException fileExtensionCheck(MultipartFile file) {
		if (!file.getOriginalFilename().toUpperCase().endsWith(getFileExtension())) {
			throw new DocumentNotValidException(ErrorCodes.PRG_PAM_DOC_004.toString(),
					ErrorMessages.DOCUMENT_INVALID_FORMAT.toString());
		}
		return null;
	}

	/**
	 * This method is used to store the uploaded document into table
	 * 
	 * @param document
	 * @param file
	 * @return ResponseDTO
	 * @throws IOException
	 */
	private ResponseDTO<DocResponseDTO> createDoc(DocumentDTO document, MultipartFile file) throws IOException {
		ResponseDTO<DocResponseDTO> responseDto = new ResponseDTO<>();
		DocResponseDTO docResponseDto;
		List<DocResponseDTO> docResponseDtos = new LinkedList<>();
		if (!isNull(document.getPrereg_id()) && !isNull(document.getStatus_code())
				&& !isNull(document.getDoc_cat_code())) {
			DocumentEntity getentity = documentRepository.findSingleDocument(document.getPrereg_id(),
					document.getDoc_cat_code());
			DocumentEntity documentEntity = serviceUtil.dtoToEntity(document);
			if (getentity != null) {
				documentEntity.setDocumentId(getentity.getDocumentId());
			}
			documentEntity.setDocName(file.getOriginalFilename());

			documentEntity = documentRepository.save(documentEntity);

			if (documentEntity != null) {
				String key = documentEntity.getDocCatCode() + "_" + Integer.toString(documentEntity.getDocumentId());
				System.out.println("Key   " + key);
				ceph.storeFile(documentEntity.getPreregId(), key, file.getInputStream());
				docResponseDto = new DocResponseDTO();
				docResponseDto.setPreRegsitrationId(documentEntity.getPreregId());
				docResponseDto.setDocumnetId(String.valueOf(documentEntity.getDocumentId()));
				docResponseDto.setDocumentName(documentEntity.getDocName());
				docResponseDto.setDocumentCat(documentEntity.getDocCatCode());
				docResponseDto.setDocumentType(documentEntity.getDocTypeCode());
				docResponseDto.setResMsg(StatusCodes.DOCUMENT_UPLOAD_SUCCESSFUL.toString());
				docResponseDtos.add(docResponseDto);
				responseDto.setStatus("true");
				responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
				responseDto.setResponse(docResponseDtos);
			} else {
				throw new DocumentFailedToUploadException(ErrorCodes.PRG_PAM_DOC_009.toString(),
						ErrorMessages.DOCUMENT_FAILED_TO_UPLOAD.toString());
			}

		} else {
			throw new MandatoryFieldNotFoundException(ErrorCodes.PRG_PAM_DOC_014.toString(),
					ErrorMessages.MANDATORY_FIELD_NOT_FOUND.toString());

		}
		return responseDto;
	}

	/**
	 * This method is used to copy the document from source preId to destination
	 * preId
	 * 
	 * @param catCode
	 * @param sourcePreId
	 * @param destinationPreId
	 * @return ResponseDTO
	 */
	public ResponseDTO<DocumentCopyDTO> copyDoucment(String catCode, String sourcePreId, String destinationPreId) {
		ResponseDTO<DocumentCopyDTO> responseDto = new ResponseDTO<>();
		List<DocumentCopyDTO> copyDocumentList = new ArrayList<>();
		try {
			DocumentEntity documentEntity = documentRepository.findSingleDocument(sourcePreId, catCode);
			if (documentEntity == null) {
				throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
			} else {
				DocumentEntity copyDocumentEntity = new DocumentEntity();
				copyDocumentEntity.setPreregId(destinationPreId);
				copyDocumentEntity.setDocName(documentEntity.getDocName());
				copyDocumentEntity.setDocTypeCode(documentEntity.getDocTypeCode());
				copyDocumentEntity.setDocCatCode(documentEntity.getDocCatCode());
				copyDocumentEntity.setDocFileFormat(documentEntity.getDocFileFormat());
				copyDocumentEntity.setCrBy(documentEntity.getCrBy());
				copyDocumentEntity.setUpdBy(documentEntity.getUpdBy());
				copyDocumentEntity.setLangCode(documentEntity.getLangCode());
				copyDocumentEntity.setCrDtime(new Timestamp(System.currentTimeMillis()));
				copyDocumentEntity.setUpdDtime(new Timestamp(System.currentTimeMillis()));
				copyDocumentEntity.setStatusCode(StatusCodes.Pending_Appointment.toString());

				copyDocumentEntity = documentRepository.save(copyDocumentEntity);
				String key1 = documentEntity.getDocCatCode() + "_" + Integer.toString(documentEntity.getDocumentId());
				InputStream sourcefile = ceph.getFile(documentEntity.getPreregId(), key1);
				if (copyDocumentEntity != null) {
					String key2 = copyDocumentEntity.getDocCatCode() + "_"
							+ Integer.toString(copyDocumentEntity.getDocumentId());
					ceph.storeFile(copyDocumentEntity.getPreregId(), key2, sourcefile);
					DocumentCopyDTO copyDcoResDto = new DocumentCopyDTO();
					copyDcoResDto.setSourcePreRegId(sourcePreId);
					copyDcoResDto.setSourceDocumnetId(String.valueOf(documentEntity.getDocumentId()));
					copyDcoResDto.setDestPreRegId(destinationPreId);
					copyDcoResDto.setDestDocumnetId(String.valueOf(copyDocumentEntity.getDocumentId()));
					copyDocumentList.add(copyDcoResDto);
					responseDto.setStatus("true");
					responseDto.setErr(null);
					responseDto.setResponse(copyDocumentList);
					responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
				} else {
					throw new DocumentFailedToCopyException(ErrorCodes.PRG_PAM_DOC_011.toString(),
							ErrorMessages.DOCUMENT_FAILED_TO_COPY.toString());
				}
			}
		} catch (DataAccessLayerException e) {
			// new DocumentExceptionCatcher().handle(DocumentFailedToCopyException.class);
			throw new DocumentFailedToCopyException(ErrorCodes.PRG_PAM_DOC_011.toString(),
					ErrorMessages.DOCUMENT_FAILED_TO_COPY.toString(), e.getCause());
		} catch (Exception ex) {
			logger.error(" Exception ", Arrays.toString(ex.getStackTrace()));
			new DocumentExceptionCatcher().handle(ex);
		}
		return responseDto;
	}

	/**
	 * This method is used to get all the documents for a preId
	 * 
	 * @param preId
	 * @return ResponseDTO
	 */
	public ResponseDTO<DocumentGetAllDTO> getAllDocumentForPreId(String preId) {
		ResponseDTO<DocumentGetAllDTO> responseDto = new ResponseDTO<>();
		List<DocumentGetAllDTO> allDocRes = new ArrayList<>();
		try {
			List<DocumentEntity> documentEntities = documentRepository.findBypreregId(preId);
			if (documentEntities == null) {
				throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
			} else {
				for (DocumentEntity doc : documentEntities) {
					DocumentGetAllDTO allDocDto = new DocumentGetAllDTO();
					allDocDto.setDoc_cat_code(doc.getDocCatCode());
					allDocDto.setDoc_file_format(doc.getDocFileFormat());
					allDocDto.setDoc_name(doc.getDocName());
					allDocDto.setDoc_id(Integer.toString(doc.getDocumentId()));
					allDocDto.setDoc_typ_code(doc.getDocTypeCode());
					String key = doc.getDocCatCode() + "_" + Integer.toString(doc.getDocumentId());
					InputStream file = ceph.getFile(doc.getPreregId(), key);
					allDocDto.setMultipartFile(IOUtils.toByteArray(file));
					allDocDto.setPrereg_id(doc.getPreregId());
					allDocRes.add(allDocDto);
				}
				responseDto.setResponse(allDocRes);
				responseDto.setStatus("true");
				responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
			}
		} catch (Exception e) {
			new DocumentExceptionCatcher().handle(e);

		}
		return responseDto;
	}

	/**
	 * This method is used to check whether the key is null
	 * 
	 * @param key
	 * @return true if key is null, else false
	 */
	public boolean isNull(Object key) {
		if (key instanceof String) {
			if (key.equals(""))
				return true;
		} else if (key instanceof List<?>) {
			if (((List<?>) key).isEmpty())
				return true;
		} else {
			if (key == null)
				return true;
		}
		return false;

	}

	/**
	 * This method is used to delete the document for document Id
	 * 
	 * @param documentId
	 * @return ResponseDTO
	 */
	public ResponseDTO<DocumentDeleteDTO> deleteDocument(String documentId) {
		Integer docId = Integer.parseInt(documentId.trim());
		List<DocumentDeleteDTO> deleteDocList = new ArrayList<>();
		ResponseDTO<DocumentDeleteDTO> delResponseDto = new ResponseDTO<>();
		ExceptionJSONInfoDTO documentErr = null;

		try {
			DocumentEntity documentEntity = documentRepository.findBydocumentId(docId);
			if (documentEntity == null) {
				// documentErr = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_005.toString(),
				// ErrorMessages.DOCUMENT_NOT_PRESENT.toString());
				// delResponseDto.setStatus("false");
				// delResponseDto.setErr(documentErr);
				// delResponseDto.setResTime(new Timestamp(System.currentTimeMillis()));
				throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
			} else {
				int deleteCount = documentRepository.deleteAllBydocumentId(docId);
				if (deleteCount > 0) {
					String key = documentEntity.getDocCatCode() + "_"
							+ Integer.toString(documentEntity.getDocumentId());
					ceph.deleteFile(documentEntity.getPreregId(), key);
					DocumentDeleteDTO deleteDTO = new DocumentDeleteDTO();
					deleteDTO.setDocumnet_Id(documentId);
					deleteDTO.setResMsg(StatusCodes.DOCUMENT_DELETE_SUCCESSFUL.toString());
					deleteDocList.add(deleteDTO);
					delResponseDto.setResponse(deleteDocList);
				}
				delResponseDto.setStatus("true");
				delResponseDto.setErr(null);
				delResponseDto.setResTime(new Timestamp(System.currentTimeMillis()));
			}
		} catch (DataAccessLayerException e) {
			throw new DocumentFailedToDeleteException(ErrorCodes.PRG_PAM_DOC_006.toString(),
					ErrorMessages.DOCUMENT_FAILED_TO_DELETE.toString(), e.getCause());
		} catch (Exception ex) {
			logger.error(" Exception ", Arrays.toString(ex.getStackTrace()));
			new DocumentExceptionCatcher().handle(ex);
		}
		return delResponseDto;
	}

	/**
	 * This method is used to delete all the documents for a preId
	 * 
	 * @param preregId
	 * @return ResponseDTO
	 */
	public ResponseDTO<DocumentDeleteDTO> deleteAllByPreId(String preregId) {
		List<DocumentDeleteDTO> deleteAllList = new ArrayList<>();
		ResponseDTO<DocumentDeleteDTO> delResponseDto = new ResponseDTO<>();

		try {
			List<DocumentEntity> documentEntityList = documentRepository.findBypreregId(preregId);
			if (documentEntityList == null || documentEntityList.isEmpty()) {
				// ExceptionJSONInfoDTO documentErr = new
				// ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_005.toString(),
				// ErrorMessages.DOCUMENT_NOT_PRESENT.toString());
				// delResponseDto.setStatus("false");
				// delResponseDto.setErr(documentErr);
				// delResponseDto.setResTime(new Timestamp(System.currentTimeMillis()));
				throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
			} else {
				int documentEntities = documentRepository.deleteAllBypreregId(preregId);
				System.out.println("All " + documentEntities);
				if (documentEntities > 0) {
					for (DocumentEntity documentEntity : documentEntityList) {
						String key = documentEntity.getDocCatCode() + "_"
								+ Integer.toString(documentEntity.getDocumentId());
						ceph.deleteFile(documentEntity.getPreregId(), key);
						DocumentDeleteDTO deleteDTO = new DocumentDeleteDTO();
						deleteDTO.setDocumnet_Id(String.valueOf(documentEntity.getDocumentId()));
						deleteDTO.setResMsg(StatusCodes.DOCUMENT_DELETE_SUCCESSFUL.toString());
						deleteAllList.add(deleteDTO);
					}
					delResponseDto.setStatus("true");
					delResponseDto.setErr(null);
					delResponseDto.setResponse(deleteAllList);
					delResponseDto.setResTime(new Timestamp(System.currentTimeMillis()));
				}
			}
		} catch (DataAccessLayerException e) {
			throw new DocumentFailedToDeleteException(ErrorCodes.PRG_PAM_DOC_006.toString(),
					ErrorMessages.DOCUMENT_FAILED_TO_DELETE.toString(), e.getCause());
		} catch (Exception ex) {
			logger.error(" Exception ", Arrays.toString(ex.getStackTrace()));
			new DocumentExceptionCatcher().handle(ex);
		}
		return delResponseDto;
	}

	/**
	 * @return maximum file size defined.
	 */
	public long getMaxFileSize() {
		return this.maxFileSize * 1024 * 1024;
	}

	/**
	 * @return defined document extension.
	 */
	public String getFileExtension() {
		return this.fileExtension;
	}

}
