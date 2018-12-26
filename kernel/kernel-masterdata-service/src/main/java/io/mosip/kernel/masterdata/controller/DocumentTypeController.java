package io.mosip.kernel.masterdata.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.ValidDocumentTypeResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.DocumentTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Document type controller with api to get list of valid document types based
 * on document category code type and language code and with api to
 * create,update and delete document types.
 * 
 * 
 * @author Uday Kumar
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "DocumentType" })
public class DocumentTypeController {
	@Autowired
	DocumentTypeService documentTypeService;

	/**
	 * 
	 * @param langCode
	 *            input from user
	 * @param documentCategoryCode
	 *            input from user
	 * @return {@link ValidDocumentTypeResponseDto}}
	 */

	@ApiOperation(value = "Fetch all the  valid doucment type avialbale for specific document category code ")
	@GetMapping("/v1.0/documenttypes/{documentcategorycode}/{langcode}")
	public ValidDocumentTypeResponseDto getDoucmentTypesForDocumentCategoryAndLangCode(
			@PathVariable("langcode") String langCode,
			@PathVariable("documentcategorycode") String documentCategoryCode) {
		List<DocumentTypeDto> validDocumentTypes = documentTypeService.getAllValidDocumentType(documentCategoryCode,
				langCode);
		return new ValidDocumentTypeResponseDto(validDocumentTypes);

	}

	/**
	 * Api to create document type.
	 * 
	 * @param types
	 *            the DTO of document type.
	 * 
	 * @return {@link CodeAndLanguageCodeID }
	 */
	@PostMapping("/v1.0/documenttypes")
	public ResponseEntity<CodeAndLanguageCodeID> createDocumentType(
			@Valid @RequestBody RequestDto<DocumentTypeDto> types) {
		return new ResponseEntity<>(documentTypeService.createDocumentType(types), HttpStatus.CREATED);
	}

	/**
	 * Api to update document type.
	 * 
	 * @param types
	 *            the DTO of document type.
	 * @return {@link CodeAndLanguageCodeID}.
	 */
	@PutMapping("/v1.0/documenttypes")
	@ApiOperation(value = "Service to update document type", response = CodeAndLanguageCodeID.class)
	public ResponseEntity<CodeAndLanguageCodeID> updateDocumentType(
			@ApiParam("Document Type DTO to update") @Valid @RequestBody RequestDto<DocumentTypeDto> types) {
		return new ResponseEntity<>(documentTypeService.updateDocumentType(types), HttpStatus.OK);
	}

	/**
	 * Api to delete document type.
	 * 
	 * @param code
	 *            the document type code.
	 * @return the code.
	 */
	@DeleteMapping("/v1.0/documenttypes/{code}")
	@ApiOperation(value = "Service to delete document type", response = CodeAndLanguageCodeID.class)
	public ResponseEntity<CodeResponseDto> deleteDocumentType(@PathVariable("code") String code) {
		return new ResponseEntity<>(documentTypeService.deleteDocumentType(code), HttpStatus.OK);
	}
}
