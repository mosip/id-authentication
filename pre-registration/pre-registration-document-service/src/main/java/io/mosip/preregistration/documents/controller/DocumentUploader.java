package io.mosip.preregistration.documents.controller;

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

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.preregistration.documents.dto.DocumentDto;
import io.mosip.preregistration.documents.dto.ResponseDto;
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

	/**
	 * @param documentString
	 * @param file
	 * @return response in a format specified in API document
	 * @throws IOException 
	 * @throws MosipJsonParseException
	 * @throws MosipJsonMappingException
	 * @throws MosipIOException
	 */
	@PostMapping(path = "/documents", consumes = { "multipart/form-data" })
	@ResponseBody
	public ResponseEntity<ResponseDto<DocumentDto>> fileUpload(
			@RequestPart(value = "documentString", required = true) String documentString,
			@RequestPart(value = "file", required = true) MultipartFile file)
			throws JsonParseException, JsonMappingException, IOException {

		ResponseDto<DocumentDto> responseDto = new ResponseDto<DocumentDto>();

		System.out.println("documentString::" + documentString);
		DocumentDto documentDto = (DocumentDto) JsonUtils.jsonStringToJavaObject(DocumentDto.class, documentString);

		responseDto = documentUploadService.uploadDoucment(file, documentDto);

		return ResponseEntity.status(HttpStatus.OK).body(responseDto);

	}

	/**
	 * @param cat_type
	 * @param source_prId
	 * @param destination_preId
	 * @return response in a format specified in API document
	 */
	@SuppressWarnings("rawtypes")
	@PostMapping(path = "/copy_documents", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<ResponseDto> copyDocument(@RequestParam String cat_type,
			@RequestParam String source_prId, @RequestParam String destination_preId) {
		ResponseDto<DocumentEntity> responseDto = new ResponseDto<DocumentEntity>();
		responseDto = documentUploadService.copyDoucment(cat_type, source_prId, destination_preId);

		return ResponseEntity.status(HttpStatus.OK).body(responseDto);

	}
	
	/**
	 * @param preId
	 * @return response in a format specified in API document
	 */
	@SuppressWarnings("rawtypes")
	@GetMapping(path = "/get_document", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<ResponseDto> getAllDocumentforPreid(@RequestParam String preId) {

		ResponseDto response = documentUploadService.getAllDocumentForPreId(preId);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * @param documentId
	 * @return response in a format specified in API document
	 */
	@SuppressWarnings("rawtypes")
	@DeleteMapping(path = "/delete_document", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDto> deleteDocument(@RequestParam String documentId) {
		ResponseDto responseDto = documentUploadService.deleteDocument(documentId);
		return ResponseEntity.status(HttpStatus.OK).body(responseDto);

	}

	/**
	 * @param preId
	 * @return response in a format specified in API document
	 */
	@SuppressWarnings("rawtypes")
	@DeleteMapping(path = "/deleteAllByPreRegId", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity deleteAllByPreId(@RequestParam String preId) {
		ResponseDto responseDto = documentUploadService.deleteAllByPreId(preId);
		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}
}
