package io.mosip.kernel.templatemanager.velocity.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import io.mosip.kernel.core.spi.templatemanager.MosipTemplateManager;
import io.mosip.kernel.templatemanager.velocity.constant.TemplateManagerConstant;
import io.mosip.kernel.templatemanager.velocity.constant.TemplateManagerExceptionCodeConstant;
import io.mosip.kernel.templatemanager.velocity.exception.TemplateMethodInvocationException;
import io.mosip.kernel.templatemanager.velocity.exception.TemplateParsingException;
import io.mosip.kernel.templatemanager.velocity.exception.TemplateResourceNotFoundException;
import io.mosip.kernel.templatemanager.velocity.util.TemplateManagerUtil;

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
	private static final String DEFAULT_ENCODING_TYPE = "UTF-8";
	private VelocityEngine velocityEngine;

	public TemplateManagerImpl(VelocityEngine engine) {
		this.velocityEngine = engine;
	}

	/**
	 * Method to merge template , where template content will be pass as inputSteam
	 * 
	 * @param is
	 *            the {@link InputStream} is template content .
	 * @param values
	 *            as Map&lt;String,Object&gt; where key will be placeholder name and
	 *            Object is the actual value for the placeholder
	 * @return template merged template content as {@link InputStream}
	 * @throws IOException
	 *             if an I/O exception occurs during writing to the writer
	 * 
	 */
	@Override
	public InputStream merge(InputStream is, Map<String, Object> values) throws IOException {
		StringWriter writer = new StringWriter();
		// logging tag name
		String logTag = "templateManager-mergeTemplate";
		Objects.requireNonNull(is, TemplateManagerConstant.TEMPLATE_INPUT_STREAM_NULL.getMessage());
		Objects.requireNonNull(values, TemplateManagerConstant.TEMPLATE_VALUES_NULL.getMessage());
		VelocityContext context = TemplateManagerUtil.bindInputToContext(values);
		try {
			boolean isMerged = false;
			if (context != null) {
				isMerged = velocityEngine.evaluate(context, writer, logTag, new InputStreamReader(is));
				if (isMerged)
					return new ByteArrayInputStream(writer.toString().getBytes());
			}
		} catch (ResourceNotFoundException e) {
			throw new TemplateResourceNotFoundException(
					TemplateManagerExceptionCodeConstant.TEMPLATE_NOT_FOUND.getErrorCode(),
					TemplateManagerExceptionCodeConstant.TEMPLATE_NOT_FOUND.getErrorMessage());
		} catch (ParseErrorException e) {
			throw new TemplateParsingException(TemplateManagerExceptionCodeConstant.TEMPLATE_PARSING.getErrorCode(),
					e.getMessage());
		} catch (MethodInvocationException e) {
			throw new TemplateMethodInvocationException(
					TemplateManagerExceptionCodeConstant.TEMPLATE_INVALID_REFERENCE.getErrorCode(),
					TemplateManagerExceptionCodeConstant.TEMPLATE_INVALID_REFERENCE.getErrorMessage());
		}
		return null;
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
	public boolean merge(String templateName, final Writer writer, Map<String, Object> values) throws IOException {
		return merge(templateName, writer, values, DEFAULT_ENCODING_TYPE);
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
	public boolean merge(String templateName, Writer writer, Map<String, Object> values, final String encodingType)
			throws IOException {
		boolean isMerged = false;
		Template template = null;
		VelocityContext context = null;
		// Null Checks
		Objects.requireNonNull(templateName, TemplateManagerConstant.TEMPATE_NAME_NULL.getMessage());
		Objects.requireNonNull(writer, TemplateManagerConstant.WRITER_NULL.getMessage());
		Objects.requireNonNull(encodingType, TemplateManagerConstant.ENCODING_TYPE_NULL.getMessage());
		Objects.requireNonNull(values, TemplateManagerConstant.TEMPLATE_VALUES_NULL.getMessage());
		try {
			template = velocityEngine.getTemplate(templateName, encodingType);
			// create context by using provided map of values
			context = TemplateManagerUtil.bindInputToContext(values);
			template.merge(context, writer);
			isMerged = true;
		} catch (ResourceNotFoundException e) {
			throw new TemplateResourceNotFoundException(
					TemplateManagerExceptionCodeConstant.TEMPLATE_NOT_FOUND.getErrorCode(),
					TemplateManagerExceptionCodeConstant.TEMPLATE_NOT_FOUND.getErrorMessage());
		} catch (ParseErrorException e) {
			throw new TemplateParsingException(TemplateManagerExceptionCodeConstant.TEMPLATE_PARSING.getErrorCode(),
					e.getMessage());
		} catch (MethodInvocationException e) {
			throw new TemplateMethodInvocationException(
					TemplateManagerExceptionCodeConstant.TEMPLATE_INVALID_REFERENCE.getErrorCode(),
					TemplateManagerExceptionCodeConstant.TEMPLATE_INVALID_REFERENCE.getErrorMessage());
		}
		return isMerged;
	}

}
