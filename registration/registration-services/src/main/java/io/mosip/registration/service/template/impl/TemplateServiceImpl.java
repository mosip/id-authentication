package io.mosip.registration.service.template.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.TemplateDao;
import io.mosip.registration.entity.Template;
import io.mosip.registration.entity.TemplateFileFormat;
import io.mosip.registration.entity.TemplateType;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.template.TemplateService;

/**
 * Implementation class for {@link TemplateService}
 * 
 * @author Himaja Dhanyamraju
 *
 */
@Service
public class TemplateServiceImpl implements TemplateService {

	private static final Logger LOGGER = AppConfig.getLogger(TemplateServiceImpl.class);

	@Autowired
	private TemplateDao templateDao;

	/**
	 * This method takes the list of templates, template file formats and template
	 * types from database and chooses the required template for creation of
	 * specific template
	 * 
	 * @param templateTypeCode
	 *            the specified template type code
	 * @param langCode
	 *            specified language code
	 * @return single template
	 */
	public Template getTemplate(String templateTypeCode, String langCode) {
		LOGGER.info("REGISTRATION - TEMPLATE_GENERATION - TEMPLATE_SERVICE_IMPL", APPLICATION_NAME, APPLICATION_ID,
				"Getting templates from database has been started");

		Template ackTemplate = new Template();
		try {
			List<Template> templates = templateDao.getAllTemplates(templateTypeCode);
			List<TemplateType> templateTypes = templateDao.getAllTemplateTypes(templateTypeCode, langCode);
			List<TemplateFileFormat> templateFileFormats = templateDao.getAllTemplateFileFormats();

			/*
			 * choosing a template for which the code is matched with template_type_code and
			 * template_file_format_code
			 */
			for (Template template : templates) {
				for (TemplateType type : templateTypes) {
					if (template.getLangCode().equals(type.getPkTmpltCode().getLangCode())) {
						for (TemplateFileFormat fileFormat : templateFileFormats) {
							if (template.getLangCode().equals(fileFormat.getPkTfftCode().getLangCode())
									&& template.getFileFormatCode().equals(fileFormat.getPkTfftCode().getCode())) {
								ackTemplate = template;
							}
						}
					}
				}
			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - TEMPLATE_GENERATION - TEMPLATE_SERVICE_IMPL", APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
			throw new RegBaseUncheckedException(RegistrationConstants.TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION,
					runtimeException.toString());
		}
		return ackTemplate;
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.template.TemplateService#getHtmlTemplate(java.lang.String, java.lang.String)
	 */
	public String getHtmlTemplate(String templateTypeCode, String langCode) {
		LOGGER.info("REGISTRATION - TEMPLATE_GENERATION - TEMPLATE_SERVICE_IMPL", APPLICATION_NAME, APPLICATION_ID,
				"Getting required template from DB started");

		String templateText = null;
		if (templateTypeCode != null && !templateTypeCode.isEmpty()
				&& getTemplate(templateTypeCode, langCode).getFileTxt() != null) {
			templateText = getTemplate(templateTypeCode, langCode).getFileTxt();
		}

		return templateText;
	}
}
