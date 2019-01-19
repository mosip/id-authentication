package io.mosip.kernel.core.templatemanager.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Map;

/**
 * TemplateManager which will merge the template and values together.
 * 
 * @author Abhishek Kumar
 * @since 2018-10-01
 * @version 1.0.0
 */
public interface TemplateManager {
	/**
	 * Method to merge template , where template content will be pass as inputSteam
	 * 
	 * @param template
	 *            the {@link InputStream} is template content .
	 * @param values
	 *            as Map&lt;String,Object &gt; where key will be placeholder name
	 *            and Object is the actual value for the placeholder
	 * @return template merged template content as {@link InputStream}
	 * @throws IOException
	 *             if an I/O exception occurs during writing to the writer
	 * 
	 */
	public InputStream merge(InputStream template, Map<String, Object> values) throws IOException;

	/**
	 * Merges a template and puts the rendered stream into the writer. The default
	 * encoding that template manager uses to read template files is UTF-8
	 * 
	 * @param templateName
	 *            name of template to be used in merge
	 * @param writer
	 *            output writer for rendered template
	 * @param values
	 *            as Map&lt;String,Object &gt; where key is placeholder name and
	 *            Object is Placeholder value
	 * @return boolean true if successfully, false otherwise.
	 * @throws IOException
	 *             if an I/O exception occurs during writing to the writer
	 */
	public boolean merge(String templateName, Writer writer, Map<String, Object> values) throws IOException;

	/**
	 * Method to merge template using provided encoding type
	 * 
	 * @param templateName
	 *            name of the template to be used in merge
	 * @param writer
	 *            output writer for render template
	 * @param values
	 *            as Map&lt;String,Object &gt; where key is placeholder name and
	 *            Object is value for the placeholder
	 * @param encodingType
	 *            as String like UTF-8,UTF-16 etc.
	 * @return boolean true if successfully, false otherwise
	 * @throws IOException
	 *             if an I/O exception occurs during writing to the writer
	 */
	public boolean merge(String templateName, Writer writer, Map<String, Object> values, final String encodingType)
			throws IOException;
}
