package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatData;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

/**
 * Service API for Template File Format
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
public interface TemplateFileFormatService {

	/**
	 * Method to create a templatefileformat
	 * 
	 * @param templateFileFormatRequestDto
	 *            the template file format dto
	 * @return {@link CodeAndLanguageCodeID}
	 */
	public CodeAndLanguageCodeID createTemplateFileFormat(
			RequestDto<TemplateFileFormatData> templateFileFormatRequestDto);

}
