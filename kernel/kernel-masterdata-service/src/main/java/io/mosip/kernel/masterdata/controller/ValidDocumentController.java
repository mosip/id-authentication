package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.ValidDocCategoryAndDocTypeResponseDto;
import io.mosip.kernel.masterdata.dto.ValidDocumentDto;
import io.mosip.kernel.masterdata.dto.postresponse.DocCategoryAndTypeResponseDto;
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
	@ResponseFilter
	@PostMapping("/validdocuments")
	@ApiOperation(value = "Service to create valid document", notes = "Create valid document and return composite id")
	public ResponseWrapper<ValidDocumentID> createValidDocument(
			@Valid @RequestBody RequestWrapper<ValidDocumentDto> document) {

		ResponseWrapper<ValidDocumentID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(documentService.createValidDocument(document.getRequest()));
		return responseWrapper;
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
	@ResponseFilter
	@DeleteMapping("/validdocuments/{doccategorycode}/{doctypecode}")
	@ApiOperation(value = "Service to delete valid document", notes = "Delete valid document and return composite id")
	public ResponseWrapper<DocCategoryAndTypeResponseDto> deleteValidDocuemnt(
			@PathVariable("doccategorycode") String docCatCode, @PathVariable("doctypecode") String docTypeCode) {

		ResponseWrapper<DocCategoryAndTypeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(documentService.deleteValidDocuemnt(docCatCode, docTypeCode));
		return responseWrapper;
	}

	/**
	 * Service to fetch all valid document categories and associated document types
	 * for a languagecode
	 * 
	 * @param langCode
	 *            language in which document categories and associated document
	 *            types should be fetch
	 * @return the valid documents
	 */
	@PreAuthorize("hasAnyRole('ZONAL_ADMIN','ZONAL_APPROVER')")
	@ResponseFilter
	@GetMapping("/validdocuments/{languagecode}")
	@ApiOperation(value = "Service to fetch all valid document categories and associated document types for a languagecode")
	public ResponseWrapper<ValidDocCategoryAndDocTypeResponseDto> getValidDocumentByLangCode(
			@PathVariable("languagecode") String langCode) {
		ResponseWrapper<ValidDocCategoryAndDocTypeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(documentService.getValidDocumentByLangCode(langCode));
		return responseWrapper;
	}
}
