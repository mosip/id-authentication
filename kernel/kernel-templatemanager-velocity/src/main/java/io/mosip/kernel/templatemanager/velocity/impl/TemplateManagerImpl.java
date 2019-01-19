package io.mosip.kernel.templatemanager.velocity.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import io.mosip.kernel.core.templatemanager.exception.TemplateMethodInvocationException;
import io.mosip.kernel.core.templatemanager.exception.TemplateParsingException;
import io.mosip.kernel.core.templatemanager.exception.TemplateResourceNotFoundException;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.templatemanager.velocity.constant.TemplateManagerConstant;
import io.mosip.kernel.templatemanager.velocity.constant.TemplateManagerExceptionCodeConstant;
import io.mosip.kernel.templatemanager.velocity.util.TemplateManagerUtil;

/**
 * Implementation of {@link TemplateManager} which uses Velocity Template
 * Engine, TemplateManagerImpl will merge the template with values.
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 01-10-2018
 */
public class TemplateManagerImpl implements TemplateManager {
	private static final String DEFAULT_ENCODING_TYPE = StandardCharsets.UTF_8.name();
	private VelocityEngine velocityEngine;

	public TemplateManagerImpl(VelocityEngine engine) {
		this.velocityEngine = engine;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.templatemanager.spi.TemplateManager#merge(java.io.
	 * InputStream, java.util.Map)
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
					TemplateManagerExceptionCodeConstant.TEMPLATE_PARSING.getErrorMessage(), e);
		} catch (MethodInvocationException e) {
			throw new TemplateMethodInvocationException(
					TemplateManagerExceptionCodeConstant.TEMPLATE_INVALID_REFERENCE.getErrorCode(),
					TemplateManagerExceptionCodeConstant.TEMPLATE_INVALID_REFERENCE.getErrorMessage());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.templatemanager.spi.TemplateManager#merge(java.lang.
	 * String, java.io.Writer, java.util.Map)
	 */
	@Override
	public boolean merge(String templateName, final Writer writer, Map<String, Object> values) throws IOException {
		return merge(templateName, writer, values, DEFAULT_ENCODING_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.templatemanager.spi.TemplateManager#merge(java.lang.
	 * String, java.io.Writer, java.util.Map, java.lang.String)
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
					TemplateManagerExceptionCodeConstant.TEMPLATE_PARSING.getErrorMessage(), e);
		} catch (MethodInvocationException e) {
			throw new TemplateMethodInvocationException(
					TemplateManagerExceptionCodeConstant.TEMPLATE_INVALID_REFERENCE.getErrorCode(),
					TemplateManagerExceptionCodeConstant.TEMPLATE_INVALID_REFERENCE.getErrorMessage());
		}
		return isMerged;
	}

}
