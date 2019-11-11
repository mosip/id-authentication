package io.mosip.preregistration.document.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.preregistration.core.common.dto.DocumentDTO;
import io.mosip.preregistration.core.common.dto.DocumentDeleteResponseDTO;
import io.mosip.preregistration.core.common.dto.DocumentsMetaData;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.document.dto.DocumentResponseDTO;

@Service
public interface DocumentServiceIntf {

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
	public MainResponseDTO<DocumentResponseDTO> uploadDocument(MultipartFile file, String documentJsonString,
			String preRegistrationId);

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
	MainResponseDTO<DocumentResponseDTO> copyDocument(String catCode, String sourcePreId, String destinationPreId);

	/**
	 * This method is used to get all the documents for a preId
	 * 
	 * @param preId
	 *            pass preRegistrationId
	 * @return ResponseDTO
	 */
	MainResponseDTO<DocumentsMetaData> getAllDocumentForPreId(String preId);

	/**
	 * This method is used to get particular document for a docId
	 * 
	 * @param docId
	 *            pass documentId
	 * @param preId
	 *            pass preRegistrationId
	 * @return ResponseDTO
	 */
	MainResponseDTO<DocumentDTO> getDocumentForDocId(String docId, String preId);

	/**
	 * This method is used to delete the document for document Id
	 * 
	 * @param documentId
	 *            pass documentID
	 * @return ResponseDTO
	 */
	MainResponseDTO<DocumentDeleteResponseDTO> deleteDocument(String documentId, String preRegistrationId);

	/**
	 * This method is used to delete all the documents for a preId
	 * 
	 * @param preregId
	 *            pass preRegistrationId
	 * @return ResponseDTO
	 */
	MainResponseDTO<DocumentDeleteResponseDTO> deleteAllByPreId(String preregId);

}