package io.mosip.kernel.masterdata.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TemplateDto;
import io.mosip.kernel.masterdata.dto.getresponse.TemplateResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.service.TemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller APIs to get Template details
 * 
 * @author Neha
 * @author Uday kumar
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "Template" })
@RequestMapping("/v1.0/templates")
public class TemplateController {

	@Autowired
	private TemplateService templateService;

	/**
	 * Method to fetch all Template details
	 * 
	 * @return All {@link TemplateDto}
	 */
	@GetMapping
	public TemplateResponseDto getAllTemplate() {
		return templateService.getAllTemplate();
	}

	/**
	 * Method to fetch all Template details based on language code
	 * 
	 * @param langCode
	 *            the language code
	 * @return All {@link TemplateDto}
	 */
	@GetMapping("/{langcode}")
	public TemplateResponseDto getAllTemplateBylangCode(@PathVariable("langcode") String langCode) {
		return templateService.getAllTemplateByLanguageCode(langCode);
	}

	/**
	 * Method to fetch all Template details based on language code and template type
	 * code
	 * 
	 * @param langCode
	 *            the language code
	 * @param templateTypeCode
	 *            the template type code
	 * @return All {@link TemplateDto}
	 */
	@GetMapping("/{langcode}/{templatetypecode}")
	public TemplateResponseDto getAllTemplateBylangCodeAndTemplateTypeCode(@PathVariable("langcode") String langCode,
			@PathVariable("templatetypecode") String templateTypeCode) {
		return templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode(langCode, templateTypeCode);
	}

	/**
	 * This method creates template based on provided details.
	 * 
	 * @param template
	 *            the template detail
	 * @return {@link IdResponseDto}
	 */
	@PostMapping
	@ApiOperation(value = "Service to create template ", notes = "create Template  and return  code", response = IdResponseDto.class)
	@ApiResponses({ @ApiResponse(code = 201, message = " successfully created", response = IdResponseDto.class),
			@ApiResponse(code = 400, message = " Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = " creating any error occured") })
	public ResponseEntity<IdResponseDto> createTemplate(@Valid @RequestBody RequestDto<TemplateDto> template) {
		return new ResponseEntity<>(templateService.createTemplate(template.getRequest()), HttpStatus.CREATED);
	}

	/**
	 * This method update template based on provided details.
	 * 
	 * @param template
	 *            the template detail
	 * @return {@link IdResponseDto}
	 */
	@PutMapping
	@ApiOperation(value = "Service to update template ", notes = "update Template  and return  code ", response = IdResponseDto.class)
	@ApiResponses({ @ApiResponse(code = 200, message = " successfully updated", response = IdResponseDto.class),
			@ApiResponse(code = 400, message = " Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = " creating any error occured") })
	public ResponseEntity<IdResponseDto> updateTemplate(@Valid @RequestBody RequestDto<TemplateDto> template) {
		return new ResponseEntity<>(templateService.updateTemplates(template.getRequest()), HttpStatus.OK);
	}

	/**
	 * This method delete template based on provided details.
	 * 
	 * @param id
	 *            the template id
	 * @return {@link IdResponseDto}
	 */

	@DeleteMapping("/{id}")
	@ApiOperation(value = "Service to delete template", notes = "Delete template and return template id", response = IdResponseDto.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "When template successfully deleted", response = IdResponseDto.class),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When No template found"),
			@ApiResponse(code = 500, message = "While deleting template  error occured") })
	public ResponseEntity<IdResponseDto> deleteTemplate(@PathVariable("id") String id) {
		return new ResponseEntity<>(templateService.deleteTemplates(id), HttpStatus.OK);
	}
}
