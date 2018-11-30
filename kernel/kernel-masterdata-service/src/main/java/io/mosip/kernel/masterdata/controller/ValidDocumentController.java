package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.ValidDocumentRequestDto;
import io.mosip.kernel.masterdata.entity.id.ValidDocumentID;
import io.mosip.kernel.masterdata.service.ValidDocumentService;

/**
 * Controller class to create valid document in table.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RestController
public class ValidDocumentController {

	@Autowired
	ValidDocumentService documentService;

	@PostMapping("/validdocuments")
	public ResponseEntity<ValidDocumentID> createValidDocument(@RequestBody ValidDocumentRequestDto document) {
		return new ResponseEntity<>(documentService.insertValidDocument(document), HttpStatus.CREATED);
	}
}
