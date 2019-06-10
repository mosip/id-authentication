package io.mosip.authentication.common.service.integration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.LanguageType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;

/**
 * 
 * Manage fetching / applying Templates based on entity.
 *
 * @author Dinesh Karuppiah.T
 */
@Component
public class IdTemplateManager {

	private static final String BOTH = "BOTH";

	/** The Constant TEMPLATE. */
	private static final String TEMPLATE = "Template";

	/** The Constant PRIMARY. */
	private static final String PRIMARY = "primary";

	/** The Constant NOTIFICATION_LANGUAGE_SUPPORT. */

	/** Class path. */
	private static final String CLASSPATH = "classpath";

	/** UTF type. */
	private static final String ENCODE_TYPE = "UTF-8";

	/** The template manager to apply template for eKyc */
	private TemplateManager templateManager;

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
		String languageRequired = environment.getProperty(IdAuthConfigKeyConstants.MOSIP_NOTIFICATION_LANGUAGE_TYPE);
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

}
