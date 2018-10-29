package io.mosip.registration.service;

import io.mosip.registration.entity.Template;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * This is the interface for TemplateServcie
 *
 * @author Himaja Dhanyamraju
 */
public interface TemplateService {

	/**
	 * This method takes the list of templates, template file formats and template
	 * types from database and chooses the required template for creation of
	 * acknowledgement
	 * 
	 * @return single template
	 */
	public Template getTemplate();

	/**
	 * This method returns the data that is in the template which is chosen for
	 * creating the acknowledgement
	 * 
	 * @return String which contains the template data
	 * @throws RegBaseCheckedException
	 */
	public String createReceipt() throws RegBaseCheckedException;
}
