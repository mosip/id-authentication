package io.mosip.kernel.masterdata.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.ValidDocumentTypeResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.DocumentTypeService;
import io.swagger.annotations.ApiOperation;

/**
 * Document type controller with api to get list of valid document types based
 * on document category code type and language code and with api to create
 * document types.
 * 
 * 
 * @author Uday Kumar
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RestController
public class DocumentTypeController {
	@Autowired
	DocumentTypeService documentTypeService;

	@ApiOperation(value = "Fetch all the  valid doucment type avialbale for specific document category code ")
	@GetMapping("/documenttypes/{documentcategorycode}/{langcode}")
	public ValidDocumentTypeResponseDto getDoucmentTypesForDocumentCategoryAndLangCode(
			@PathVariable("langcode") String langCode,
			@PathVariable("documentcategorycode") String documentcategoryCode) {
		List<DocumentTypeDto> validDocumentTypes = documentTypeService.getAllValidDocumentType(documentcategoryCode,
				langCode);
		return new ValidDocumentTypeResponseDto(validDocumentTypes);

	}

	@PostMapping("/documenttypes")
	public ResponseEntity<CodeAndLanguageCodeID> createDocumentType(
			@Valid @RequestBody RequestDto<DocumentTypeDto> types) {
		return new ResponseEntity<>(documentTypeService.createDocumentTypes(types), HttpStatus.CREATED);
	}
}
