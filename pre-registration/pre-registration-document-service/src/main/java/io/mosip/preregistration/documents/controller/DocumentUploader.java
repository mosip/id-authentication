
package io.mosip.preregistration.documents.controller;

import org.json.JSONException;
import org.json.JSONObject;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.preregistration.documents.dto.DocResponseDto;
import io.mosip.preregistration.documents.dto.DocumentCopyDTO;
import io.mosip.preregistration.documents.dto.DocumentDeleteDTO;
import io.mosip.preregistration.documents.dto.DocumentDto;
import io.mosip.preregistration.documents.dto.DocumentGetAllDto;
import io.mosip.preregistration.documents.dto.ResponseDto;
import io.mosip.preregistration.documents.service.DocumentUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Document upload controller
 * 
 * @author M1043008
 *
 */
@RestController
@RequestMapping("/v0.1/pre-registration/")
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
	 * @throws JSONException 
	 * @throws MosipJsonParseException
	 * @throws MosipJsonMappingException
	 * @throws MosipIOException
	 */
	@PostMapping(path = "/documents", consumes = { "multipart/form-data" })
	@ApiOperation(value = "Document Upload")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Document uploaded successfully"),
			@ApiResponse(code = 400, message = "Document uploaded failed") })
	public ResponseEntity<ResponseDto<DocResponseDto>> fileUpload(
			@RequestPart(value = "JsonString", required = true) String documentJsonString,
			@RequestPart(value = "file", required = true) MultipartFile file)
			throws JsonParseException, JsonMappingException, IOException, JSONException {
		JSONObject documentData= new JSONObject(documentJsonString);
        JSONObject docDTOData=(JSONObject)documentData.get("request");
		DocumentDto documentDto = (DocumentDto) JsonUtils.jsonStringToJavaObject(DocumentDto.class, docDTOData.toString());
		ResponseDto<DocResponseDto> responseDto = documentUploadService.uploadDoucment(file, documentDto);
		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}

	/**
	 * @param cat_type
	 * @param source_prId
	 * @param destination_preId
	 * @return response in a format specified in API document
	 */
	@PostMapping(path = "/copyDocuments", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Copy uploaded document")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Document successfully copied"),
			@ApiResponse(code = 400, message = "Document copying failed") })
	public ResponseEntity<ResponseDto<DocumentCopyDTO>> copyDocument(@RequestParam String catCode,
			@RequestParam String sourcePrId, @RequestParam String destinationPreId) {
		ResponseDto<DocumentCopyDTO> responseDto = documentUploadService.copyDoucment(catCode, sourcePrId, destinationPreId);
		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}
	
	/**
	 * @param preId
	 * @return response in a format specified in API document
	 */
	@GetMapping(path = "/getDocument", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get All Document for Pre-Registration Id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Documents reterived successfully"),
			@ApiResponse(code = 400, message = "Documents failed to reterive") })
	public ResponseEntity<ResponseDto<DocumentGetAllDto>> getAllDocumentforPreid(@RequestParam String preId) {
		ResponseDto<DocumentGetAllDto> response = documentUploadService.getAllDocumentForPreId(preId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * @param documentId
	 * @return response in a format specified in API document
	 */
	@DeleteMapping(path = "/deleteDocument", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete document by document Id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Document successfully deleted"),
			@ApiResponse(code = 400, message = "Document failed to delete") })
	public ResponseEntity<ResponseDto<DocumentDeleteDTO>> deleteDocument(@RequestParam String documentId) {
		ResponseDto<DocumentDeleteDTO> responseDto = documentUploadService.deleteDocument(documentId);
		return ResponseEntity.status(HttpStatus.OK).body(responseDto);

	}

	/**
	 * @param preId
	 * @return response in a format specified in API document
	 */
	@DeleteMapping(path = "/deleteAllByPreRegId", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete all documents by pre-registration Id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Documents successfully deleted"),
			@ApiResponse(code = 400, message = "Documents failed to delete") })
	public ResponseEntity<ResponseDto<DocumentDeleteDTO>> deleteAllByPreId(@RequestParam String preId) {
		ResponseDto<DocumentDeleteDTO> responseDto = documentUploadService.deleteAllByPreId(preId);
		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}
}
