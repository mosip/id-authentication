package io.mosip.registration.service.template;

import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * The {@code TemplateService} represents the Template that needs to be
 * displayed. This class will be invoked during New Registration, UIN Update and
 * Lost UIN based on the template code.
 *
 * @author Himaja Dhanyamraju
 */
public interface TemplateService {
	
	/**
	 * This method returns the template data to display based on the corresponding
	 * template and primary language code
	 * 
	 * <p>If provided Template code is not null</p>
	 * 		<p>Fetch Template and Template Type and Template File Format from the database by passing Template code
	 * 			return the template which matches with language code and file format code</p>
	 * <p>If provided Template code is null:</p>
	 * 		<p>returns empty string</p>
	 * 
	 * @param templateName
	 *            {@code String} defines the template name
	 * @param langCode
	 *            {@code String} the language code in which the template is required
	 * 
	 * @return {@code String} which contains the template data
	 * @throws RegBaseCheckedException the custom exception to handle all checked exceptions
	 */
	public String getHtmlTemplate(String templateName, String langCode) throws RegBaseCheckedException;
	
}
