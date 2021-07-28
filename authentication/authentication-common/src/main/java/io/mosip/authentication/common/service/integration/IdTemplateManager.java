package io.mosip.authentication.common.service.integration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;

/**
 * 
 * Manage fetching / applying Templates based on entity.
 *
 * @author Dinesh Karuppiah.T
 * @author Nagarjuna
 */
@Component
public class IdTemplateManager {
	
	/** The Constant TEMPLATE. */
	private static final String TEMPLATE = "Template";	

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
	public String applyTemplate(String templateName, Map<String, Object> values, List<String> templateLanguages)
			throws IdAuthenticationBusinessException, IOException {
		Objects.requireNonNull(templateName);
		Objects.requireNonNull(values);
		StringWriter writer = new StringWriter();
		InputStream templateValue;
		String fetchedTemplate = fetchTemplate(templateName, templateLanguages);
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
	 * Fetch Templates for e-KYC based on Template name.
	 *
	 * @param templateName the template name
	 * @return the string
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public String fetchTemplate(String templateName, List<String> templateLanguages) throws IdAuthenticationBusinessException {		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(masterDataManager.fetchTemplate(templateName, templateLanguages));
		return stringBuilder.toString();
	}

}