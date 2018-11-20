
package io.mosip.preregistration.documents.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;
import io.mosip.preregistration.documents.code.StatusCodes;
import io.mosip.preregistration.documents.dto.DocResponseDto;
import io.mosip.preregistration.documents.dto.DocumentDto;
import io.mosip.preregistration.documents.dto.DocumentGetAllDto;
import io.mosip.preregistration.documents.dto.ExceptionJSONInfo;
import io.mosip.preregistration.documents.dto.ResponseDto;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.exception.DocumentNotValidException;
import io.mosip.preregistration.documents.exception.DocumentSizeExceedException;
import io.mosip.preregistration.documents.repository.DocumentRepository;

/**
 * Document service
 * 
 * @author M1043008
 *
 */
@Component
public class DocumentUploadService {

	private final Logger logger = LoggerFactory.getLogger(DocumentUploadService.class);

	@Autowired
	@Qualifier("documentRepositoery")
	private DocumentRepository documentRepository;

	@Value("${max.file.size}")
	private int maxFileSize;

	@Value("${file.extension}")
	private String fileExtension;

	

	public ResponseDto<DocResponseDto> uploadDoucment(MultipartFile file, DocumentDto documentDto) {
		ResponseDto<DocResponseDto> responseDto = new ResponseDto<DocResponseDto>();
		List<DocResponseDto> uploadList = new ArrayList<>();
		DocResponseDto uploadDcoRes= new DocResponseDto();
		List<ExceptionJSONInfo> err = new ArrayList<>();
		ExceptionJSONInfo DocumentErr = null;

		if (file.getSize() > getMaxFileSize()) {
			DocumentErr = new ExceptionJSONInfo(ErrorCodes.PRG_PAM‌_007.toString(),
					StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());

			throw new DocumentSizeExceedException(StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());

		} else if (!file.getOriginalFilename().toUpperCase().endsWith(getFileExtension())) {
			DocumentErr = new ExceptionJSONInfo(ErrorCodes.PRG_PAM‌_004.toString(),
					StatusCodes.DOCUMENT_INVALID_FORMAT.toString());

			throw new DocumentNotValidException(StatusCodes.DOCUMENT_INVALID_FORMAT.toString());

		} else {
			DocumentErr = new ExceptionJSONInfo("", "");
			uploadDcoRes.setResMsg(StatusCodes.DOCUMENT_UPLOAD_SUCCESSFUL.toString());
			uploadList.add(uploadDcoRes);
			responseDto.setResponse(uploadList);
		}

		DocumentEntity documentEntity = new DocumentEntity();

		documentEntity.setPreregId(documentDto.getPrereg_id());
		documentEntity.setDoc_name(file.getOriginalFilename());
		documentEntity.setDoc_cat_code(documentDto.getDoc_cat_code());
		documentEntity.setDoc_typ_code(documentDto.getDoc_typ_code());
		documentEntity.setDoc_file_format(documentDto.getDoc_file_format());
		try {
			documentEntity.setDoc_store(file.getBytes());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		documentEntity.setStatus_code(documentDto.getStatus_code());
		documentEntity.setCr_dtimesz(new Timestamp(System.currentTimeMillis()));
		documentEntity.setUpd_by(documentDto.getUpd_by());
		documentEntity.setUpd_dtimesz(new Timestamp(System.currentTimeMillis()));

		DocumentEntity returnentity = documentRepository.save(documentEntity);

		responseDto.setStatus("true");
		err.add(DocumentErr);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
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
		DocResponseDto copyDcoRes= new DocResponseDto();

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


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResponseDto<DocumentGetAllDto> getAllDocumentForPreId(String preId) {
		DocumentGetAllDto allDocDto= new DocumentGetAllDto();
		ResponseDto<DocumentGetAllDto> responseDto = new ResponseDto<DocumentGetAllDto>();
		List<DocumentGetAllDto> allDocRes= new ArrayList<>();
		List<ExceptionJSONInfo> err = new ArrayList<>();
		ExceptionJSONInfo DocumentMissingErr = null;
		try {
		List<DocumentEntity> documentEntities = documentRepository.findBypreregId(preId);
		if (documentEntities == null) {
			throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
		} else {
			DocumentMissingErr = new ExceptionJSONInfo("", "");
			err.add(DocumentMissingErr);
			responseDto.setErr(err);
			for(DocumentEntity doc:documentEntities) {
				allDocDto.setDoc_cat_code(doc.getDoc_cat_code());
				allDocDto.setDoc_file_format(doc.getDoc_file_format());
				allDocDto.setDoc_id(Integer.toString(doc.getDocumentId()));
				allDocDto.setDoc_typ_code(doc.getDoc_typ_code());
				allDocDto.setMultipartFile(doc.getDoc_store());
				allDocDto.setPrereg_id(doc.getPreregId());
				allDocRes.add(allDocDto);
			}
			responseDto.setResponse(allDocRes);
		}
		}catch (DataAccessLayerException e) {
            //log here
			throw new TablenotAccessibleException(StatusCodes.DOCUMENT_TABLE_NOTACCESSIBLE.toString(), e);

			}

		
		responseDto.setStatus("true");
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return responseDto;
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseDto<DocResponseDto> deleteDocument(String doctId) {
		Integer documnetId = Integer.parseInt(doctId.toString().trim());

		DocumentEntity documentEntity = documentRepository.findBydocumentId(documnetId);
		DocResponseDto deletedocRes= new DocResponseDto();
		List<DocResponseDto> deleteDocList= new ArrayList<>();
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
		DocResponseDto deleteAllDto=new DocResponseDto();
		List<DocResponseDto> deleteAllList= new ArrayList<>();
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
