package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.TemplateDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.Template;

/**
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
public interface TemplateService {

	/**
	 * To fetch all the {@link Template} based on language code
	 * 
	 * @return {@link List<TemplateDto>}
	 */
	public List<TemplateDto> getAllTemplate();

	/**
	 * To fetch all the {@link Template} based on language code
	 * 
	 * @param languageCode
	 * @return {@link List<Template>}
	 */
	public List<TemplateDto> getAllTemplateByLanguageCode(String languageCode);

	/**
	 * To fetch all the {@link Template} based on language code and template type code
	 * 
	 * @param languageCode
	 * @param templateTypeCode
	 * @return {@link List<Template>}
	 */
	public List<TemplateDto> getAllTemplateByLanguageCodeAndTemplateTypeCode(String languageCode,
			String templateTypeCode);
	/**
	 * Method to create template  based on  provided
	 * 
	 * @param tempalte
	 *            dto with Template .
	 * @return {@linkplain IdResponseDto}
	 */
	public IdResponseDto createTemplate(TemplateDto tempalte);

}
