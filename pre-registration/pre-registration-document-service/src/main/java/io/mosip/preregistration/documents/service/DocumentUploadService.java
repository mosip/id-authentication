package io.mosip.preregistration.documents.service;

import java.util.List;
import java.util.Map;

import javax.print.attribute.IntegerSyntax;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.preregistration.documents.dto.DocumentDto;
import io.mosip.preregistration.documents.entity.DocumentEntity;

/**
 * Document service interface
 * 
 * @author M1043008
 *
 */
@Service
public interface DocumentUploadService {


	/**
	 * @param file
	 * @param documentDto
	 * for individual
	 * @return
	 */
	Map<String, String> uploadDoucment(MultipartFile file, DocumentDto documentDto);
	
	/**
	 * @param file
	 * @param documentDto
	 * for group 
	 *  
	 * @return
	 */
	Map<String, String> copyDoucment(String cat_type, String source, String destination);
	
	/**
	 * @param documentId
	 * @return List of Document entity
	 */
	List<DocumentEntity> getAllDocumentForPreId(String preId);
	
	Boolean deleteDocument(Integer documnetId);
	
	

}