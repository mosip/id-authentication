package io.mosip.registration.service;

import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * This is the interface for TemplateServcie
 *
 * @author Himaja Dhanyamraju
 */
public interface TemplateService {
	/**
	 * This method returns the data that is in the template which is chosen for
	 * creating the acknowledgement
	 * 
	 * @param templateName 
	 *            to define the template name
	 * 
	 * @return String which contains the template data
	 * @throws RegBaseCheckedException
	 */
	public String getHtmlTemplate(String templateName) throws RegBaseCheckedException;
	
}
