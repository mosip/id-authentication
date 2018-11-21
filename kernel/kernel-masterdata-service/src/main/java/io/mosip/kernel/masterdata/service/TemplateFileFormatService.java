package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatRequestDto;

public interface TemplateFileFormatService {

	public PostResponseDto addTemplateFileFormat(TemplateFileFormatRequestDto templateFileFormatRequestDto);
	
}
