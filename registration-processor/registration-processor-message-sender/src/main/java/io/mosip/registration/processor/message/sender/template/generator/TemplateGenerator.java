package io.mosip.registration.processor.message.sender.template.generator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.templatemanager.exception.TemplateParsingException;
import io.mosip.kernel.core.templatemanager.exception.TemplateResourceNotFoundException;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.TemplateNotFoundException;
import io.mosip.registration.processor.core.exception.TemplateProcessingFailureException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.notification.template.generator.dto.TemplateResponseDto;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@Component
public class TemplateGenerator {

	private static Logger log = LoggerFactory.getLogger(TemplateGenerator.class);

	@Autowired
	private TemplateManager templateManager;

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	public String templateGenerator(String templateTypeCode, Map<String, Object> attributes, String langCode)
			throws IOException {
		String artifact = null;
		try {
			List<String> pathSegments = new ArrayList<>();
			pathSegments.add(langCode);
			pathSegments.add(templateTypeCode);
			TemplateResponseDto template = (TemplateResponseDto) restClientService.getApi(ApiName.MASTER,
					pathSegments, "", "", TemplateResponseDto.class);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(template);
			InputStream is = new ByteArrayInputStream(baos.toByteArray());

			InputStream out = templateManager.merge(is, attributes);

			StringWriter writer = new StringWriter();
			IOUtils.copy(out, writer, "UTF-8");

			artifact = writer.toString();

		} catch (ApisResourceAccessException e) {
			throw new TemplateNotFoundException(PlatformErrorMessages.RPR_TEM_NOT_FOUND.getCode());
		} catch (TemplateResourceNotFoundException | TemplateParsingException e) {
			throw new TemplateProcessingFailureException(PlatformErrorMessages.RPR_TEM_PROCESSING_FAILURE.getCode());
		} catch (Exception e) {
			log.error("Template was not generated due to some internal error");
		}

		return artifact;
	}

}
