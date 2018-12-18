package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ValidDocumentDto;
import io.mosip.kernel.masterdata.entity.id.ValidDocumentID;
import io.mosip.kernel.masterdata.service.ValidDocumentService;
import io.swagger.annotations.Api;

/**
 * Controller class to create valid document in table.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "ValidDocument" })
public class ValidDocumentController {

	@Autowired
	ValidDocumentService documentService;

	@PostMapping("/v1.0/validdocuments")
	public ResponseEntity<ValidDocumentID> createValidDocument(
			@Valid @RequestBody RequestDto<ValidDocumentDto> document) {
		return new ResponseEntity<>(documentService.createValidDocument(document), HttpStatus.CREATED);
	}
}
