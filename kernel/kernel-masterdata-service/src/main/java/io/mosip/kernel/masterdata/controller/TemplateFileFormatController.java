package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.TemplateFileFormatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller class to fetch or create TemplateFileFormat.
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@RestController
@RequestMapping("/templatefileformats")
@Api(tags = { "TemplateFileFormat" })
public class TemplateFileFormatController {

	@Autowired
	private TemplateFileFormatService templateFileFormatService;

	/**
	 * API to create a templatefileformat
	 * 
	 * @param templateFileFormatRequestDto {@link TemplateFileFormatDto} instance
	 * 
	 * @return {@link CodeAndLanguageCodeID}
	 */
	@ResponseFilter
	@PostMapping
	public ResponseWrapper<CodeAndLanguageCodeID> createTemplateFileFormat(
			@Valid @RequestBody RequestWrapper<TemplateFileFormatDto> templateFileFormatRequestDto) {

		ResponseWrapper<CodeAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(
				templateFileFormatService.createTemplateFileFormat(templateFileFormatRequestDto.getRequest()));
		return responseWrapper;
	}

	/**
	 * API to update an existing row of Templatefileformat data
	 * 
	 * @param templateFileFormatRequestDto input parameter
	 *                                     templateFileFormatRequestDto
	 * 
	 * @return ResponseEntity TemplateFileFormat Code and LangCode which is updated
	 *         successfully {@link ResponseEntity}
	 */
	@ResponseFilter
	@PutMapping
	@ApiOperation(value = "Service to update TemplateFileFormat", notes = "Update TemplateFileFormat and return TemplateFileFormat id")
	@ApiResponses({ @ApiResponse(code = 200, message = "When TemplateFileFormat updated successfully"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When TemplateFileFormat is not found"),
			@ApiResponse(code = 500, message = "While updating TemplateFileFormat any error occured") })
	public ResponseWrapper<CodeAndLanguageCodeID> updateDevice(
			@Valid @RequestBody RequestWrapper<TemplateFileFormatDto> templateFileFormatRequestDto) {

		ResponseWrapper<CodeAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(
				templateFileFormatService.updateTemplateFileFormat(templateFileFormatRequestDto.getRequest()));
		return responseWrapper;
	}

	/**
	 * Api to delete TemplateFileFormat
	 * 
	 * @param code the TemplateFileFormat code
	 * @return the code of templatefileformat
	 */
	@ResponseFilter
	@DeleteMapping("/{code}")
	@ApiOperation(value = "Service to delete TemplateFileFormat", notes = "Delete TemplateFileFormat and return code")
	@ApiResponses({ @ApiResponse(code = 200, message = "When TemplateFileFormat successfully deleted"),
			@ApiResponse(code = 400, message = "When path is invalid"),
			@ApiResponse(code = 404, message = "When No document category found"),
			@ApiResponse(code = 500, message = "While deleting document category any error occured") })
	public ResponseWrapper<CodeResponseDto> deleteDocumentCategory(@PathVariable("code") String code) {

		ResponseWrapper<CodeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(templateFileFormatService.deleteTemplateFileFormat(code));
		return responseWrapper;
	}
}
