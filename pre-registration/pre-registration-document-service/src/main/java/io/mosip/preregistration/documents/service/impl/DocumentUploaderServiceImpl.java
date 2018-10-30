package io.mosip.preregistration.documents.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
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
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.exception.DocumentNotValidException;
import io.mosip.preregistration.documents.exception.DocumentSizeExceedException;
import io.mosip.preregistration.documents.repository.DocumentRepository;
import io.mosip.preregistration.documents.service.DocumentUploadService;

/**
 * Document service
 * 
 * @author M1043008
 *
 */
@Component
public class DocumentUploaderServiceImpl implements DocumentUploadService {

	private final Logger logger = LoggerFactory.getLogger(DocumentUploaderServiceImpl.class);

	@Autowired
	@Qualifier("documentRepositoery")
	private DocumentRepository documentRepository;


	@Value("${max.file.size}")
	private int maxFileSize;

	@Value("${file.extension}")
	private String fileExtension;
	
	/* (non-Javadoc)
	 * @see io.mosip.registration.service.DocumentUploadService#uploadDoucment(org.springframework.web.multipart.MultipartFile, io.mosip.registration.dto.DocumentDto)
	 */
	@Override
	public Map<String, String> uploadDoucment(MultipartFile file, DocumentDto documentDto) {


		if (file.getSize() > getMaxFileSize()) {
			throw new DocumentSizeExceedException(StatusCodes.DOCUMENT_EXCEEDING_PERMITTED_SIZE.toString());
		}

		if (!file.getOriginalFilename().toUpperCase().endsWith(getFileExtension())) {
			throw new DocumentNotValidException(StatusCodes.DOCUMENT_INVALID_FORMAT.toString());
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

		DocumentEntity returnentity=documentRepository.save(documentEntity);
		
		Map<String, String> response=new HashMap<String,String>();
		response.put("DocumentId",Integer.toString(returnentity.getDocumentId()));
		response.put("Status", returnentity.getStatus_code());
		
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mosip.practice.fileUploader.serviceImpl.DocumentUploadService#uploadFile(
	 * org.springframework.web.multipart.MultipartFile)
	 */
	@Override
	public Map<String, String> copyDoucment(String cat_type, String source_preId, String destination_preId) {

		DocumentEntity preIdList= documentRepository.findSingleDocument(source_preId, cat_type);
		
		
		if(preIdList==null) {
			throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString()); 
		}
		
		DocumentEntity documentEntity=new DocumentEntity();
		
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
		documentEntity.setStatus_code("Draft"); //?

		DocumentEntity returnEntity=documentRepository.save(documentEntity);
		
		Map<String, String> response=new HashMap<String,String>();
		
		response.put("DocumentId", String.valueOf(returnEntity.getDocumentId()));
		response.put("Destination Prid", destination_preId);
		response.put("Status", returnEntity.getStatus_code());

		return response;

	}
	
	/* (non-Javadoc)
	 * @see io.mosip.registration.service.DocumentUploadService#getAllDocumentForPreId(java.lang.String)
	 */
	@Override
	public List<DocumentEntity> getAllDocumentForPreId(String preId) {
		
		List<DocumentEntity> documentEntities=documentRepository.findBypreregId(preId);
		
		if(documentEntities==null) {
			
			throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
		}
		
		return documentEntities;
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.registration.service.DocumentUploadService#deleteDocument(java.lang.String)
	 */
	@Override
	public Boolean deleteDocument(Integer documnetId) {
		
		boolean deleteFlag=false;
		
		DocumentEntity documentEntity=documentRepository.findBydocumentId(documnetId);
		
		if(documentEntity==null) {
			throw new DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
		}
		
		else {
			
			if(documentRepository.deleteAllBydocumentId(documnetId)) {
				
				deleteFlag=true;
				
			}
			else {
				throw new TablenotAccessibleException(StatusCodes.DOCUMENT_TABLE_NOTACCESSIBLE.toString());
			}
			
		}
		
		return deleteFlag;
	}
	
	

	/**
	 * @return maximum file size defined.
	 */
	public long getMaxFileSize() {
		return (this.maxFileSize* 1024 * 1024);
	}

	/**
	 * @return defined document extension.
	 */
	public String getFileExtension() {
		return this.fileExtension;
	}

}
