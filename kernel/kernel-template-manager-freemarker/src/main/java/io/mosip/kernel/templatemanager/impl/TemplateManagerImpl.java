package io.mosip.kernel.templatemanager.impl;

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
import io.mosip.kernel.templatemanager.constants.NullParamMessageConstant;
import io.mosip.kernel.templatemanager.constants.TemplateManagerExceptionCodeConstants;
import io.mosip.kernel.templatemanager.exception.TemplateIOException;
import io.mosip.kernel.templatemanager.exception.TemplateParsingException;
import io.mosip.kernel.templatemanager.exception.TemplateResourceNotFoundException;

/**
 * Mosip Template Manager wrap the template with values and return back
 * 
 * @author Abhishek Kumar
 * @since 2018-10-03
 * @version 1.0
 */

public class TemplateManagerImpl implements MosipTemplateManager {
	private Configuration configuration;

	public TemplateManagerImpl(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * @param data
	 *            as InputStream
	 * @param values
	 *            as Map
	 * @return as InputStream
	 */
	@Override
	public InputStream mergeTemplate(InputStream data, Map<String, Object> values) {
		Reader reader = null;
		Template template = null;
		StringWriter writer = new StringWriter();
		Objects.requireNonNull(data, NullParamMessageConstant.TEMPLATE_INPUT_STREAM.getMessage());
		Objects.requireNonNull(values, NullParamMessageConstant.TEMPLATE_VALUES.getMessage());
			try {
				reader = new InputStreamReader(data);
				template = new Template("template", reader, configuration);
				template.process(values, writer);
				return new ByteArrayInputStream(writer.toString().getBytes());
			} catch (TemplateException e) {
				throw new TemplateParsingException(
						TemplateManagerExceptionCodeConstants.TEMPLATE_PARSING.getErrorCode(),
						TemplateManagerExceptionCodeConstants.TEMPLATE_PARSING.getErrorMessage());
			} catch (IOException e) {
				throw new TemplateIOException(
						TemplateManagerExceptionCodeConstants.TEMPLATE_WRITER_EXCEPTION.getErrorCode(),
						TemplateManagerExceptionCodeConstants.TEMPLATE_WRITER_EXCEPTION.getErrorMessage());
			}
	}

	/**
	 * This merge method will merge template with values with default encoding UTF-8
	 * 
	 * @param templateName
	 * @param writer
	 * @param values
	 *            as Map
	 */
	@Override
	public boolean merge(String templateName, Writer writer, Map<String, Object> values) {

		return merge(templateName, writer, values, configuration.getDefaultEncoding());
	}

	/**
	 * @param templateName
	 * @param writer
	 * @param values
	 *            as Map
	 * @param encodingType
	 */
	@Override
	public boolean merge(String templateName, Writer writer, Map<String, Object> values, String encodingType) {
		boolean result = false;
		Objects.requireNonNull(templateName, NullParamMessageConstant.TEMPATE_NAME.getMessage());
		Objects.requireNonNull(writer, NullParamMessageConstant.WRITER.getMessage());
		Objects.requireNonNull(encodingType, NullParamMessageConstant.ENCODING_TYPE.getMessage());
		Objects.requireNonNull(values, NullParamMessageConstant.TEMPLATE_VALUES.getMessage());
			try {
				Template template = configuration.getTemplate(templateName, encodingType);
				template.process(values, writer);
				result = true;
			} catch (TemplateNotFoundException e) {
				throw new TemplateResourceNotFoundException(
						TemplateManagerExceptionCodeConstants.TEMPLATE_NOT_FOUND.getErrorCode(),
						TemplateManagerExceptionCodeConstants.TEMPLATE_NOT_FOUND.getErrorMessage());
			} catch (TemplateException e) {
				throw new TemplateParsingException(
						TemplateManagerExceptionCodeConstants.TEMPLATE_PARSING.getErrorCode(), e.getMessage());
			} catch (IOException e) {
				throw new TemplateIOException(
						TemplateManagerExceptionCodeConstants.TEMPLATE_WRITER_EXCEPTION.getErrorCode(),
						TemplateManagerExceptionCodeConstants.TEMPLATE_WRITER_EXCEPTION.getErrorMessage());
			}
		return result;
	}

}
