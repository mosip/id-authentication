package io.mosip.registration.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.registration.entity.Template;
import io.mosip.registration.entity.TemplateFileFormat;
import io.mosip.registration.entity.TemplateType;

/**
 * This class is used to fetch the list of templates from {@link Template} table by passing 
 * template type code as parameter,  fetch the list of templates types from {@link TemplateType} 
 * table by passing code and language code as parametrs and to fetch the list of template file 
 * formats from {@link TemplateFileFormat} table.
 * 
 * @author Himaja Dhanyamraju
 *
 */
@Repository
public interface TemplateDao {

	/**
	 * This method returns the list of templates which are active and have specified
	 * templateTypeCode
	 * 
	 * @param templateTypeCode
	 *            the required template type code
	 * @return the list of {@link Template}
	 */
	List<Template> getAllTemplates(String templateTypeCode);

	/**
	 * This method returns the list of template types which are active and have
	 * specified templateTypeCode and language code
	 * 
	 * @param code
	 *            the template type code
	 * @param langCode 
	 * 				the lang code in which the template is required
	 * @return the list of {@link TemplateType}
	 */
	List<TemplateType> getAllTemplateTypes(String code,String langCode);

	/**
	 * This method returns the list of template file formats which are active
	 * 
	 * @return the list of {@link TemplateFileFormat} 
	 */
	List<TemplateFileFormat> getAllTemplateFileFormats();
}
