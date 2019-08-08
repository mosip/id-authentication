package io.mosip.registration.service.template.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.TemplateDao;
import io.mosip.registration.entity.Template;
import io.mosip.registration.entity.TemplateFileFormat;
import io.mosip.registration.entity.TemplateType;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.template.TemplateService#getHtmlTemplate(java.
	 * lang.String, java.lang.String)
	 */
	public String getHtmlTemplate(String templateTypeCode, String langCode) throws RegBaseCheckedException {
		LOGGER.info("REGISTRATION - TEMPLATE_GENERATION - TEMPLATE_SERVICE_IMPL", APPLICATION_NAME, APPLICATION_ID,
				"Getting required template from DB started");

		String templateText = null;

		if (nullCheckForTemplate(templateTypeCode, langCode)) {
			if (getTemplate(templateTypeCode, langCode).getFileTxt() != null) {
				templateText = getTemplate(templateTypeCode, langCode).getFileTxt();
			}
		} else {
			LOGGER.error("REGISTRATION - TEMPLATE_GENERATION - TEMPLATE_SERVICE_IMPL", APPLICATION_NAME, APPLICATION_ID,
					"Template Type Code / Lang code cannot be null");
			throw new RegBaseCheckedException(RegistrationExceptionConstants.TEMPLATE_CHECK_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.TEMPLATE_CHECK_EXCEPTION.getErrorMessage());
		}

		return templateText;
	}

	private boolean nullCheckForTemplate(String templateTypeCode, String langCode) {
		if (StringUtils.isEmpty(templateTypeCode)) {
			LOGGER.info("REGISTRATION - TEMPLATE_GENERATION - TEMPLATE_SERVICE_IMPL", APPLICATION_NAME, APPLICATION_ID,
					"Template Type Code is empty or null");
			return false;
		} else if (StringUtils.isEmpty(langCode)) {
			LOGGER.info("REGISTRATION - TEMPLATE_GENERATION - TEMPLATE_SERVICE_IMPL", APPLICATION_NAME, APPLICATION_ID,
					"Lang Code is empty or null");
			return false;
		} else {
			return true;
		}
	}
}
