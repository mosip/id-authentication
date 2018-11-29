package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatData;
import io.mosip.kernel.masterdata.entity.CodeAndLanguageCodeId;
import io.mosip.kernel.masterdata.service.TemplateFileFormatService;

@RestController
@RequestMapping("/templatefileformats")
public class TemplateFileFormatController {
	
	@Autowired
	private TemplateFileFormatService templateFileFormatService;

	@PostMapping
	public CodeAndLanguageCodeId addTemplateFileFormat(@RequestBody RequestDto<TemplateFileFormatData> templateFileFormatRequestDto) {
		return templateFileFormatService.addTemplateFileFormat(templateFileFormatRequestDto);
		
	}
}
