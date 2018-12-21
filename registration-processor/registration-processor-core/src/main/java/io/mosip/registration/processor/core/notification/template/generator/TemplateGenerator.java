package io.mosip.registration.processor.core.notification.template.generator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.templatemanager.exception.TemplateParsingException;
import io.mosip.kernel.core.templatemanager.exception.TemplateResourceNotFoundException;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.masterdata.dto.getresponse.TemplateResponseDto;
import io.mosip.registration.processor.core.exception.TemplateNotFoundException;
import io.mosip.registration.processor.core.exception.TemplateProcessingFailureException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@Component
public class TemplateGenerator {
	

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(TemplateGenerator.class);

	@Autowired
	private TemplateManager templateManager;

	@Value("${kernal.masterdata.uri}")
	private String kernalMasterDataUri;

	public String templateGenerator(String templateTypeCode, Map<String, Object> attributes, String langCode)
			throws IOException {
		RestTemplate restTemplate = new RestTemplate();
		String uri = kernalMasterDataUri + langCode + "/" + templateTypeCode;
		String artifact = null;
		try {
			TemplateResponseDto template = restTemplate.getForObject(uri, TemplateResponseDto.class);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(template);
			InputStream is = new ByteArrayInputStream(baos.toByteArray());

			InputStream out = templateManager.merge(is, attributes);

			StringWriter writer = new StringWriter();
			IOUtils.copy(out, writer, "UTF-8");

			artifact = writer.toString();

		} catch (RestClientException e) {
			throw new TemplateNotFoundException(PlatformErrorMessages.RPR_TEM_NOT_FOUND.getCode());
		} catch (TemplateResourceNotFoundException | TemplateParsingException e) {
			throw new TemplateProcessingFailureException(PlatformErrorMessages.RPR_TEM_PROCESSING_FAILURE.getCode());
		} catch( Exception e) {
			log.error("Template was not generated due to some internal error");
		}

		return artifact;
	}

}
