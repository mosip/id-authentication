package io.mosip.kernel.templatemanager.velocity.impl;

import java.io.ByteArrayInputStream;
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
import io.mosip.kernel.templatemanager.velocity.constant.TemplateManagerExceptionCodeConstants;
import io.mosip.kernel.templatemanager.velocity.exception.TemplateMethodInvocationException;
import io.mosip.kernel.templatemanager.velocity.exception.TemplateParsingException;
import io.mosip.kernel.templatemanager.velocity.exception.TemplateResourceNotFoundException;
import io.mosip.kernel.templatemanager.velocity.util.TemplateManagerUtil;

/**
 * Implementation of @See {@link MosipTemplateManager} which uses Velocity
 * Template Engine internaly. TemplateManagerImpl will merge the template with
 * values.
 * 
 * @author Abhishek Kumar
 * @version 1.0
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
	 *            as InputStream for template content template content should be in
	 *            the form of InputStream
	 * @param values
	 *            as Map values should be pass as Map<String,Object>
	 * @return template as InputStream merged given template content and values
	 * 
	 */
	@Override
	public InputStream mergeTemplate(InputStream is, Map<String, Object> values) {
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
					TemplateManagerExceptionCodeConstants.TEMPLATE_NOT_FOUND.getErrorCode(),
					TemplateManagerExceptionCodeConstants.TEMPLATE_NOT_FOUND.getErrorMessage());
		} catch (ParseErrorException e) {
			throw new TemplateParsingException(
					TemplateManagerExceptionCodeConstants.TEMPLATE_PARSING.getErrorCode(),
					TemplateManagerExceptionCodeConstants.TEMPLATE_PARSING.getErrorMessage());
		} catch (MethodInvocationException e) {
			throw new TemplateMethodInvocationException(
					TemplateManagerExceptionCodeConstants.TEMPLATE_INVALID_REFERENCE.getErrorCode(),
					TemplateManagerExceptionCodeConstants.TEMPLATE_INVALID_REFERENCE.getErrorMessage());
		}
		return null;
	}

	/**
	 * Method to merge template using default UTF-8 encoding
	 * 
	 * @param templateName
	 *            as String
	 * @param writer
	 * @param values
	 *            as Map values should be pass as Map<String,Object>
	 * @return boolean return true if successfully merged given template and values
	 */
	@Override
	public boolean merge(String templateName, final Writer writer, Map<String, Object> values) {
		return merge(templateName, writer, values, DEFAULT_ENCODING_TYPE);
	}

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
	 */
	@Override
	public boolean merge(String templateName, Writer writer, Map<String, Object> values, final String encodingType) {
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
					TemplateManagerExceptionCodeConstants.TEMPLATE_NOT_FOUND.getErrorCode(),
					TemplateManagerExceptionCodeConstants.TEMPLATE_NOT_FOUND.getErrorMessage());
		} catch (ParseErrorException e) {
			throw new TemplateParsingException(
					TemplateManagerExceptionCodeConstants.TEMPLATE_PARSING.getErrorCode(),
					TemplateManagerExceptionCodeConstants.TEMPLATE_PARSING.getErrorMessage());
		} catch (MethodInvocationException e) {
			throw new TemplateMethodInvocationException(
					TemplateManagerExceptionCodeConstants.TEMPLATE_INVALID_REFERENCE.getErrorCode(),
					TemplateManagerExceptionCodeConstants.TEMPLATE_INVALID_REFERENCE.getErrorMessage());
		}
		return isMerged;
	}

}
