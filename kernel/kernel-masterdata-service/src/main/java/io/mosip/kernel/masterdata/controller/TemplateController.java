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

import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TemplateDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.TemplateService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller APIs to get Template details
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@RestController
public class TemplateController {

	@Autowired
	private TemplateService templateService;

	/**
	 * Method to fetch all Template details
	 * 
	 * @return All {@link TemplateDto}
	 */
	@GetMapping("/templates")
	public List<TemplateDto> getAllTemplate() {
		return templateService.getAllTemplate();
	}

	/**
	 * API to fetch all Template details based on language code
	 * 
	 * @return All TemplateDto of specific language
	 */
	@GetMapping("/templates/{langcode}")
	public List<TemplateDto> getAllTemplateBylangCode(@PathVariable("langcode") String langCode) {
		return templateService.getAllTemplateByLanguageCode(langCode);
	}

	/**
	 * API to fetch a Template details using templateTypeCode and language code
	 * 
	 * @return Template Details
	 */
	@GetMapping("/templates/{langcode}/{templatetypecode}")
	public List<TemplateDto> getAllTemplateBylangCodeAndTemplateTypeCode(
			@PathVariable("langcode") String langCode,
			@PathVariable("templatetypecode") String templateTypeCode) {
		return templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode(langCode, templateTypeCode);
	}
	
	/**
	 * This method creates template  based on provided.
	 * 
	 * @param category
	 *            the request dto.
	 * @return {@link CodeAndLanguageCodeID}
	 */
	@PostMapping("/templates")
	@ApiOperation(value = "Service to create template ", notes = "create Template  and return  code and LangCode", response = IdResponseDto.class)
	@ApiResponses({ @ApiResponse(code = 201, message = " successfully created", response = IdResponseDto.class),
			@ApiResponse(code = 400, message = " Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = " creating any error occured") })
	public ResponseEntity<IdResponseDto> createTemplate(
			@Valid @RequestBody RequestDto<TemplateDto> template) {
		return new ResponseEntity<>(templateService.createTemplate(template.getRequest()),
				HttpStatus.CREATED);

	}
}
