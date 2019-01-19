package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * Service API for Template File Format
 * 
 * @author Neha Sinha
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
			RequestDto<TemplateFileFormatDto> templateFileFormatRequestDto);

	/**
	 * This method is used to update an existing TemplateFileFormat
	 * 
	 * @param templateFileFormatRequestDto
	 *            TemplateFileFormat DTO to update data
	 * @return IdResponseDto TemplateFileFormat ID which is successfully updated
	 *         {@link IdResponseDto}
	 * @throws MasterDataServiceException
	 *             if any error occurred while updating Device
	 */
	public CodeAndLanguageCodeID updateDevice(RequestDto<TemplateFileFormatDto> templateFileFormatRequestDto);

	/**
	 * Method to delete TemplateFileFormat based on code provided.
	 * 
	 * @param code
	 *            the TemplateFileFormat code.
	 * 
	 * @return {@link CodeResponseDto}
	 */
	public CodeResponseDto deleteTemplateFileFormat(String code);

}
