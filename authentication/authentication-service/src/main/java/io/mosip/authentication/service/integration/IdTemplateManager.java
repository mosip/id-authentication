package io.mosip.authentication.service.integration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

@Component
public class IdTemplateManager {

	private static final String MOSIP_NOTIFICATION_LANGUAGE_TYPE = "mosip.notification.language-type";


	private static final String BOTH = "BOTH";


	/** The Constant TEMPLATE. */
	private static final String TEMPLATE = "Template";

	/** The Constant PRIMARY. */
	private static final String PRIMARY = "primary";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SESSION_ID";

	/** The Constant NOTIFICATION_LANGUAGE_SUPPORT. */

	/** Class path. */
	private static final String CLASSPATH = "classpath";

	/** UTF type. */
	private static final String ENCODE_TYPE = "UTF-8";

	/** The template manager to apply template for eKyc */
	private TemplateManager templateManager;

	/** PDF Generator for eKYC document */
	private PDFGenerator pdfGenerator;

	/**
	 * Template Manager Builder to build templates
	 */
	@Autowired
	private TemplateManagerBuilder templateManagerBuilder;

	/**
	 * To integrate Master data from Kernal
	 */
	@Autowired
	private MasterDataManager masterDataManager;

	/**
	 * The environment
	 */
	@Autowired
	private Environment environment;

	@Autowired
	private IdInfoFetcher idInfoFetcher;

	/**
	 * Id template manager post construct.
	 */
	@PostConstruct
	public void idTemplateManagerPostConstruct() {
		templateManager = templateManagerBuilder.encodingType(ENCODE_TYPE).enableCache(false).resourceLoader(CLASSPATH)
				.build();
	}

	/**
	 * IdTemplate Manager Logger
	 */
	private static Logger logger = IdaLogger.getLogger(IdTemplateManager.class);

	/**
	 * To apply Template for PDF Generation.
	 *
	 * @param templateName - template name for pdf format
	 * @param values       - list of contents
	 * @return the string
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 * @throws IOException                       Signals that an I/O exception has
	 *                                           occurred.
	 */
	public String applyTemplate(String templateName, Map<String, Object> values)
			throws IdAuthenticationBusinessException, IOException {
		Objects.requireNonNull(templateName);
		Objects.requireNonNull(values);
		StringWriter writer = new StringWriter();
		InputStream templateValue;
		String fetchedTemplate = fetchTemplate(templateName);
		templateValue = templateManager
				.merge(new ByteArrayInputStream(fetchedTemplate.getBytes(StandardCharsets.UTF_8)), values);
		if (templateValue != null) {
			IOUtils.copy(templateValue, writer, StandardCharsets.UTF_8);
			return writer.toString();
		} else {
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), TEMPLATE));
		}
	}

	/**
	 * Fetch Templates for e-KYC based on Template name
	 * 
	 * @param templateName
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	public String fetchTemplate(String templateName) throws IdAuthenticationBusinessException {
		String languageRequired = environment.getProperty(MOSIP_NOTIFICATION_LANGUAGE_TYPE);
		StringBuilder stringBuilder = new StringBuilder();
		if (languageRequired.equalsIgnoreCase(BOTH)) {
			stringBuilder.append(masterDataManager.fetchTemplate(templateName));
		} else if (languageRequired.equalsIgnoreCase(PRIMARY)) {
			stringBuilder.append(masterDataManager
					.fetchTemplate(idInfoFetcher.getLanguageCode(LanguageType.PRIMARY_LANG), templateName));
		} else {
			// TODO throw exception
		}
		return stringBuilder.toString();
	}

	/**
	 * Generate PDF for e-KYC.
	 *
	 * @param templateName the template name
	 * @param values       the values
	 * @return the output stream
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public OutputStream generatePDF(String templateName, Map<String, Object> values)
			throws IdAuthenticationBusinessException {
		try {
			String template = applyTemplate(templateName, values);
			Objects.requireNonNull(template);
			return pdfGenerator.generate(new ByteArrayInputStream(template.getBytes()));
		} catch (IOException e) {
			logger.error(SESSION_ID, "Inside generatePDF >>>>>", e.getMessage(), e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}

	}

}
