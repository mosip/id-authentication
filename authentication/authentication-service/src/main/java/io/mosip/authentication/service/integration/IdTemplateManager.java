package io.mosip.authentication.service.integration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Component;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.kernel.core.spi.pdfgenerator.PdfGenerator;
import io.mosip.kernel.core.spi.templatemanager.TemplateManager;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateConfigureBuilder;

@Component
public class IdTemplateManager {

	private PdfGenerator pdfGenerator;

	private static final String CLASSPATH = "classpath";

	private static final String ENCODE_TYPE = "UTF-8";

	private static final String TEMPLATES = "/templates";

	private TemplateManager templateManager = new TemplateConfigureBuilder().encodingType(ENCODE_TYPE)
			.enableCache(false).resourcePath(TEMPLATES).resourceLoader(CLASSPATH).build();

	public String applyTemplate(String templateName, Map<String, Object> values) throws IOException {
		Objects.requireNonNull(templateName);
		Objects.requireNonNull(values);
		StringWriter writer = new StringWriter();
		boolean isTemplateAvail = templateManager.merge(templateName, writer, values);
		if (isTemplateAvail) {
			return writer.toString();
		} else {
			// FIXME template not available
		}
		return "";
	}

	public OutputStream generatePDF(String templateName, Map<String, Object> values)
			throws IdAuthenticationBusinessException {
		try {
			String template = applyTemplate(templateName, values);
			Objects.requireNonNull(template);
			return pdfGenerator.generate(new ByteArrayInputStream(template.getBytes()));
		} catch (IOException e) {
			// FIXME throw new business exception
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.AD_FAD_MUTUALLY_EXCULUSIVE, e);
		}

	}

}
