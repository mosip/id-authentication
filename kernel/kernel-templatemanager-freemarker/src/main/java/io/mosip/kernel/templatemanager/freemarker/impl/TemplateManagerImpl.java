package io.mosip.kernel.templatemanager.freemarker.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import io.mosip.kernel.core.spi.templatemanager.MosipTemplateManager;
import io.mosip.kernel.templatemanager.freemarker.constant.TemplateManagerConstant;
import io.mosip.kernel.templatemanager.freemarker.constant.TemplateManagerExceptionCodeConstant;
import io.mosip.kernel.templatemanager.freemarker.exception.TemplateParsingException;
import io.mosip.kernel.templatemanager.freemarker.exception.TemplateResourceNotFoundException;

/**
 * Implementation of {@link MosipTemplateManager} which uses Velocity Template
 * Engine, TemplateManagerImpl will merge the template with values.
 * 
 * <pre>
 * // set up and initialize MosipTemplateManager using TemplateConfigureBuilder
 * // before this code block
 *
 * MosipTemplateManager templateManager = new TemplateConfigureBuilder().build();
 * templateManager.merge(template, values);
 * </pre>
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 2018-10-01
 */
public class TemplateManagerImpl implements MosipTemplateManager {
	private Configuration configuration;

	/**
	 * constructor
	 * 
	 * @param configuration
	 *            template configuration
	 * 
	 */
	public TemplateManagerImpl(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Method to merge template , where template content will be pass as inputSteam
	 * 
	 * @param is
	 *            the {@link InputStream} is template content .
	 * @param values
	 *            as Map&lt;String,Object &gt; where key will be placeholder name
	 *            and Object is the actual value for the placeholder
	 * @return template merged template content as {@link InputStream}
	 * @throws IOException
	 *             if an I/O exception occurs during writing to the writer
	 * 
	 */
	@Override
	public InputStream mergeTemplate(InputStream is, Map<String, Object> values) throws IOException {
		Reader reader = null;
		Template template = null;
		StringWriter writer = new StringWriter();

		Objects.requireNonNull(is, TemplateManagerConstant.TEMPLATE_INPUT_STREAM_NULL.getMessage()); // data null check
		Objects.requireNonNull(values, TemplateManagerConstant.TEMPLATE_VALUES_NULL.getMessage());// values null check
		try {
			reader = new InputStreamReader(is);
			template = new Template("template", reader, configuration);
			template.process(values, writer);
			return new ByteArrayInputStream(writer.toString().getBytes());
		} catch (TemplateException e) {
			throw new TemplateParsingException(TemplateManagerExceptionCodeConstant.TEMPLATE_PARSING.getErrorCode(),
					TemplateManagerExceptionCodeConstant.TEMPLATE_PARSING.getErrorMessage());
		}
	}

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
	@Override
	public boolean merge(String templateName, Writer writer, Map<String, Object> values) throws IOException {
		return merge(templateName, writer, values, configuration.getDefaultEncoding());
	}

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
	@Override
	public boolean merge(String templateName, Writer writer, Map<String, Object> values, String encodingType)
			throws IOException {
		boolean isMerged = false;
		// Null Checks
		Objects.requireNonNull(templateName, TemplateManagerConstant.TEMPATE_NAME_NULL.getMessage());
		Objects.requireNonNull(writer, TemplateManagerConstant.WRITER_NULL.getMessage());
		Objects.requireNonNull(encodingType, TemplateManagerConstant.ENCODING_TYPE_NULL.getMessage());
		Objects.requireNonNull(values, TemplateManagerConstant.TEMPLATE_VALUES_NULL.getMessage());
		try {
			Template template = configuration.getTemplate(templateName, encodingType);
			template.process(values, writer);
			isMerged = true;
		} catch (TemplateNotFoundException e) {
			throw new TemplateResourceNotFoundException(
					TemplateManagerExceptionCodeConstant.TEMPLATE_NOT_FOUND.getErrorCode(),
					TemplateManagerExceptionCodeConstant.TEMPLATE_NOT_FOUND.getErrorMessage());
		} catch (TemplateException e) {
			throw new TemplateParsingException(TemplateManagerExceptionCodeConstant.TEMPLATE_PARSING.getErrorCode(),
					e.getMessage());
		}
		return isMerged;
	}

}
