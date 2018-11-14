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
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateConfigureBuilder;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

@Component
public class IdTemplateManager {

	private PDFGenerator pdfGenerator;

	private static final String CLASSPATH = "classpath";

	private static final String ENCODE_TYPE = "UTF-8";

	private static final String TEMPLATES = "/templates";

	private static Logger logger = IdaLogger.getLogger(IdTemplateManager.class);

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
			try {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.MISSING_TEMPLATE_CONFIG);
			} catch (IdAuthenticationBusinessException e) {
				logger.error("NA", "Inside Apply Template >>>>>", e.getErrorCode(), e.getErrorText());
			}
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
			logger.error("NA", "Inside generatePDF >>>>>", e.getMessage(), e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PDF_NOT_GENERATED, e);
		}

	}

}
