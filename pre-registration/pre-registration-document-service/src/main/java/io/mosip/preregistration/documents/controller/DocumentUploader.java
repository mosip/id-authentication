/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.controller;

import org.json.JSONException;
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
import io.mosip.preregistration.documents.dto.DocResponseDTO;
import io.mosip.preregistration.documents.dto.DocumentCopyDTO;
import io.mosip.preregistration.documents.dto.DocumentDeleteDTO;
import io.mosip.preregistration.documents.dto.DocumentGetAllDTO;
import io.mosip.preregistration.documents.dto.ResponseDTO;
import io.mosip.preregistration.documents.service.DocumentUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * This class provides different API's to perform operations on
 * Document upload.
 * 
 * @author Rajath KR
 * @author Kishan Rathore
 * @author Tapaswini Behera
 * @author Jagadishwari S
 * @author Ravi C Balaji
 * @since 1.0.0
 */
@RestController
@RequestMapping("/v0.1/pre-registration/")
@Api(tags = "Document Handler")
@CrossOrigin("*")
public class DocumentUploader {
	
	/**
	 * Autowired reference for {@link #DocumentUploadService}
	 */
	@Autowired
	private DocumentUploadService documentUploadService;
	
	/**
	 * Post API to upload the document.
	 * 
	 * @param reqDto pass documentString 
	 * @param file pass files
	 * @return response in a format specified in API document
	 * 
	 */
	@PostMapping(path = "/documents", consumes = { "multipart/form-data" })
	@ApiOperation(value = "Document Upload")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Document uploaded successfully"),
			@ApiResponse(code = 400, message = "Document uploaded failed") })
	public ResponseEntity<ResponseDTO<DocResponseDTO>> fileUpload(
			@RequestPart(value = "Document request DTO", required = true) String reqDto,
			@RequestPart(value = "file", required = true) MultipartFile file) {
		return ResponseEntity.status(HttpStatus.OK).body(documentUploadService.uploadDoucment(file, reqDto));
	}

	/**
	 * Post API to copy the document from source to destination by Preregistration Id
	 * 
	 * @param catCode pass cat_type
	 * @param sourcePrId pass source_prId
	 * @param destinationPreId pass destination_preId
	 * @return response in a format specified in API document
	 */
	@PostMapping(path = "/copyDocuments", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Copy uploaded document")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Document successfully copied"),
			@ApiResponse(code = 400, message = "Document copying failed") })
	public ResponseEntity<ResponseDTO<DocumentCopyDTO>> copyDocument(@RequestParam String catCode,
			@RequestParam String sourcePrId, @RequestParam String destinationPreId) {
		return ResponseEntity.status(HttpStatus.OK).body(documentUploadService.copyDoucment(catCode, sourcePrId, destinationPreId));
	}
	
	/**
	 * Get API to fetch all the documents for a Preregistration Id
	 * 
	 * @param preId pass preRegistrationId
	 * @return response in a format specified in API document
	 */
	@GetMapping(path = "/getDocument", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get All Document for Pre-Registration Id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Documents reterived successfully"),
			@ApiResponse(code = 400, message = "Documents failed to reterive") })
	public ResponseEntity<ResponseDTO<DocumentGetAllDTO>> getAllDocumentforPreid(@RequestParam String preId) {
		return ResponseEntity.status(HttpStatus.OK).body(documentUploadService.getAllDocumentForPreId(preId));
	}

	/**
	 * Delete API to delete the document for a Document Id
	 * 
	 * @param documentId pass documentId
	 * @return response in a format specified in API document
	 */
	@DeleteMapping(path = "/deleteDocument", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete document by document Id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Document successfully deleted"),
			@ApiResponse(code = 400, message = "Document failed to delete") })
	public ResponseEntity<ResponseDTO<DocumentDeleteDTO>> deleteDocument(@RequestParam String documentId) {
		return ResponseEntity.status(HttpStatus.OK).body(documentUploadService.deleteDocument(documentId));

	}

	/**
	 * Delete API to delete all the documents for a preregistrationId 
	 * 
	 * @param preId pass preregistrationId
	 * @return response in a format specified in API document
	 */
	@DeleteMapping(path = "/deleteAllByPreRegId", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete all documents by pre-registration Id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Documents successfully deleted"),
			@ApiResponse(code = 400, message = "Documents failed to delete") })
	public ResponseEntity<ResponseDTO<DocumentDeleteDTO>> deleteAllByPreId(@RequestParam String preId) {
		return ResponseEntity.status(HttpStatus.OK).body(documentUploadService.deleteAllByPreId(preId));
	}
	
	/**
	 *
	 * @return response in a format specified in API document
	 * @throws java.io.IOException 
	 */
//	@GetMapping(path = "/testCeph")
//	@ApiOperation(value = "test Ceph")
//	@ApiResponses(value = { @ApiResponse(code = 200, message = "Documents reterived successfully"),
//			@ApiResponse(code = 400, message = "Documents failed to reterive") })
//	public ResponseEntity<byte[]> getdoc() throws java.io.IOException {
//		ClassLoader classLoader = getClass().getClassLoader();
//		File file = new File(classLoader.getResource("application.properties").getPath());
//		InputStream targetStream = new FileInputStream(file);
//	//	ceph.storePacket("1236", file.getAbsoluteFile());
//     //   ceph.storeFile("1239", file.getName(), targetStream);
//    //    System.out.println("File name "+file.getName());
//		byte[] bytes = IOUtils.toByteArray(ceph.getFile("38163180487193","POA_15"));
//		HttpHeaders responseHeaders = new HttpHeaders();
//		responseHeaders.add("Content-Type", "application/octet-stream");
//		responseHeaders.add("Content-Disposition", "attachment; filename=\"" + file.getName());
//		return new ResponseEntity<byte[]>(bytes,responseHeaders,HttpStatus.OK);
//	}
}
