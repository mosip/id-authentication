package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.TemplateDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.TemplateResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.BlacklistedWordsExtnDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.TemplateExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.Template;
import io.mosip.kernel.masterdata.entity.id.IdAndLanguageCodeID;

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
	 *            the language code
	 * @return {@link TemplateResponseDto}
	 */
	public TemplateResponseDto getAllTemplateByLanguageCode(String langCode);

	/**
	 * To fetch all the {@link Template} based on language code and template type
	 * code
	 * 
	 * @param langCode
	 *            the language code
	 * @param templateTypeCode
	 *            the template type code
	 * @return {@link TemplateResponseDto}
	 */
	public TemplateResponseDto getAllTemplateByLanguageCodeAndTemplateTypeCode(String langCode,
			String templateTypeCode);

	/**
	 * Method to create template based on provided details
	 * 
	 * @param template
	 *            the Template Dto.
	 * @return {@linkplain IdAndLanguageCodeID}
	 */
	public IdAndLanguageCodeID createTemplate(TemplateDto template);

	/**
	 * Method to update template based on provided details
	 * 
	 * @param template
	 *            the Template Dto.
	 * @return {@linkplain IdAndLanguageCodeID}
	 */
	public IdAndLanguageCodeID updateTemplates(TemplateDto template);

	/**
	 * Method to delete template based on provided template id
	 * 
	 * @param id
	 *            Template id.
	 * @return {@linkplain IdResponseDto}
	 */

	public IdResponseDto deleteTemplates(String id);

	/**
	 * To fetch all the {@link Template} based on template type code
	 * 
	 * @param templateTypeCode
	 *            the template type code
	 * @return {@link TemplateResponseDto}
	 */
	public TemplateResponseDto getAllTemplateByTemplateTypeCode(String templateTypeCode);

	/**
	 * This method provides with all templates.
	 * 
	 * @param pageNumber
	 *            the page number
	 * @param pageSize
	 *            the size of each page
	 * @param sortBy
	 *            the attributes by which it should be ordered
	 * @param orderBy
	 *            the order to be used
	 * 
	 * @return the response i.e. pages containing the templates.
	 */
	public PageDto<TemplateExtnDto> getTemplates(int pageNumber, int pageSize, String sortBy, String orderBy);

	/**
	 * Search templates.
	 *
	 * @param searchDto the search dto
	 * @return {@link PageResponseDto}the page response dto
	 */
	public PageResponseDto<TemplateExtnDto> searchTemplates(SearchDto searchDto);

}
