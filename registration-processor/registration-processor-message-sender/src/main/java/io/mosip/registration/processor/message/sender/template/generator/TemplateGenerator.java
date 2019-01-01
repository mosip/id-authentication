package io.mosip.registration.processor.message.sender.template.generator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.templatemanager.exception.TemplateMethodInvocationException;
import io.mosip.kernel.core.templatemanager.exception.TemplateParsingException;
import io.mosip.kernel.core.templatemanager.exception.TemplateResourceNotFoundException;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.templatemanager.velocity.impl.TemplateManagerImpl;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.notification.template.generator.dto.TemplateResponseDto;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.message.sender.exception.TemplateNotFoundException;
import io.mosip.registration.processor.message.sender.exception.TemplateProcessingFailureException;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@Component
public class TemplateGenerator {

	private static Logger log = LoggerFactory.getLogger(TemplateGenerator.class);
	private static final String TEMPLATES = "templates";

	private String resourceLoader = "classpath";
	private String templatePath = ".";
	private boolean cache = Boolean.TRUE;
	private String defaultEncoding = StandardCharsets.UTF_8.name();

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	public String templateGenerator(String templateTypeCode, Map<String, Object> attributes, String langCode)
			throws IOException {
		String artifact = null;

		try {
			List<String> pathSegments = new ArrayList<>();
			pathSegments.add(TEMPLATES);
			pathSegments.add(langCode);
			pathSegments.add(templateTypeCode);
			TemplateResponseDto template = (TemplateResponseDto) restClientService.getApi(ApiName.MASTER, pathSegments,
					"", "", TemplateResponseDto.class);

			InputStream is = new ByteArrayInputStream(
					template.getTemplates().iterator().next().getFileText().getBytes());

			InputStream out = getTemplateManager().merge(is, attributes);

			StringWriter writer = new StringWriter();
			IOUtils.copy(out, writer, "UTF-8");

			artifact = writer.toString();

		} catch (ApisResourceAccessException e) {
			throw new TemplateNotFoundException(PlatformErrorMessages.RPR_TEM_NOT_FOUND.getCode());
		} catch (TemplateResourceNotFoundException | TemplateParsingException | TemplateMethodInvocationException e) {
			throw new TemplateProcessingFailureException(PlatformErrorMessages.RPR_TEM_PROCESSING_FAILURE.getCode());
		} catch (Exception e) {
			log.error("Template was not generated due to some internal error", e);
			throw new TemplateProcessingFailureException(PlatformErrorMessages.RPR_TEM_PROCESSING_FAILURE.getCode());
		}

		return artifact;
	}

	public TemplateManager getTemplateManager() {
		final Properties properties = new Properties();
		properties.put(RuntimeConstants.INPUT_ENCODING, defaultEncoding);
		properties.put(RuntimeConstants.OUTPUT_ENCODING, defaultEncoding);
		properties.put(RuntimeConstants.ENCODING_DEFAULT, defaultEncoding);
		properties.put(RuntimeConstants.RESOURCE_LOADER, resourceLoader);
		properties.put(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templatePath);
		properties.put(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, cache);
		properties.put(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, NullLogChute.class.getName());
		properties.put("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		properties.put("file.resource.loader.class", FileResourceLoader.class.getName());
		VelocityEngine engine = new VelocityEngine(properties);
		engine.init();
		return new TemplateManagerImpl(engine);
	}

}
