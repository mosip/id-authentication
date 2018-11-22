package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatRequestDto;
import io.mosip.kernel.masterdata.service.TemplateFileFormatService;

@RestController
@RequestMapping("/templatefileformats")
public class TemplateFileFormatController {
	
	@Autowired
	private TemplateFileFormatService templateFileFormatService;

	@PostMapping
	public PostResponseDto addTemplateFileFormat(@RequestBody TemplateFileFormatRequestDto templateFileFormatRequestDto) {
		return templateFileFormatService.addTemplateFileFormat(templateFileFormatRequestDto);
		
	}
}
