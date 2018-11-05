package io.mosip.preregistration.documents.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.MosipIOException;
import io.mosip.kernel.core.util.exception.MosipJsonMappingException;
import io.mosip.kernel.core.util.exception.MosipJsonParseException;
import io.mosip.preregistration.documents.code.StatusCodes;
import io.mosip.preregistration.documents.dto.DocumentDto;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.service.DocumentUploadService;
import io.swagger.annotations.Api;

/**
 * Document upload controller
 * 
 * @author M1043008
 *
 */
@RestController
@RequestMapping("/v0.1/pre-registration/registration/")
@Api(tags = "Document Handler")
@CrossOrigin("*")
public class DocumentUploader {

	@Autowired
	private DocumentUploadService documentUploadService;

	@PostMapping(path = "/documents", consumes = {
			"multipart/form-data" })
	@ResponseBody
	public ResponseEntity<Map<String, String>> fileUpload(
			@RequestPart(value = "documentString", required = true) String documentString,
			@RequestPart(value = "file", required = true) MultipartFile file)
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		
		System.out.println("documentString::"+documentString);
		DocumentDto documentDto = (DocumentDto) JsonUtils.jsonStringToJavaObject(DocumentDto.class, documentString);

		Map<String, String> response = documentUploadService.uploadDoucment(file, documentDto);

		return ResponseEntity.status(HttpStatus.OK).body(response);

	}

	@PostMapping(path = "/copy_documents", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, String>> copyDocument(@RequestParam String cat_type, @RequestParam String source_prId,
			@RequestParam String destination_preId) {

		Map<String, String> response = documentUploadService.copyDoucment(cat_type, source_prId, destination_preId);

		return ResponseEntity.status(HttpStatus.OK).body(response);

	}
	
	@GetMapping(path="/get_document",produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<List<DocumentEntity>> getAllDocumentforPreid(@RequestParam String preId){
		
		List<DocumentEntity> response=documentUploadService.getAllDocumentForPreId(preId);
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@DeleteMapping(path="/delete_document",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StatusCodes> deleteDocument(@RequestParam Integer documentId){
		
		documentUploadService.deleteDocument(documentId);
		
		return ResponseEntity.ok().body(StatusCodes.DOCUMENT_DELETE_SUCCESSFUL);
		
	}

}
