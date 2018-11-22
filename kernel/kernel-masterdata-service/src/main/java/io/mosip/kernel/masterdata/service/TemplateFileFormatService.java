package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatRequestDto;

/**
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
public interface TemplateFileFormatService {

	public PostResponseDto addTemplateFileFormat(TemplateFileFormatRequestDto templateFileFormatRequestDto);
	
}
