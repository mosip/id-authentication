/* 
 * Copyright
 * 
 */
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.common.dto.DocumentMultipartResponseDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.documents.dto.DocumentCopyResponseDTO;
import io.mosip.preregistration.documents.dto.DocumentDeleteResponseDTO;
import io.mosip.preregistration.documents.dto.DocumentResponseDTO;
import io.mosip.preregistration.documents.service.DocumentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * This class provides different API's to perform operations on Document upload.
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
public class DocumentController {

	/**
	 * Autowired reference for {@link #DocumentUploadService}
	 */
	@Autowired
	private DocumentService documentUploadService;


	/**
	 * Logger configuration for DocumentController
	 */
	private static Logger log = LoggerConfiguration.logConfig(DocumentController.class);

	/**
	 * Post API to upload the document.
	 * 
	 * @param reqDto
	 *            pass documentString
	 * @param file
	 *            pass files
	 * @return response in a format specified in API document
	 * 
	 */
	@PostMapping(path = "/documents", consumes = { "multipart/form-data" })
	@ApiOperation(value = "Document Upload")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Document uploaded successfully"),
			@ApiResponse(code = 400, message = "Document uploaded failed") })
	public ResponseEntity<MainListResponseDTO<DocumentResponseDTO>> fileUpload(
			@RequestPart(value = "Document request DTO", required = true) String reqDto,
			@RequestPart(value = "file", required = true) MultipartFile file) {

		log.info("sessionId", "idType", "id",
				"In fileUpload method of document controller to upload the document for request " + reqDto);
		return ResponseEntity.status(HttpStatus.OK).body(documentUploadService.uploadDoucment(file, reqDto));
	}

	/**

	 * Post API to copy the document from source to destination by Preregistration
	 * Id
	 * 
	 * @param catCode
	 *            pass cat_type
	 * @param sourcePrId
	 *            pass source_prId
	 * @param destinationPreId
	 *            pass destination_preId
	 * @return response in a format specified in API document
	 */
	@PostMapping(path = "/copyDocuments", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Copy uploaded document")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Document successfully copied"),
			@ApiResponse(code = 400, message = "Document copying failed") })
	public ResponseEntity<MainListResponseDTO<DocumentCopyResponseDTO>> copyDocument(@RequestParam String catCode,
			@RequestParam String sourcePrId, @RequestParam String destinationPreId) {

		log.info("sessionId", "idType", "id",
				"In copyDocument method of document controller to copy the document for request " + catCode + ","
						+ sourcePrId + "," + destinationPreId);
		return ResponseEntity.status(HttpStatus.OK)
				.body(documentUploadService.copyDoucment(catCode, sourcePrId, destinationPreId));
	}

	/**
	 * Get API to fetch all the documents for a Preregistration Id
	 * 
	 * @param pre_registration_id
	 *            pass preRegistrationId
	 * @return response in a format specified in API document
	 */
	@GetMapping(path = "/getDocument", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get All Document for Pre-Registration Id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Documents reterived successfully"),
			@ApiResponse(code = 400, message = "Documents failed to reterive") })

	public ResponseEntity<MainListResponseDTO<DocumentMultipartResponseDTO>> getAllDocumentforPreid(
			@RequestParam String pre_registration_id) {
		log.info("sessionId", "idType", "id",
				"In getAllDocumentforPreid method of document controller to get all the document for pre_registration_id "
						+ pre_registration_id);
		return ResponseEntity.status(HttpStatus.OK)
				.body(documentUploadService.getAllDocumentForPreId(pre_registration_id));
	}

	/**
	 * Delete API to delete the document for a Document Id
	 * 

	 * @param documentId
	 *            pass documentId
	 * @return response in a format specified in API document
	 */
	@DeleteMapping(path = "/deleteDocument", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete document by document Id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Document successfully deleted"),
			@ApiResponse(code = 400, message = "Document failed to delete") })

	public ResponseEntity<MainListResponseDTO<DocumentDeleteResponseDTO>> deleteDocument(
			@RequestParam String documentId) {
		log.info("sessionId", "idType", "id",
				"In deleteDocument method of document controller to delete the document for documentId " + documentId);
		return ResponseEntity.status(HttpStatus.OK).body(documentUploadService.deleteDocument(documentId));

	}

	/**
	 * Delete API to delete all the documents for a preregistrationId
	 * 
	 * @param pre_registration_id
	 *            pass preregistrationId
	 * @return response in a format specified in API document
	 */
	@DeleteMapping(path = "/deleteAllByPreRegId", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete all documents by pre-registration Id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Documents successfully deleted"),
			@ApiResponse(code = 400, message = "Documents failed to delete") })
	public ResponseEntity<MainListResponseDTO<DocumentDeleteResponseDTO>> deleteAllByPreId(
			@RequestParam String pre_registration_id) {
		log.info("sessionId", "idType", "id",
				"In deleteDocument method of document controller to delete all the document for preId "
						+ pre_registration_id);
		return ResponseEntity.status(HttpStatus.OK).body(documentUploadService.deleteAllByPreId(pre_registration_id));
	}
}
