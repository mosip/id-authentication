package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatData;
import io.mosip.kernel.masterdata.entity.CodeAndLanguageCodeId;

/**
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
public interface TemplateFileFormatService {

	public CodeAndLanguageCodeId addTemplateFileFormat(RequestDto<TemplateFileFormatData> templateFileFormatRequestDto);
	
}
