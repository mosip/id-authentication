
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
 * Document service
 * 
 * @author M1043008
 *
 */
@Component
public class DocumentUploadService {

	@Autowired
	@Qualifier("documentRepository")
	private DocumentRepository documentRepository;

	@Value("${max.file.size}")
	private int maxFileSize;

	@Value("${file.extension}")
	private String fileExtension;

	@Value("${id}")
	private String id;

	@Value("${ver}")
	private String ver;

	@Autowired
	private FilesystemCephAdapterImpl ceph;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private VirusScanner<Boolean, String> virusScan;

	@Autowired
	private DocumentServiceUtil serviceUtil;

	Map<String, String> requiredRequestMap = new HashMap<>();

	@PostConstruct
	public void setup() {
		requiredRequestMap.put("id", id);
		requiredRequestMap.put("ver", ver);
	}

	public ResponseDTO<DocResponseDTO> uploadDoucment(MultipartFile file, String documentJsonString) {
		Map<String, String> requestParamMap = new HashMap<>();
		Boolean scanFile = true;
		try {
			UploadRequestDTO<DocumentDTO> docReqDto = serviceUtil.createUploadDto(documentJsonString);
			requestParamMap = serviceUtil.prepareRequestParamMap(docReqDto);
			// scanFile = virusScan.scanDocument(file.getBytes());
			if (ValidationUtil.requestValidator(requestParamMap, requiredRequestMap)) {
				if (scanFile) {
					if (fileSizeCheck(file.getSize())) {
						throw new DocumentSizeExceedException(
								ErrorMessages.DOCUMENT_EXCEEDING_PREMITTED_SIZE.toString());
					} else if (!file.getOriginalFilename().toUpperCase().endsWith(getFileExtension())) {
						throw new DocumentNotValidException(ErrorMessages.DOCUMENT_INVALID_FORMAT.toString());
					} else {
						return createDoc(docReqDto.getRequest(), file);
					}
				} else {
					throw new DocumentVirusScanException(ErrorCodes.PRG_PAM_DOC_010.toString(),
							ErrorMessages.DOCUMENT_FAILED_IN_VIRUS_SCAN.toString());
				}
			}
		} catch (Exception ex) {
			logger.error(" Exception ",Arrays.toString(ex.getStackTrace()));
			new DocumentExceptionCatcher().handle(ex);
		}

		return null;
	}

	public boolean fileSizeCheck(long uploadedFileSize) {
		long maxAllowedSize = getMaxFileSize();
		return (uploadedFileSize > maxAllowedSize);
	}

	private ResponseDTO<DocResponseDTO> createDoc(DocumentDTO document, MultipartFile file) throws IOException {
		ResponseDTO<DocResponseDTO> responseDto = new ResponseDTO<>();
		DocResponseDTO docResponseDto;
		List<DocResponseDTO> docResponseDtos = new LinkedList<>();
		if (!isNull(document.getPrereg_id()) && !isNull(document.getStatus_code())&&!isNull(document.getDoc_cat_code())) {
			DocumentEntity getentity = documentRepository.findSingleDocument(document.getPrereg_id(), document.getDoc_cat_code());
			DocumentEntity documentEntity = serviceUtil.dtoToEntity(document);
			if(getentity!=null) {
				documentEntity.setDocumentId(getentity.getDocumentId());
			}
			documentEntity.setDocName(file.getOriginalFilename());
			
			documentEntity = documentRepository.save(documentEntity);

			if (documentEntity != null) {
				String key=documentEntity.getDocCatCode()+"_"+Integer.toString(documentEntity.getDocumentId());
				System.out.println("Key   "+key);
				ceph.storeFile(documentEntity.getPreregId(),key,
						file.getInputStream());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mosip.practice.fileUploader.serviceImpl.DocumentUploadService#uploadFile(
	 * org.springframework.web.multipart.MultipartFile)
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
				String key1=documentEntity.getDocCatCode()+"_"+Integer.toString(documentEntity.getDocumentId());
				InputStream sourcefile = ceph.getFile(documentEntity.getPreregId(),key1);
				if (copyDocumentEntity != null) {
					String key2=copyDocumentEntity.getDocCatCode()+"_"+Integer.toString(copyDocumentEntity.getDocumentId());
					ceph.storeFile(copyDocumentEntity.getPreregId(),key2,
							sourcefile);
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
//			new DocumentExceptionCatcher().handle(DocumentFailedToCopyException.class);
			throw new DocumentFailedToCopyException(ErrorCodes.PRG_PAM_DOC_011.toString(),
					ErrorMessages.DOCUMENT_FAILED_TO_COPY.toString(), e.getCause());
		}catch (Exception ex) {
			logger.error(" Exception ",Arrays.toString(ex.getStackTrace()));
			new DocumentExceptionCatcher().handle(ex);
		}
		return responseDto;
	}

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
					String key=doc.getDocCatCode()+"_"+Integer.toString(doc.getDocumentId());
					InputStream file = ceph.getFile(doc.getPreregId(),key);
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

	public ResponseDTO<DocumentDeleteDTO> deleteDocument(String documentId) {
		Integer docId = Integer.parseInt(documentId.trim());
		List<DocumentDeleteDTO> deleteDocList = new ArrayList<>();
		ResponseDTO<DocumentDeleteDTO> delResponseDto = new ResponseDTO<>();
		ExceptionJSONInfoDTO documentErr = null;

		try {
			DocumentEntity documentEntity = documentRepository.findBydocumentId(docId);
			if (documentEntity == null) {
				documentErr = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_005.toString(),
						ErrorMessages.DOCUMENT_NOT_PRESENT.toString());
				delResponseDto.setStatus("false");
				delResponseDto.setErr(documentErr);
				delResponseDto.setResTime(new Timestamp(System.currentTimeMillis()));
			} else {
				int deleteCount = documentRepository.deleteAllBydocumentId(docId);
				if (deleteCount > 0) {
					String key=documentEntity.getDocCatCode()+"_"+Integer.toString(documentEntity.getDocumentId());
					ceph.deleteFile(documentEntity.getPreregId(),key);
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
		}catch (Exception ex) {
			logger.error(" Exception ",Arrays.toString(ex.getStackTrace()));
			new DocumentExceptionCatcher().handle(ex);
		}
		return delResponseDto;
	}

	public ResponseDTO<DocumentDeleteDTO> deleteAllByPreId(String preregId) {
		List<DocumentDeleteDTO> deleteAllList = new ArrayList<>();
		ResponseDTO<DocumentDeleteDTO> delResponseDto = new ResponseDTO<>();

		try {
			List<DocumentEntity> documentEntityList = documentRepository.findBypreregId(preregId);
			if (documentEntityList == null || documentEntityList.isEmpty()) {
				ExceptionJSONInfoDTO documentErr = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_005.toString(),
						ErrorMessages.DOCUMENT_NOT_PRESENT.toString());
				delResponseDto.setStatus("false");
				delResponseDto.setErr(documentErr);
				delResponseDto.setResTime(new Timestamp(System.currentTimeMillis()));
			} else {
				int documentEntities = documentRepository.deleteAllBypreregId(preregId);
				System.out.println("All "+documentEntities);
				if (documentEntities>0) {
					for (DocumentEntity documentEntity : documentEntityList) {
						String key=documentEntity.getDocCatCode()+"_"+Integer.toString(documentEntity.getDocumentId());
						ceph.deleteFile(documentEntity.getPreregId(),key);
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
		}catch (Exception ex) {
			logger.error(" Exception ",Arrays.toString(ex.getStackTrace()));
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
