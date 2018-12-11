package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatData;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.TemplateFileFormatService;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v1.0/templatefileformats")
@Api(tags = { "TemplateFileFormat" })
public class TemplateFileFormatController {

	@Autowired
	private TemplateFileFormatService templateFileFormatService;

	/**
	 * API to create a templatefileformat
	 * 
	 * @param templateFileFormatRequestDto
	 * 
	 * @return {@link CodeAndLanguageCodeID}
	 */
	@PostMapping
	public CodeAndLanguageCodeID createTemplateFileFormat(
			@Valid @RequestBody RequestDto<TemplateFileFormatData> templateFileFormatRequestDto) {
		return templateFileFormatService.createTemplateFileFormat(templateFileFormatRequestDto);

	}
}
