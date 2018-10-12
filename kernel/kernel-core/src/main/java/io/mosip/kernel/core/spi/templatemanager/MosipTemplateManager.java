package io.mosip.kernel.core.spi.templatemanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Map;

/**
 * MosipTemplateManager which will merge the template and values
 * together.
 * 
 * @author Abhishek Kumar
 * @since 2018-10-01
 * @version 1.0.0
 */
public interface MosipTemplateManager {
	/**
	 * Method to merge template , where template content will be pass as inputSteam
	 * 
	 * @param template
	 *            as InputStream for template content template content should be in
	 *            the form of InputStream
	 * @param values
	 *            as Map values should be pass as Map<String,Object>
	 * @return template as InputStream merged given template content and values
	 * @throws IOException 
	 * 
	 */
	public InputStream mergeTemplate(InputStream template, Map<String, Object> values) throws IOException;

	/**
	 * Method to merge template using default UTF-8 encoding
	 * 
	 * @param templateName
	 *            as String
	 * @param writer
	 * @param values
	 *            as Map values should be pass as Map<String,Object>
	 * @return boolean return true if successfully merged given template and values
	 * @throws IOException 
	 */
	public boolean merge(String templateName, Writer writer, Map<String, Object> values) throws IOException;

	/**
	 * Method to merge template using provided encoding type
	 * 
	 * @param templateName
	 *            as String
	 * @param writer
	 * @param values
	 *            as Map
	 * @param encodingType
	 *            as String
	 * @return boolean return true if successfully merged given template and values
	 * @throws IOException 
	 */
	public boolean merge(String templateName, Writer writer, Map<String, Object> values, final String encodingType) throws IOException;
}
