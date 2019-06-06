package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.constant.OrderEnum;
import io.mosip.kernel.masterdata.dto.TemplateDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.TemplateResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.TemplateExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.id.IdAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.TemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@RequestMapping("/templates")
public class TemplateController {

	@Autowired
	private TemplateService templateService;

	/**
	 * Method to fetch all Template details
	 * 
	 * @return All {@link TemplateDto}
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','REGISTRATION_PROCESSOR','ID_AUTHENTICATION','AUTH','ZONAL_ADMIN','ZONAL_APPROVER')")
	@ResponseFilter
	@GetMapping
	public ResponseWrapper<TemplateResponseDto> getAllTemplate() {
		ResponseWrapper<TemplateResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(templateService.getAllTemplate());
		return responseWrapper;
	}

	/**
	 * Method to fetch all Template details based on language code
	 * 
	 * @param langCode
	 *            the language code
	 * @return All {@link TemplateDto}
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','REGISTRATION_PROCESSOR','ID_AUTHENTICATION','AUTH','PRE_REGISTRATION_ADMIN')")
	@ResponseFilter
	@GetMapping("/{langcode}")
	public ResponseWrapper<TemplateResponseDto> getAllTemplateBylangCode(@PathVariable("langcode") String langCode) {

		ResponseWrapper<TemplateResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(templateService.getAllTemplateByLanguageCode(langCode));
		return responseWrapper;
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
	@PreAuthorize("hasAnyRole('INDIVIDUAL','REGISTRATION_PROCESSOR','ID_AUTHENTICATION','AUTH','PRE_REGISTRATION_ADMIN')")
	@ResponseFilter
	@GetMapping("/{langcode}/{templatetypecode}")
	public ResponseWrapper<TemplateResponseDto> getAllTemplateBylangCodeAndTemplateTypeCode(
			@PathVariable("langcode") String langCode, @PathVariable("templatetypecode") String templateTypeCode) {

		ResponseWrapper<TemplateResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(
				templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode(langCode, templateTypeCode));
		return responseWrapper;
	}

	/**
	 * This method creates template based on provided details.
	 * 
	 * @param template
	 *            the template detail
	 * @return {@link IdResponseDto}
	 */
	@ResponseFilter
	@PostMapping
	@ApiOperation(value = "Service to create template ", notes = "create Template  and return  code")
	@ApiResponses({ @ApiResponse(code = 201, message = " successfully created"),
			@ApiResponse(code = 400, message = " Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = " creating any error occured") })
	public ResponseWrapper<IdAndLanguageCodeID> createTemplate(
			@Valid @RequestBody RequestWrapper<TemplateDto> template) {

		ResponseWrapper<IdAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(templateService.createTemplate(template.getRequest()));
		return responseWrapper;
	}

	/**
	 * This method update template based on provided details.
	 * 
	 * @param template
	 *            the template detail
	 * @return {@link IdResponseDto}
	 */
	@ResponseFilter
	@PutMapping
	@ApiOperation(value = "Service to update template ", notes = "update Template  and return  code ")
	@ApiResponses({ @ApiResponse(code = 200, message = " successfully updated"),
			@ApiResponse(code = 400, message = " Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = " creating any error occured") })
	public ResponseWrapper<IdAndLanguageCodeID> updateTemplate(
			@Valid @RequestBody RequestWrapper<TemplateDto> template) {

		ResponseWrapper<IdAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(templateService.updateTemplates(template.getRequest()));
		return responseWrapper;
	}

	/**
	 * This method delete template based on provided details.
	 * 
	 * @param id
	 *            the template id
	 * @return {@link IdResponseDto}
	 */
	@ResponseFilter
	@DeleteMapping("/{id}")
	@ApiOperation(value = "Service to delete template", notes = "Delete template and return template id")
	@ApiResponses({ @ApiResponse(code = 200, message = "When template successfully deleted"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When No template found"),
			@ApiResponse(code = 500, message = "While deleting template  error occured") })
	public ResponseWrapper<IdResponseDto> deleteTemplate(@PathVariable("id") String id) {

		ResponseWrapper<IdResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(templateService.deleteTemplates(id));
		return responseWrapper;
	}

	/**
	 * Method to fetch all Template details based on template type code
	 * 
	 * @param templateTypeCode
	 *            the template type code
	 * @return All {@link TemplateDto}
	 */
	@GetMapping("/templatetypecodes/{code}")
	public ResponseWrapper<TemplateResponseDto> getAllTemplateByTemplateTypeCode(
			@PathVariable("code") String templateTypeCode) {

		ResponseWrapper<TemplateResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(templateService.getAllTemplateByTemplateTypeCode(templateTypeCode));
		return responseWrapper;
	}

	/**
	 * This controller method provides with all templates.
	 * 
	 * @param pageNumber
	 *            the page number
	 * @param pageSize
	 *            the size of each page
	 * @param sortBy
	 *            the attributes by which it should be ordered
	 * @param orderBy
	 *            the order to be used
	 * 
	 * @return the response i.e. pages containing the templates.
	 */
	@PreAuthorize("hasAnyRole('ZONAL_ADMIN','CENTRAL_ADMIN')")
	@ResponseFilter
	@GetMapping("/all")
	@ApiOperation(value = "Retrieve all the templates with additional metadata", notes = "Retrieve all the templates with the additional metadata")
	@ApiResponses({ @ApiResponse(code = 200, message = "list of templates"),
			@ApiResponse(code = 500, message = "Error occured while retrieving templates") })
	public ResponseWrapper<PageDto<TemplateExtnDto>> getTemplates(
			@RequestParam(name = "pageNumber", defaultValue = "0") @ApiParam(value = "page no for the requested data", defaultValue = "0") int pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") @ApiParam(value = "page size for the requested data", defaultValue = "10") int pageSize,
			@RequestParam(name = "sortBy", defaultValue = "createdDateTime") @ApiParam(value = "sort the requested data based on param value", defaultValue = "createdDateTime") String sortBy,
			@RequestParam(name = "orderBy", defaultValue = "desc") @ApiParam(value = "order the requested data based on param", defaultValue = "desc") OrderEnum orderBy) {
		ResponseWrapper<PageDto<TemplateExtnDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(templateService.getTemplates(pageNumber, pageSize, sortBy, orderBy.name()));
		return responseWrapper;
	}
}
