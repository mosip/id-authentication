package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.TemplateTypeRequestDto;
import io.mosip.kernel.masterdata.entity.CodeAndLanguageCodeId;
import io.mosip.kernel.masterdata.service.TemplateTypeService;

/**
 * 
 * 
 * @author Uday Kumar
 * @since 1.0.0
 * 
 */
@RestController
public class TemplateTypeController {

	@Autowired
	TemplateTypeService templateTypeService;

	/**
	 * This method creates template type based on provided.
	 * 
	 * @param category
	 *            the request dto.
	 * @return {@link CodeAndLanguageCodeId}
	 */
	@PostMapping("/templatetype")
	public ResponseEntity<CodeAndLanguageCodeId> createTemplateType(@RequestBody TemplateTypeRequestDto templateType) {
		return new ResponseEntity<>(templateTypeService.createTemplateType(templateType), HttpStatus.CREATED);

	}

}
