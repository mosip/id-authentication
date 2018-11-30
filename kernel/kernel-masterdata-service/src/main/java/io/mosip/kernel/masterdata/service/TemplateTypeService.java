package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.TemplateTypeDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
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
	 * @param tempalteType
	 *            dto with Template Type .
	 * @return {@linkplain CodeAndLanguageCodeID}
	 */
	public CodeAndLanguageCodeID createTemplateType(TemplateTypeDto tempalteType);
}
