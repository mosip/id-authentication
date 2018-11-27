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
import io.mosip.kernel.core.templatemanager.exception.TemplateParsingException;
import io.mosip.kernel.core.templatemanager.exception.TemplateResourceNotFoundException;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.templatemanager.freemarker.constant.TemplateManagerConstant;
import io.mosip.kernel.templatemanager.freemarker.constant.TemplateManagerExceptionCodeConstant;

/**
 * Implementation of {@link TemplateManager} which uses Velocity Template
 * Engine, TemplateManagerImpl will merge the template with values.
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 01-10-2018
 */
/**
 * @author Abhishek Kumar
 *
 */
public class TemplateManagerImpl implements TemplateManager {
	private Configuration configuration;

	public TemplateManagerImpl(Configuration configuration) {
		this.configuration = configuration;
	}

	
	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.templatemanager.spi.TemplateManager#merge(java.io.InputStream, java.util.Map)
	 */
	@Override
	public InputStream merge(InputStream is, Map<String, Object> values) throws IOException {
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
					TemplateManagerExceptionCodeConstant.TEMPLATE_PARSING.getErrorMessage(), e);
		}
	}

	
	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.templatemanager.spi.TemplateManager#merge(java.lang.String, java.io.Writer, java.util.Map)
	 */
	@Override
	public boolean merge(String templateName, Writer writer, Map<String, Object> values) throws IOException {
		return merge(templateName, writer, values, configuration.getDefaultEncoding());
	}

	
	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.templatemanager.spi.TemplateManager#merge(java.lang.String, java.io.Writer, java.util.Map, java.lang.String)
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
					TemplateManagerExceptionCodeConstant.TEMPLATE_PARSING.getErrorMessage(), e);
		}
		return isMerged;
	}

}
