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

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import io.mosip.kernel.core.spi.templatemanager.MosipTemplateManager;
import io.mosip.kernel.templatemanager.freemarker.constant.TemplateManagerConstant;
import io.mosip.kernel.templatemanager.freemarker.constant.TemplateManagerExceptionCodeConstant;
import io.mosip.kernel.templatemanager.freemarker.exception.TemplateParsingException;
import io.mosip.kernel.templatemanager.freemarker.exception.TemplateResourceNotFoundException;

/**
 * Implementation of @See {@link MosipTemplateManager} which uses Apache
 * Freemarker Template Engine. TemplateManagerImpl will merge the template with
 * values.
 * 
 * @author Abhishek Kumar
 * @version 1.0
 * @since 2018-10-01
 */
public class TemplateManagerImpl implements MosipTemplateManager {
	private Configuration configuration;

	public TemplateManagerImpl(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Method to merge template , where template content will be pass as inputSteam
	 * 
	 * @param is
	 *            as InputStream
	 * @param values
	 *            as Map
	 * @return as InputStream
	 * @throws IOException
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
	 * Method will merge template with values with default encoding UTF-8
	 * 
	 * @param templateName
	 * @param writer
	 * @param values
	 *            as Map
	 * @throws IOException
	 */
	@Override
	public boolean merge(String templateName, Writer writer, Map<String, Object> values) throws IOException {
		return merge(templateName, writer, values, configuration.getDefaultEncoding());
	}

	/**
	 * Method will merge template with values with provided encoding type
	 * 
	 * @param templateName
	 * @param writer
	 * @param values
	 *            as Map
	 * @param encodingType
	 * @throws IOException
	 * @throws ParseException
	 * @throws MalformedTemplateNameException
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
