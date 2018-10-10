package io.mosip.kernel.templatemanager.impl;

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
import io.mosip.kernel.templatemanager.constant.MosipTemplateManagerExceptionCodeConstants;
import io.mosip.kernel.templatemanager.constant.NullParamMessageConstant;
import io.mosip.kernel.templatemanager.exception.TemplateMethodInvocationException;
import io.mosip.kernel.templatemanager.exception.TemplateParsingException;
import io.mosip.kernel.templatemanager.exception.TemplateResourceNotFoundException;
import io.mosip.kernel.templatemanager.util.TemplateManagerUtil;

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
	 * @param template
	 *            as InputStream for template content
	 *            template content should be in the form of InputStream
	 * @param values
	 *            as Map
	 *            values should be pass as Map<String,Object> 
	 * @return template as InputStream
	 * 		merged given template content and values
	 * 
	 */
	public InputStream mergeTemplate(InputStream template, Map<String, Object> values) {
		StringWriter writer = new StringWriter();
		//logging tag name
		String logTag = "templateManager-mergeTemplate";
		Objects.requireNonNull(template, NullParamMessageConstant.TEMPLATE_INPUT_STREAM.getMessage());
		Objects.requireNonNull(values, NullParamMessageConstant.TEMPLATE_VALUES.getMessage());
		VelocityContext context = TemplateManagerUtil.bindInputToContext(values);
		try {
			boolean result = false;
			if (context != null) {
				result = velocityEngine.evaluate(context, writer, logTag, new InputStreamReader(template));
				if (result)
					return new ByteArrayInputStream(writer.toString().getBytes());
			}
		} catch (ResourceNotFoundException e) {
			throw new TemplateResourceNotFoundException(
					MosipTemplateManagerExceptionCodeConstants.TEMPLATE_NOT_FOUND.getErrorCode(),
					MosipTemplateManagerExceptionCodeConstants.TEMPLATE_NOT_FOUND.getErrorMessage());
		} catch (ParseErrorException e) {
			throw new TemplateParsingException(
					MosipTemplateManagerExceptionCodeConstants.TEMPLATE_PARSING.getErrorCode(),
					MosipTemplateManagerExceptionCodeConstants.TEMPLATE_PARSING.getErrorMessage());
		} catch (MethodInvocationException e) {
			throw new TemplateMethodInvocationException(
					MosipTemplateManagerExceptionCodeConstants.TEMPLATE_INVALID_REFERENCE.getErrorCode(),
					MosipTemplateManagerExceptionCodeConstants.TEMPLATE_INVALID_REFERENCE.getErrorMessage());
		}
		return null;
	}

	/**
	 * Method to merge template using default UTF-8 encoding
	 * 
	 * @param templateName as String
	 * @param writer 
	 * @param values
	 *           as Map
	 *            values should be pass as Map<String,Object> 
	 * @return boolean
	 *   return true if successfully merged given template and values
	 */
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
	public boolean merge(String templateName, Writer writer, Map<String, Object> values, final String encodingType) {
		boolean result = false;
		Template template = null;
		VelocityContext context = null;
		// Null Checks
		Objects.requireNonNull(templateName, NullParamMessageConstant.TEMPATE_NAME.getMessage());
		Objects.requireNonNull(writer, NullParamMessageConstant.WRITER.getMessage());
		Objects.requireNonNull(encodingType, NullParamMessageConstant.ENCODING_TYPE.getMessage());
		Objects.requireNonNull(values, NullParamMessageConstant.TEMPLATE_VALUES.getMessage());
		try {
			template = velocityEngine.getTemplate(templateName, encodingType);
			// create context by using provided map of values
			context = TemplateManagerUtil.bindInputToContext(values);
			template.merge(context, writer);
			result = true;
		} catch (ResourceNotFoundException e) {
			throw new TemplateResourceNotFoundException(
					MosipTemplateManagerExceptionCodeConstants.TEMPLATE_NOT_FOUND.getErrorCode(),
					MosipTemplateManagerExceptionCodeConstants.TEMPLATE_NOT_FOUND.getErrorMessage());
		} catch (ParseErrorException e) {
			throw new TemplateParsingException(
					MosipTemplateManagerExceptionCodeConstants.TEMPLATE_PARSING.getErrorCode(),
					MosipTemplateManagerExceptionCodeConstants.TEMPLATE_PARSING.getErrorMessage());
		} catch (MethodInvocationException e) {
			throw new TemplateMethodInvocationException(
					MosipTemplateManagerExceptionCodeConstants.TEMPLATE_INVALID_REFERENCE.getErrorCode(),
					MosipTemplateManagerExceptionCodeConstants.TEMPLATE_INVALID_REFERENCE.getErrorMessage());
		}
		return result;
	}

}
