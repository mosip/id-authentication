/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.documents.code.StatusCodes;
import io.mosip.preregistration.documents.dto.DocumentCopyResponseDTO;
import io.mosip.preregistration.documents.dto.DocumentDeleteResponseDTO;
import io.mosip.preregistration.documents.dto.DocumentMultipartResponseDTO;
import io.mosip.preregistration.documents.dto.DocumentRequestDTO;
import io.mosip.preregistration.documents.dto.DocumentResponseDTO;
import io.mosip.preregistration.documents.dto.MainListResponseDTO;
import io.mosip.preregistration.documents.dto.MainRequestDTO;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.errorcodes.ErrorMessages;
import io.mosip.preregistration.documents.exception.CephServerException;
import io.mosip.preregistration.documents.exception.DocumentFailedToCopyException;
import io.mosip.preregistration.documents.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.documents.exception.DocumentFailedToUploadException;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.exception.DocumentVirusScanException;
import io.mosip.preregistration.documents.exception.MandatoryFieldNotFoundException;
import io.mosip.preregistration.documents.exception.util.DocumentExceptionCatcher;
import io.mosip.preregistration.documents.repository.DocumentRepository;
import io.mosip.preregistration.documents.service.util.DocumentServiceUtil;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;

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
	 * Autowired reference for {@link #DocumentRepository}
	 */
	@Autowired
	@Qualifier("documentRepository")
	private DocumentRepository documentRepository;

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
	@Transactional(propagation = Propagation.REQUIRES_NEW, 
            rollbackFor = Exception.class)
	public MainListResponseDTO<DocumentResponseDTO> uploadDoucment(MultipartFile file, String documentJsonString) {
		MainListResponseDTO<DocumentResponseDTO> responseDto = new MainListResponseDTO<>();
		try {
			MainRequestDTO<DocumentRequestDTO> docReqDto = serviceUtil.createUploadDto(documentJsonString);
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestParamMap(docReqDto), requiredRequestMap)) {
				if (serviceUtil.isVirusScanSuccess(file) && serviceUtil.fileSizeCheck(file.getSize()) && serviceUtil.fileExtensionCheck(file) ) {
					List<DocumentResponseDTO> docResponseDtos = createDoc(docReqDto.getRequest(), file);
					responseDto.setStatus(responseStatus);
					responseDto.setResTime(serviceUtil.getCurrentResponseTime());
					responseDto.setResponse(docResponseDtos);
				}else {
					throw new DocumentVirusScanException(ErrorCodes.PRG_PAM_DOC_010.toString(),
							ErrorMessages.DOCUMENT_FAILED_IN_VIRUS_SCAN.toString());
				}
			} 
		} catch (Exception ex) {
			new DocumentExceptionCatcher().handle(ex);
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
	@Transactional(propagation = Propagation.MANDATORY )
	private List<DocumentResponseDTO> createDoc(DocumentRequestDTO document, MultipartFile file) throws IOException {
		DocumentResponseDTO docResponseDto = new DocumentResponseDTO();
		List<DocumentResponseDTO> docResponseDtos = new LinkedList<>();
		if (!serviceUtil.isNull(document.getPreregId()) && !serviceUtil.isNull(document.getStatusCode())
				&& !serviceUtil.isNull(document.getDocCatCode()) && serviceUtil.callGetPreRegInfoRestService(document.getPreregId())) {
			DocumentEntity getentity = documentRepository.findSingleDocument(document.getPreregId(),
					document.getDocCatCode());
			DocumentEntity documentEntity = serviceUtil.dtoToEntity(document);
			if (getentity != null) {
				documentEntity.setDocumentId(String.valueOf(getentity.getDocumentId()));
			}
			documentEntity.setDocName(file.getOriginalFilename());
			documentEntity = documentRepository.save(documentEntity);
			if (documentEntity != null) {
				String key = documentEntity.getDocCatCode() + "_" + documentEntity.getDocumentId();
				boolean isStoreSuccess = ceph.storeDocument(documentEntity.getPreregId(), key, file.getInputStream());
				if(!isStoreSuccess) {
					throw new CephServerException(ErrorCodes.PRG_PAM_DOC_009.toString(),
							ErrorMessages.DOCUMENT_FAILED_TO_UPLOAD.toString());
				}
				docResponseDto.setPreRegsitrationId(documentEntity.getPreregId());
				docResponseDto.setDocumnetId(String.valueOf(documentEntity.getDocumentId()));
				docResponseDto.setDocumentName(documentEntity.getDocName());
				docResponseDto.setDocumentCat(documentEntity.getDocCatCode());
				docResponseDto.setDocumentType(documentEntity.getDocTypeCode());
				docResponseDto.setResMsg(StatusCodes.DOCUMENT_UPLOAD_SUCCESSFUL.toString());
				docResponseDtos.add(docResponseDto);
			} else {
				throw new DocumentFailedToUploadException(ErrorCodes.PRG_PAM_DOC_009.toString(),
						ErrorMessages.DOCUMENT_FAILED_TO_UPLOAD.toString());
			}
		} else {
			throw new MandatoryFieldNotFoundException(ErrorCodes.PRG_PAM_DOC_014.toString(),
					ErrorMessages.MANDATORY_FIELD_NOT_FOUND.toString());
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
	public MainListResponseDTO<DocumentCopyResponseDTO> copyDoucment(String catCode, String sourcePreId,
			String destinationPreId) {
		MainListResponseDTO<DocumentCopyResponseDTO> responseDto = new MainListResponseDTO<>();
		List<DocumentCopyResponseDTO> copyDocumentList = new ArrayList<>();
		try {
			if (ValidationUtil.isvalidPreRegId(sourcePreId) && ValidationUtil.isvalidPreRegId(destinationPreId)
					&& serviceUtil.isValidCatCode(catCode)) {
				DocumentEntity documentEntity = documentRepository.findSingleDocument(sourcePreId, catCode);
				if (documentEntity != null) {
					DocumentEntity copyDocumentEntity = documentRepository
							.save(serviceUtil.documentEntitySetter(destinationPreId, documentEntity));
					String key1 = documentEntity.getDocCatCode() + "_"
							+ documentEntity.getDocumentId();
					InputStream sourcefile = ceph.getFile(documentEntity.getPreregId(), key1);
					if (copyDocumentEntity != null) {
						String key2 = copyDocumentEntity.getDocCatCode() + "_"
								+ copyDocumentEntity.getDocumentId();
						boolean isStoreSuccess = ceph.storeDocument(copyDocumentEntity.getPreregId(), key2, sourcefile);
						if(!isStoreSuccess) {
							throw new CephServerException(ErrorCodes.PRG_PAM_DOC_009.toString(),
									ErrorMessages.DOCUMENT_FAILED_TO_UPLOAD.toString());
						}
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
						throw new DocumentFailedToCopyException(ErrorCodes.PRG_PAM_DOC_011.toString(),
								ErrorMessages.DOCUMENT_FAILED_TO_COPY.toString());
					}
				} else {
					throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
				}
			}
		} catch (DataAccessLayerException e) {
			throw new DocumentFailedToCopyException(ErrorCodes.PRG_PAM_DOC_011.toString(),
					ErrorMessages.DOCUMENT_FAILED_TO_COPY.toString(), e.getCause());
		} catch (Exception ex) {
			new DocumentExceptionCatcher().handle(ex);
		}
		return responseDto;
	}

	/**
	 * This method is used to get all the documents for a preId
	 * 
	 * @param preId
	 *            pass preRegistrationId
	 * @return ResponseDTO
	 */
	public MainListResponseDTO<DocumentMultipartResponseDTO> getAllDocumentForPreId(String preId) {
		MainListResponseDTO<DocumentMultipartResponseDTO> responseDto = new MainListResponseDTO<>();
		List<DocumentMultipartResponseDTO> allDocRes = new ArrayList<>();
		try {
			if (ValidationUtil.isvalidPreRegId(preId)) {
				List<DocumentEntity> documentEntities = documentRepository.findBypreregId(preId);
				if (documentEntities != null && !documentEntities.isEmpty()) {
					for (DocumentEntity doc : documentEntities) {
						DocumentMultipartResponseDTO allDocDto = new DocumentMultipartResponseDTO();
						allDocDto.setDoc_cat_code(doc.getDocCatCode());
						allDocDto.setDoc_file_format(doc.getDocFileFormat());
						allDocDto.setDoc_name(doc.getDocName());
						allDocDto.setDoc_id(doc.getDocumentId());
						allDocDto.setDoc_typ_code(doc.getDocTypeCode());
						String key = doc.getDocCatCode() + "_" + doc.getDocumentId();
						System.out.println(key);
						InputStream file = ceph.getFile(doc.getPreregId(), key);
						if(file==null) {
							throw new CephServerException(ErrorCodes.PRG_PAM_DOC_005.toString(),
									ErrorMessages.DOCUMENT_FAILED_TO_FETCH.toString());
						}
						allDocDto.setMultipartFile(IOUtils.toByteArray(file));
						allDocDto.setPrereg_id(doc.getPreregId());
						allDocRes.add(allDocDto);
					}
					responseDto.setResponse(allDocRes);
					responseDto.setStatus(responseStatus);
					responseDto.setResTime(serviceUtil.getCurrentResponseTime());
				} else {
					throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
				}
			}
		} catch (Exception e) {
			new DocumentExceptionCatcher().handle(e);
		}
		return responseDto;
	}

	/**
	 * This method is used to delete the document for document Id
	 * 
	 * @param documentId
	 *            pass documentID
	 * @return ResponseDTO
	 */
	public MainListResponseDTO<DocumentDeleteResponseDTO> deleteDocument(String documentId) {
		List<DocumentDeleteResponseDTO> deleteDocList = new ArrayList<>();
		MainListResponseDTO<DocumentDeleteResponseDTO> delResponseDto = new MainListResponseDTO<>();
		try {
			Integer docId = serviceUtil.parseDocumentId(documentId.trim());
			DocumentEntity documentEntity = documentRepository.findBydocumentId(docId);
			if (documentEntity != null) {
				if (documentRepository.deleteAllBydocumentId(docId) > 0) {
					String key = documentEntity.getDocCatCode() + "_"
							+ documentEntity.getDocumentId();
					boolean isDeleted = ceph.deleteFile(documentEntity.getPreregId(), key);
					if(!isDeleted) {
						throw new CephServerException(ErrorCodes.PRG_PAM_DOC_006.toString(),
								ErrorMessages.DOCUMENT_FAILED_TO_DELETE.toString());
					}
					DocumentDeleteResponseDTO deleteDTO = new DocumentDeleteResponseDTO();
					deleteDTO.setDocumnet_Id(documentId);
					deleteDTO.setResMsg(StatusCodes.DOCUMENT_DELETE_SUCCESSFUL.toString());
					deleteDocList.add(deleteDTO);
					delResponseDto.setResponse(deleteDocList);
				}
				delResponseDto.setStatus(responseStatus);
				delResponseDto.setResTime(serviceUtil.getCurrentResponseTime());
			} else {
				throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
			}
		} catch (DataAccessLayerException e) {
			throw new DocumentFailedToDeleteException(ErrorCodes.PRG_PAM_DOC_006.toString(),
					ErrorMessages.DOCUMENT_FAILED_TO_DELETE.toString(), e.getCause());
		} catch (Exception ex) {
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
	public MainListResponseDTO<DocumentDeleteResponseDTO> deleteAllByPreId(String preregId) {
		List<DocumentDeleteResponseDTO> deleteAllList = new ArrayList<>();
		MainListResponseDTO<DocumentDeleteResponseDTO> delResponseDto = new MainListResponseDTO<>();
		try {
			if (ValidationUtil.isvalidPreRegId(preregId)) {
				List<DocumentEntity> documentEntityList = documentRepository.findBypreregId(preregId);
				if (documentEntityList != null && !documentEntityList.isEmpty()) {
					if (documentRepository.deleteAllBypreregId(preregId) > 0) {
						for (DocumentEntity documentEntity : documentEntityList) {
							String key = documentEntity.getDocCatCode() + "_"
									+ documentEntity.getDocumentId();
							ceph.deleteFile(documentEntity.getPreregId(), key);
							DocumentDeleteResponseDTO deleteDTO = new DocumentDeleteResponseDTO();
							deleteDTO.setDocumnet_Id(String.valueOf(documentEntity.getDocumentId()));
							deleteDTO.setResMsg(StatusCodes.DOCUMENT_DELETE_SUCCESSFUL.toString());
							deleteAllList.add(deleteDTO);
						}
						delResponseDto.setResponse(deleteAllList);
						delResponseDto.setStatus(responseStatus);
						delResponseDto.setResTime(serviceUtil.getCurrentResponseTime());
					}
				} else {
					throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
				}
			}
		} catch (DataAccessLayerException e) {
			throw new DocumentFailedToDeleteException(ErrorCodes.PRG_PAM_DOC_006.toString(),
					ErrorMessages.DOCUMENT_FAILED_TO_DELETE.toString(), e.getCause());
		} catch (Exception ex) {
			new DocumentExceptionCatcher().handle(ex);
		}
		return delResponseDto;
	}

}
