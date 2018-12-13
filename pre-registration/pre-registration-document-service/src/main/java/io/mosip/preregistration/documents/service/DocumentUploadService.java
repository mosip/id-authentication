
package io.mosip.preregistration.documents.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;
import io.mosip.preregistration.documents.code.StatusCodes;
import io.mosip.preregistration.documents.dto.DocResponseDto;
import io.mosip.preregistration.documents.dto.DocumentCopyDTO;
import io.mosip.preregistration.documents.dto.DocumentDeleteDTO;
import io.mosip.preregistration.documents.dto.DocumentDto;
import io.mosip.preregistration.documents.dto.DocumentGetAllDto;
import io.mosip.preregistration.documents.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.documents.dto.ResponseDTO;
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
import io.mosip.preregistration.documents.exception.system.SystemIOException;
import io.mosip.preregistration.documents.repository.DocumentRepository;

/**
 * Document service
 * 
 * @author M1043008
 *
 */
@Component
public class DocumentUploadService {
	@Autowired
	@Qualifier("documentRepositoery")
	private DocumentRepository documentRepository;

	@Value("${max.file.size}")
	private int maxFileSize;

	@Value("${file.extension}")
	private String fileExtension;

	@Autowired
	private VirusScanner<Boolean, String> virusScan;

	public ResponseDTO<DocResponseDto> uploadDoucment(MultipartFile file, DocumentDto documentDto) {
		ResponseDTO<DocResponseDto> responseDto = new ResponseDTO<>();
		DocResponseDto docResponseDto = null;
		DocumentEntity documentEntity = null;
		Boolean scanFile = true;
		List<DocResponseDto> docResponseDtos = new LinkedList<>();
		try {
			//scanFile = virusScan.scanDocument(file.getBytes());

			if (scanFile) {
				if (fileSizeCheck(file.getSize())) {
					throw new DocumentSizeExceedException(ErrorMessages.DOCUMENT_EXCEEDING_PREMITTED_SIZE.toString());
				} else if (!file.getOriginalFilename().toUpperCase().endsWith(getFileExtension())) {
					throw new DocumentNotValidException(ErrorMessages.DOCUMENT_INVALID_FORMAT.toString());
				} else {
					documentEntity = new DocumentEntity();
					documentEntity.setPreregId(documentDto.getPrereg_id());
					documentEntity.setDocName(file.getOriginalFilename());
					documentEntity.setDocCatCode(documentDto.getDoc_cat_code());
					documentEntity.setDocTypeCode(documentDto.getDoc_typ_code());
					documentEntity.setDocFileFormat(documentDto.getDoc_file_format());
					documentEntity.setDocStore(file.getBytes());
					documentEntity.setStatusCode(documentDto.getStatus_code());
					documentEntity.setCrDtime(new Timestamp(System.currentTimeMillis()));
					documentEntity.setUpdBy(documentDto.getUpd_by());
					documentEntity.setUpdDtime(new Timestamp(System.currentTimeMillis()));

					documentEntity = documentRepository.save(documentEntity);

					if (documentEntity != null) {
						docResponseDto = new DocResponseDto();
						docResponseDto.setPreRegsitrationId(documentEntity.getPreregId());
						docResponseDto.setDocumnetId(String.valueOf(documentEntity.getDocumentId()));
						docResponseDto.setDocumentName(documentEntity.getDocName());
						docResponseDto.setDocumentCat(documentEntity.getDocCatCode());
						docResponseDto.setDocumentType(documentEntity.getDocTypeCode());
						docResponseDto.setResMsg("DOCUMENT_UPLOAD_SUCCESSFUL");

						docResponseDtos.add(docResponseDto);
						responseDto.setStatus("true");
						responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
						responseDto.setResponse(docResponseDtos);
					} else {
						throw new DocumentFailedToUploadException(ErrorCodes.PRG_PAM_DOC_009.toString(),
								ErrorMessages.DOCUMENT_FAILED_TO_UPLOAD.toString());
					}
				}
			} else {
				throw new DocumentVirusScanException(ErrorCodes.PRG_PAM_DOC_010.toString(),
						ErrorMessages.DOCUMENT_FAILED_IN_VIRUS_SCAN.toString());
			}
		} catch (DataAccessLayerException e) {
			throw new DocumentFailedToUploadException(ErrorCodes.PRG_PAM_DOC_009.toString(),
					ErrorMessages.DOCUMENT_FAILED_TO_UPLOAD.toString(), e.getCause());
		} catch (IOException e) {
			throw new SystemIOException(ErrorCodes.PRG_PAM_DOC_013.toString(),
					ErrorMessages.DOCUMENT_IO_EXCEPTION.toString());
		}
		return responseDto;
	}

	public boolean fileSizeCheck(long uploadedFileSize) {
		long maxAllowedSize = getMaxFileSize();
		return (uploadedFileSize > maxAllowedSize);
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
				copyDocumentEntity.setDocStore(documentEntity.getDocStore());
				copyDocumentEntity.setDocFileFormat(documentEntity.getDocFileFormat());
				copyDocumentEntity.setCrBy(documentEntity.getCrBy());
				copyDocumentEntity.setUpdBy(documentEntity.getUpdBy());
				copyDocumentEntity.setLangCode(documentEntity.getLangCode());
				copyDocumentEntity.setCrDtime(new Timestamp(System.currentTimeMillis()));
				copyDocumentEntity.setUpdDtime(new Timestamp(System.currentTimeMillis()));
				copyDocumentEntity.setStatusCode("Pending_Appointment");

				copyDocumentEntity = documentRepository.save(copyDocumentEntity);

				if (copyDocumentEntity != null) {
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
				}else {
					throw new DocumentFailedToCopyException(ErrorCodes.PRG_PAM_DOC_011.toString(),
							ErrorMessages.DOCUMENT_FAILED_TO_COPY.toString());
				}
			}
		} catch (DataAccessLayerException e) {
			throw new DocumentFailedToCopyException(ErrorCodes.PRG_PAM_DOC_011.toString(),
					ErrorMessages.DOCUMENT_FAILED_TO_COPY.toString(), e.getCause());
		}
		return responseDto;
	}

	public ResponseDTO<DocumentGetAllDto> getAllDocumentForPreId(String preId) {
		ResponseDTO<DocumentGetAllDto> responseDto = new ResponseDTO<>();
		List<DocumentGetAllDto> allDocRes = new ArrayList<>();
		try {
			List<DocumentEntity> documentEntities = documentRepository.findBypreregId(preId);
			if (documentEntities == null) {
				throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
			} else {
				for (DocumentEntity doc : documentEntities) {
					DocumentGetAllDto allDocDto = new DocumentGetAllDto();
					allDocDto.setDoc_cat_code(doc.getDocCatCode());
					allDocDto.setDoc_file_format(doc.getDocFileFormat());
					allDocDto.setDoc_name(doc.getDocName());
					allDocDto.setDoc_id(Integer.toString(doc.getDocumentId()));
					allDocDto.setDoc_typ_code(doc.getDocTypeCode());
					allDocDto.setMultipartFile(doc.getDocStore());
					allDocDto.setPrereg_id(doc.getPreregId());
					allDocRes.add(allDocDto);
				}
				responseDto.setResponse(allDocRes);
				responseDto.setStatus("true");
				responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
			}
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(ErrorCodes.PRG_PAM_DOC_012.toString(),
					ErrorMessages.DOCUMENT_TABLE_NOTACCESSIBLE.toString(), e);

		}
		return responseDto;
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
		}
		return delResponseDto;
	}

	public ResponseDTO<DocumentDeleteDTO> deleteAllByPreId(String preregId) {
		List<DocumentDeleteDTO> deleteAllList = new ArrayList<>();
		ResponseDTO<DocumentDeleteDTO> delResponseDto = new ResponseDTO<>();

		try {
			System.out.println("PreId Req for doc delete::"+preregId);
			List<DocumentEntity> documentEntityList = documentRepository.findBypreregId(preregId);
			if (documentEntityList == null || documentEntityList.isEmpty()) {
				ExceptionJSONInfoDTO documentErr = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_DOC_005.toString(),
						ErrorMessages.DOCUMENT_NOT_PRESENT.toString());
				delResponseDto.setStatus("false");
				delResponseDto.setErr(documentErr);
				delResponseDto.setResTime(new Timestamp(System.currentTimeMillis()));
			} else {
				List<DocumentEntity> documentEntities = documentRepository.deleteAllBypreregId(preregId);
				if (!documentEntities.isEmpty()) {
					for (DocumentEntity documentEntity : documentEntities) {
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
		}
		return delResponseDto;
	}

	/**
	 * @return maximum file size defined.
	 */
	public long getMaxFileSize() {
		return (this.maxFileSize * 1024 * 1024);
	}

	/**
	 * @return defined document extension.
	 */
	public String getFileExtension() {
		return this.fileExtension;
	}

}
