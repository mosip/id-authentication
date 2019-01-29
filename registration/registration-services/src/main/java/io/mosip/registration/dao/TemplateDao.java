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
	 * This method returns the list of templates which are active
	 * 
	 * @return List<Template>
	 *            the list of templates
	 */
	List<Template> getAllTemplates();
	
	/**
	 * This method returns the list of template types which are active
	 * 
	 * @return List<TemplateType>
	 *            the list of template types
	 */
	List<TemplateType> getAllTemplateTypes();
	
	/**
	 * This method returns the list of template file formats which are active
	 * 
	 * @return List<TemplateFileFormatO>
	 *            the list of template file formats
	 */
	List<TemplateFileFormat> getAllTemplateFileFormats();
}
