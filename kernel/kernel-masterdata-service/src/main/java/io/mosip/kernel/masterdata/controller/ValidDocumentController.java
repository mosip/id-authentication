package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ValidDocumentDto;
import io.mosip.kernel.masterdata.dto.postresponse.PostValidDocumentResponseDto;
import io.mosip.kernel.masterdata.entity.id.ValidDocumentID;
import io.mosip.kernel.masterdata.service.ValidDocumentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Controller class to create and delete valid document.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "ValidDocument" })
public class ValidDocumentController {

	/**
	 * Reference to ValidDocumentService.
	 */
	@Autowired
	ValidDocumentService documentService;

	/**
	 * Api to create valid document.
	 * 
	 * @param document
	 *            the DTO for valid document.
	 * @return ValidDocumentID.
	 */
	@PostMapping("/v1.0/validdocuments")
	@ApiOperation(value = "Service to create valid document", notes = "Create valid document and return composite id", response = ValidDocumentID.class)
	public ResponseEntity<ValidDocumentID> createValidDocument(
			@Valid @RequestBody RequestDto<ValidDocumentDto> document) {
		return new ResponseEntity<>(documentService.createValidDocument(document), HttpStatus.CREATED);
	}

	/**
	 * Api to delete valid docuemnt.
	 * 
	 * @param docCatCode
	 *            the document category code.
	 * @param docTypeCode
	 *            the document type code.
	 * @return the PostValidDocumentResponseDto.
	 */
	@DeleteMapping("/v1.0/validdocuments/{doccategorycode}/{doctypecode}")
	@ApiOperation(value = "Service to delete valid document", notes = "Delete valid document and return composite id", response = PostValidDocumentResponseDto.class)
	public ResponseEntity<PostValidDocumentResponseDto> deleteValidDocuemnt(
			@PathVariable("doccategorycode") String docCatCode, @PathVariable("doctypecode") String docTypeCode) {
		return new ResponseEntity<>(documentService.deleteValidDocuemnt(docCatCode, docTypeCode), HttpStatus.OK);
	}
}
