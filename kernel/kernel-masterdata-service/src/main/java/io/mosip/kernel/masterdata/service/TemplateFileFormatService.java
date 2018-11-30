package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatData;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

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
	 * 
	 * @return {@link CodeAndLanguageCodeID}
	 * 
	 * @throws MasterDataServiceException
	 * 					If the insertion of data fails
	 */
	public CodeAndLanguageCodeID createTemplateFileFormat(
			RequestDto<TemplateFileFormatData> templateFileFormatRequestDto);

}
