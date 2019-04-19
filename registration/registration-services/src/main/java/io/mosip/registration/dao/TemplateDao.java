package io.mosip.registration.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.registration.entity.Template;
import io.mosip.registration.entity.TemplateFileFormat;
import io.mosip.registration.entity.TemplateType;

/**
 * DAO class for Repository
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
	 * @return {@link Template} the list of templates
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
	 * @return {@link TemplateType} the list of template types
	 */
	List<TemplateType> getAllTemplateTypes(String code,String langCode);

	/**
	 * This method returns the list of template file formats which are active
	 * 
	 * @return {@link TemplateFileFormat} the list of template file formats
	 */
	List<TemplateFileFormat> getAllTemplateFileFormats();
}
