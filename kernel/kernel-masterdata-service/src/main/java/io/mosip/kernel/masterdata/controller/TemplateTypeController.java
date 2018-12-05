package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TemplateTypeDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.TemplateTypeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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
	 * @return {@link CodeAndLanguageCodeID}
	 */
	@PostMapping("/v1.0/templatetype")
	@ApiOperation(value = "Service to create template type", notes = "create TemplateType  and return  code and LangCode", response = CodeAndLanguageCodeID.class)
	@ApiResponses({ @ApiResponse(code = 201, message = " successfully created", response = CodeAndLanguageCodeID.class),
			@ApiResponse(code = 400, message = " Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = " creating any error occured") })
	public ResponseEntity<CodeAndLanguageCodeID> createTemplateType(
			@Valid @RequestBody RequestDto<TemplateTypeDto> templateType) {
		return new ResponseEntity<>(templateTypeService.createTemplateType(templateType.getRequest()),
				HttpStatus.CREATED);

	}

}
