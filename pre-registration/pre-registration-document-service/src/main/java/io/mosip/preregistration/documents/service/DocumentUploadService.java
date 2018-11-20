package io.mosip.preregistration.documents.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;
import io.mosip.preregistration.documents.code.StatusCodes;
import io.mosip.preregistration.documents.dto.DocumentDto;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.DocumentUploadService#uploadDoucment(org.
	 * springframework.web.multipart.MultipartFile,
	 * io.mosip.registration.dto.DocumentDto)
	 */
	// public Map<String, String> uploadDoucment(MultipartFile file, DocumentDto
	// documentDto) {
	//
	// if (file.getSize() > getMaxFileSize()) {
	// throw new
	// DocumentSizeExceedException(StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());
	// }
	//
	// if (!file.getOriginalFilename().toUpperCase().endsWith(getFileExtension())) {
	// throw new
	// DocumentNotValidException(StatusCodes.DOCUMENT_INVALID_FORMAT.toString());
	// }
	//
	// DocumentEntity documentEntity = new DocumentEntity();
	//
	// documentEntity.setPreregId(documentDto.getPrereg_id());
	// documentEntity.setDoc_name(file.getOriginalFilename());
	// documentEntity.setDoc_cat_code(documentDto.getDoc_cat_code());
	// documentEntity.setDoc_typ_code(documentDto.getDoc_typ_code());
	// documentEntity.setDoc_file_format(documentDto.getDoc_file_format());
	// try {
	// documentEntity.setDoc_store(file.getBytes());
	// } catch (IOException e) {
	// logger.error(e.getMessage());
	// }
	// documentEntity.setStatus_code(documentDto.getStatus_code());
	// documentEntity.setLang_code(documentDto.getLang_code());
	// documentEntity.setCr_by(documentDto.getCr_by());
	// documentEntity.setCr_dtimesz(new Timestamp(System.currentTimeMillis()));
	// documentEntity.setUpd_by(documentDto.getUpd_by());
	// documentEntity.setUpd_dtimesz(new Timestamp(System.currentTimeMillis()));
	//
	// DocumentEntity returnentity = documentRepository.save(documentEntity);
	//
	// Map<String, String> response = new HashMap<String, String>();
	// response.put("DocumentId", Integer.toString(returnentity.getDocumentId()));
	// response.put("Status", returnentity.getStatus_code());
	//
	// return response;
	// }

	public ResponseDto<DocumentDto> uploadDoucment(MultipartFile file, DocumentDto documentDto) {
		ResponseDto<DocumentDto> responseDto = new ResponseDto();
		List uploadList = new ArrayList();
		List<ExceptionJSONInfo> err = new ArrayList<>();
		ExceptionJSONInfo DocumentErr = null;
		String status = "true";

		if (file.getSize() > getMaxFileSize()) {

			status = "false";
			DocumentErr = new ExceptionJSONInfo(ErrorCodes.PRG_PAM‌_007.toString(),
					StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());

			throw new DocumentSizeExceedException(StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());

		} else if (!file.getOriginalFilename().toUpperCase().endsWith(getFileExtension())) {

			status = "false";
			DocumentErr = new ExceptionJSONInfo(ErrorCodes.PRG_PAM‌_004.toString(),
					StatusCodes.DOCUMENT_INVALID_FORMAT.toString());

			throw new DocumentNotValidException(StatusCodes.DOCUMENT_INVALID_FORMAT.toString());

		} else {

			status = "true";
			DocumentErr = new ExceptionJSONInfo("", "");
			uploadList.add(StatusCodes.DOCUMENT_UPLOAD_SUCCESSFUL.toString());
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
		documentEntity.setLang_code(documentDto.getLang_code());
		documentEntity.setCr_by(documentDto.getCr_by());
		documentEntity.setCr_dtimesz(new Timestamp(System.currentTimeMillis()));
		documentEntity.setUpd_by(documentDto.getUpd_by());
		documentEntity.setUpd_dtimesz(new Timestamp(System.currentTimeMillis()));

		DocumentEntity returnentity = documentRepository.save(documentEntity);

		responseDto.setStatus(status);
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

	public ResponseDto<DocumentEntity> copyDoucment(String cat_type, String source_preId, String destination_preId) {

		DocumentEntity preIdList = documentRepository.findSingleDocument(source_preId, cat_type);

		ResponseDto<DocumentEntity> responseDto = new ResponseDto<>();
		String status = "true";
		List copyList = new ArrayList();

		List<ExceptionJSONInfo> err = new ArrayList<>();

		ExceptionJSONInfo documentErr = null;
		if (preIdList == null) {

			status = "false";
			documentErr = new ExceptionJSONInfo(ErrorCodes.PRG_PAM‌_006.toString(), "Document failed to copy");
			err.add(documentErr);

			throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
		} else {
			status = "true";
			documentErr = new ExceptionJSONInfo("", "");
			copyList.add(StatusCodes.DOCUMENT_UPLOAD_SUCCESSFUL.toString());
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
		documentEntity.setStatus_code("Draft"); // ?

		DocumentEntity returnEntity = documentRepository.save(documentEntity);

		Map<String, String> response = new HashMap<String, String>();

		response.put("DocumentId", String.valueOf(returnEntity.getDocumentId()));
		response.put("Destination Prid", destination_preId);
		response.put("Status", returnEntity.getStatus_code());

		responseDto.setStatus(status);
		err.add(documentErr);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));

		return responseDto;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.DocumentUploadService#getAllDocumentForPreId(
	 * java.lang.String)
	 */

	// public List<DocumentEntity> getAllDocumentForPreId(String preId) {
	//
	// List<DocumentEntity> documentEntities =
	// documentRepository.findBypreregId(preId);
	//
	// if (documentEntities == null) {
	//
	// throw new
	// DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
	// }
	//
	// return documentEntities;
	// }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResponseDto getAllDocumentForPreId(String preId) {

		ResponseDto responseDto = new ResponseDto<>();
		String status = "true";
		List<ExceptionJSONInfo> err = new ArrayList<>();
		ExceptionJSONInfo DocumentMissingErr = null;
		List<DocumentEntity> documentEntities = documentRepository.findBypreregId(preId);

		if (documentEntities == null) {
			status = "false";
			DocumentMissingErr = new ExceptionJSONInfo(ErrorCodes.PRG_PAM‌_001.toString(),
					StatusCodes.DOCUMENT_IS_MISSING.toString());
			err.add(DocumentMissingErr);
			responseDto.setErr(err);
			throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
		} else {
			status = "true";
			DocumentMissingErr = new ExceptionJSONInfo("", "");
			err.add(DocumentMissingErr);
			responseDto.setErr(err);
			responseDto.setResponse(documentEntities);
		}
		responseDto.setStatus(status);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return responseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.DocumentUploadService#deleteDocument(java.lang.
	 * String)
	 */

	// public Boolean deleteDocument(String doctId) {
	// Integer documnetId = Integer.parseInt(doctId.trim());
	//
	// boolean deleteFlag = false;
	//
	// DocumentEntity documentEntity =
	// documentRepository.findBydocumentId(documnetId);
	//
	// if (documentEntity == null) {
	// throw new
	// DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
	// }
	//
	// else {
	//
	// if (documentRepository.deleteAllBydocumentId(documnetId) > 0) {
	//
	// deleteFlag = true;
	//
	// } else {
	// throw new
	// TablenotAccessibleException(StatusCodes.DOCUMENT_TABLE_NOTACCESSIBLE.toString());
	// }
	//
	// }
	//
	// return deleteFlag;
	// }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseDto deleteDocument(String doctId) {
		Integer documnetId = Integer.parseInt(doctId.toString().trim());
		String status = "true";

		DocumentEntity documentEntity = documentRepository.findBydocumentId(documnetId);

		ResponseDto responseDto = new ResponseDto();
		List deleteList = new ArrayList<>();

		List<ExceptionJSONInfo> err = new ArrayList<>();
		ExceptionJSONInfo documentErr = null;
		if (documentEntity == null) {

			documentErr = new ExceptionJSONInfo(ErrorCodes.PRG_PAM‌_001.toString(),
					StatusCodes.DOCUMENT_IS_MISSING.toString());
			err.add(documentErr);

			throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
		}

		else {

			if (documentRepository.deleteAllBydocumentId(documnetId) > 0) {
				status = "true";
				documentErr = new ExceptionJSONInfo("", "");
				err.add(documentErr);
				deleteList.add(StatusCodes.DOCUMENT_DELETE_SUCCESSFUL);
				responseDto.setResponse(deleteList);

			} else {
				status = "false";
				documentErr = new ExceptionJSONInfo(ErrorCodes.PRG_PAM‌_001.toString(),
						StatusCodes.DOCUMENT_TABLE_NOTACCESSIBLE.toString());
				err.add(documentErr);
				throw new TablenotAccessibleException(StatusCodes.DOCUMENT_TABLE_NOTACCESSIBLE.toString());
			}

		}

		responseDto.setStatus(status);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return responseDto;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResponseDto deleteAllByPreId(String preregId) {
		// TODO Auto-generated method stub

		String status = "true";

		ResponseDto responseDto = new ResponseDto();
		List deleteAllList = new ArrayList<>();

		List<ExceptionJSONInfo> err = new ArrayList<>();
		ExceptionJSONInfo documentErr = null;

		List<DocumentEntity> documentEntityList = documentRepository.findBypreregId(preregId);
		if (documentEntityList == null || documentEntityList.size() == 0) {
			status = "false";
			documentErr = new ExceptionJSONInfo(ErrorCodes.PRG_PAM‌_001.toString(),
					StatusCodes.DOCUMENT_IS_MISSING.toString());
			err.add(documentErr);

			throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
		} else {
			if (documentRepository.deleteAllBypreregId(preregId).size() > 0) {
				status = "true";
				documentErr = new ExceptionJSONInfo("", "");
				err.add(documentErr);
				deleteAllList.add(StatusCodes.DOCUMENT_DELETE_SUCCESSFUL);
				responseDto.setResponse(deleteAllList);

			} else {
				status = "false";
				documentErr = new ExceptionJSONInfo(ErrorCodes.PRG_PAM‌_001.toString(),
						StatusCodes.DOCUMENT_TABLE_NOTACCESSIBLE.toString());
				err.add(documentErr);
				throw new TablenotAccessibleException(StatusCodes.DOCUMENT_TABLE_NOTACCESSIBLE.toString());
			}
		}
		responseDto.setStatus(status);
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

	
public ResponseDto<DocumentEntity> updateDoucment(MultipartFile file, DocumentDto documentDto, String doctId) {
		
		Integer documnetId = Integer.parseInt(doctId.toString().trim());
		ResponseDto<DocumentEntity> responseDto = new ResponseDto();
		List uploadList = new ArrayList();
		List<ExceptionJSONInfo> err = new ArrayList<>();
		ExceptionJSONInfo DocumentErr = null;
		String status = "true";

		if (file.getSize() > getMaxFileSize()) {

			status = "false";
			DocumentErr = new ExceptionJSONInfo(ErrorCodes.PRG_PAM‌_007.toString(),
					StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());

			throw new DocumentSizeExceedException(StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());

		} else if (!file.getOriginalFilename().toUpperCase().endsWith(getFileExtension())) {

			status = "false";
			DocumentErr = new ExceptionJSONInfo(ErrorCodes.PRG_PAM‌_004.toString(),
					StatusCodes.DOCUMENT_INVALID_FORMAT.toString());

			throw new DocumentNotValidException(StatusCodes.DOCUMENT_INVALID_FORMAT.toString());

		} else {

			status = "true";
			DocumentErr = new ExceptionJSONInfo("", "");
			uploadList.add(StatusCodes.DOCUMENT_UPLOAD_SUCCESSFUL.toString());
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
		documentEntity.setLang_code(documentDto.getLang_code());
		
//		documentEntity.setCr_by(documentDto.getCr_by());
//		documentEntity.setCr_dtimesz(new Timestamp(System.currentTimeMillis()));
		
		documentEntity.setUpd_by(documentDto.getUpd_by());
		documentEntity.setUpd_dtimesz(new Timestamp(System.currentTimeMillis()));
		
		documentRepository.deleteAllBydocumentId(documnetId);

		DocumentEntity returnentity = documentRepository.save(documentEntity);

		responseDto.setStatus(status);
		err.add(DocumentErr);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return responseDto;
		
	}
}
