package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.TemplateTypeDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.TemplateTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 * 
 */
@RestController
@Api(tags = { "TemplateType" })
public class TemplateTypeController {

	@Autowired
	TemplateTypeService templateTypeService;

	/**
	 * This method creates template type based on provided.
	 * 
	 * @param templateType the request dto.
	 * @return {@link CodeAndLanguageCodeID}
	 */
	@ResponseFilter
	@PostMapping("/templatetypes")
	@ApiOperation(value = "Service to create template type", notes = "create TemplateType  and return  code and LangCode")
	@ApiResponses({ @ApiResponse(code = 201, message = " successfully created"),
			@ApiResponse(code = 400, message = " Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = " creating any error occured") })
	public ResponseWrapper<CodeAndLanguageCodeID> createTemplateType(
			@Valid @RequestBody RequestWrapper<TemplateTypeDto> templateType) {

		ResponseWrapper<CodeAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(templateTypeService.createTemplateType(templateType.getRequest()));
		return responseWrapper;

	}

}
