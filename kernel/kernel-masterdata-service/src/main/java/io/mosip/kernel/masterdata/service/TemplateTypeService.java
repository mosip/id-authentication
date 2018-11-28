package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.TemplateTypeRequestDto;
import io.mosip.kernel.masterdata.entity.CodeAndLanguageCodeId;
/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */

public interface TemplateTypeService {

	
	/**
	 * Method to create template type based on  provided
	 * 
	 * @param category
	 *            dto with Template Type .
	 * @return {@linkplain CodeAndLanguageCodeId}
	 */
	public CodeAndLanguageCodeId createTemplateType(TemplateTypeRequestDto tempalteType);
}
