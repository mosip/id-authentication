package io.mosip.registration.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.dto.DocumentDto;

/**
 * Document service interface
 * 
 * @author M1043008
 *
 */
@Service
public interface DocumentUploadService {
	/**
	 * 
	 * @param uploaded file
	 * @param documentDto
	 * @return boolean
	 */

	Boolean uploadDoucment(MultipartFile file, DocumentDto documentDto);

}