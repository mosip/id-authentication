package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.TemplateDto;
import io.mosip.kernel.masterdata.dto.getresponse.TemplateResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.Template;

/**
 * @author Uday Kumar
 * @author Neha
 * @since 1.0.0
 *
 */
public interface TemplateService {

	/**
	 * To fetch all the {@link Template} based on language code
	 * 
	 * @return {@link TemplateResponseDto}
	 */
	public TemplateResponseDto getAllTemplate();

	/**
	 * To fetch all the {@link Template} based on language code
	 * 
	 * @param langCode
	 * 			the language code
	 * @return {@link TemplateResponseDto}
	 */
	public TemplateResponseDto getAllTemplateByLanguageCode(String langCode);

	/**
	 * To fetch all the {@link Template} based on language code and template type code
	 * 
	 * @param langCode
	 * 			the language code
	 * @param templateTypeCode
	 * 			the template type code
	 * @return {@link TemplateResponseDto}
	 */
	public TemplateResponseDto getAllTemplateByLanguageCodeAndTemplateTypeCode(String langCode,
			String templateTypeCode);
	/**
	 * Method to create template  based on  provided details
	 * 
	 * @param template
	 *            the Template Dto .
	 * @return {@linkplain IdResponseDto}
	 */
	public IdResponseDto createTemplate(TemplateDto tempalte);

}
