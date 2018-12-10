
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
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.kernel.virusscanner.clamav.impl.VirusScannerImpl;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;
import io.mosip.preregistration.documents.code.StatusCodes;
import io.mosip.preregistration.documents.dto.DocResponseDto;
import io.mosip.preregistration.documents.dto.DocumentDto;
import io.mosip.preregistration.documents.dto.DocumentGetAllDto;
import io.mosip.preregistration.documents.dto.ExceptionJSONInfo;
import io.mosip.preregistration.documents.dto.ResponseDto;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.errorcodes.ErrorMessages;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.exception.DocumentNotValidException;
import io.mosip.preregistration.documents.exception.DocumentSizeExceedException;
import io.mosip.preregistration.documents.exception.DocumentVirusScanException;
import io.mosip.preregistration.documents.repository.DocumentRepository;
import xyz.capybara.clamav.commands.scan.Scan;

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

	public ResponseDto<DocResponseDto> uploadDoucment(MultipartFile file, DocumentDto documentDto) {
		ResponseDto<DocResponseDto> responseDto = new ResponseDto<DocResponseDto>();
		DocResponseDto docResponseDto = null;
		DocumentEntity documentEntity = null;
		Boolean scanFile;
		List<DocResponseDto> docResponseDtos = new LinkedList<>();
		try {
			scanFile = virusScan.scanDocument(file.getBytes());
			
			if (scanFile) {
				if (file.getSize() > getMaxFileSize()) {
					throw new DocumentSizeExceedException(ErrorMessages.DOCUMENT_EXCEEDING_PREMITTED_SIZE.toString());
				} else if (!file.getOriginalFilename().toUpperCase().endsWith(getFileExtension())) {
					throw new DocumentNotValidException(ErrorMessages.DOCUMENT_INVALID_FORMAT.toString());
				} else {
					documentEntity = new DocumentEntity();
					documentEntity.setPreregId(documentDto.getPrereg_id());
					documentEntity.setDoc_name(file.getOriginalFilename());
					documentEntity.setDoc_cat_code(documentDto.getDoc_cat_code());
					documentEntity.setDoc_typ_code(documentDto.getDoc_typ_code());
					documentEntity.setDoc_file_format(documentDto.getDoc_file_format());
					documentEntity.setDoc_store(file.getBytes());
					documentEntity.setStatus_code(documentDto.getStatus_code());
					documentEntity.setCr_dtimesz(new Timestamp(System.currentTimeMillis()));
					documentEntity.setUpd_by(documentDto.getUpd_by());
					documentEntity.setUpd_dtimesz(new Timestamp(System.currentTimeMillis()));

					documentEntity = documentRepository.save(documentEntity);

					docResponseDto = new DocResponseDto();
					docResponseDto.setPre_regsitration_id(documentEntity.getPreregId());
					docResponseDto.setDocumnet_Id(String.valueOf(documentEntity.getDocumentId()));
					docResponseDto.setDocument_Name(documentEntity.getDoc_name());
					docResponseDto.setDocument_Cat(documentEntity.getDoc_cat_code());
					docResponseDto.setDocument_Type(documentEntity.getDoc_typ_code());

					docResponseDtos.add(docResponseDto);
					responseDto.setStatus("true");
					responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
					responseDto.setResponse(docResponseDtos);
				}
			} else
				throw new DocumentVirusScanException(ErrorCodes.PRG_PAM_DOC_010.toString(),
						ErrorMessages.DOCUMENT_FAILED_IN_VIRUS_SCAN.toString());
		} catch (IOException e1) {
			e1.printStackTrace();
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

	public ResponseDto<DocResponseDto> copyDoucment(String cat_code, String source_preId, String destination_preId) {

		DocumentEntity preIdList = documentRepository.findSingleDocument(source_preId, cat_code);

		ResponseDto<DocResponseDto> responseDto = new ResponseDto<>();
		List<DocResponseDto> copyList = new ArrayList<>();
		DocResponseDto copyDcoRes = new DocResponseDto();

		List<ExceptionJSONInfo> err = new ArrayList<>();

		ExceptionJSONInfo documentErr = null;
		if (preIdList == null) {
			throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
		} else {
			documentErr = new ExceptionJSONInfo("", "");
			copyDcoRes.setResMsg(StatusCodes.DOCUMENT_UPLOAD_SUCCESSFUL.toString());
			copyList.add(copyDcoRes);
			responseDto.setResponse(copyList);
		}

		DocumentEntity documentEntity = new DocumentEntity();

		documentEntity.setPreregId(destination_preId);
		documentEntity.setDoc_name(preIdList.getDoc_name());
		documentEntity.setDoc_typ_code(preIdList.getDoc_typ_code());
		documentEntity.setDoc_cat_code(preIdList.getDoc_cat_code());
		documentEntity.setDoc_store(preIdList.getDoc_store());
		documentEntity.setDoc_file_format(preIdList.getDoc_file_format());
		documentEntity.setCr_by(preIdList.getCr_by());
		documentEntity.setUpd_by(preIdList.getUpd_by());
		documentEntity.setLang_code(preIdList.getLang_code());
		documentEntity.setCr_dtimesz(new Timestamp(System.currentTimeMillis()));
		documentEntity.setUpd_dtimesz(new Timestamp(System.currentTimeMillis()));
		documentEntity.setStatus_code("Pending_Appointment");
		documentRepository.save(documentEntity);
		responseDto.setStatus("true");
		err.add(documentErr);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));

		return responseDto;

	}

	public ResponseDto<DocumentGetAllDto> getAllDocumentForPreId(String preId) {
		
		ResponseDto<DocumentGetAllDto> responseDto = new ResponseDto<DocumentGetAllDto>();
		List<DocumentGetAllDto> allDocRes = new ArrayList<>();
		try {
			List<DocumentEntity> documentEntities = documentRepository.findBypreregId(preId);
			if (documentEntities == null) {
				throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
			} else {
				for (DocumentEntity doc : documentEntities) {
					DocumentGetAllDto allDocDto = new DocumentGetAllDto();
					allDocDto.setDoc_cat_code(doc.getDoc_cat_code());
					allDocDto.setDoc_file_format(doc.getDoc_file_format());
					allDocDto.setDoc_name(doc.getDoc_name());
					allDocDto.setDoc_id(Integer.toString(doc.getDocumentId()));
					allDocDto.setDoc_typ_code(doc.getDoc_typ_code());
					allDocDto.setMultipartFile(doc.getDoc_store());
					allDocDto.setPrereg_id(doc.getPreregId());
					allDocRes.add(allDocDto);
				}
				responseDto.setResponse(allDocRes);
			}
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(ErrorCodes.PRG_PAM_DOC_012.toString(),
					ErrorMessages.DOCUMENT_TABLE_NOTACCESSIBLE.toString(), e);

		}

		responseDto.setStatus("true");
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return responseDto;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseDto<DocResponseDto> deleteDocument(String doctId) {
		Integer documnetId = Integer.parseInt(doctId.toString().trim());

		DocumentEntity documentEntity = documentRepository.findBydocumentId(documnetId);
		DocResponseDto deletedocRes = new DocResponseDto();
		List<DocResponseDto> deleteDocList = new ArrayList<>();
		ResponseDto<DocResponseDto> responseDto = new ResponseDto();

		List<ExceptionJSONInfo> err = new ArrayList<>();
		ExceptionJSONInfo documentErr = null;
		if (documentEntity == null) {
			throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
		}

		else {

			if (documentRepository.deleteAllBydocumentId(documnetId) > 0) {

				documentErr = new ExceptionJSONInfo("", "");
				err.add(documentErr);
				deletedocRes.setResMsg(StatusCodes.DOCUMENT_DELETE_SUCCESSFUL.toString());
				deleteDocList.add(deletedocRes);
				responseDto.setResponse(deleteDocList);

			} else {

				throw new TablenotAccessibleException(StatusCodes.DOCUMENT_TABLE_NOTACCESSIBLE.toString());
			}

		}

		responseDto.setStatus("true");
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return responseDto;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResponseDto<DocResponseDto> deleteAllByPreId(String preregId) {
		DocResponseDto deleteAllDto = new DocResponseDto();
		List<DocResponseDto> deleteAllList = new ArrayList<>();
		ResponseDto<DocResponseDto> responseDto = new ResponseDto();

		List<ExceptionJSONInfo> err = new ArrayList<>();
		ExceptionJSONInfo documentErr = null;

		List<DocumentEntity> documentEntityList = documentRepository.findBypreregId(preregId);
		if (documentEntityList == null || documentEntityList.size() == 0) {

			throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
		} else {
			if (documentRepository.deleteAllBypreregId(preregId).size() > 0) {

				documentErr = new ExceptionJSONInfo("", "");
				err.add(documentErr);
				deleteAllDto.setResMsg(StatusCodes.DOCUMENT_DELETE_SUCCESSFUL.toString());
				deleteAllList.add(deleteAllDto);
				responseDto.setResponse(deleteAllList);

			} else {

				throw new TablenotAccessibleException(StatusCodes.DOCUMENT_TABLE_NOTACCESSIBLE.toString());
			}
		}
		responseDto.setStatus("true");
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return responseDto;

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
