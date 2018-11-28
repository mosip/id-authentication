package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.TemplateFileFormatRequestDto;
import io.mosip.kernel.masterdata.entity.CodeAndLanguageCodeId;

/**
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
public interface TemplateFileFormatService {

	public CodeAndLanguageCodeId addTemplateFileFormat(TemplateFileFormatRequestDto templateFileFormatRequestDto);
	
}
