package io.mosip.registration.service.template.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.dao.TemplateDao;
import io.mosip.registration.entity.Template;
import io.mosip.registration.entity.TemplateFileFormat;
import io.mosip.registration.entity.TemplateType;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.template.TemplateService;

/**
 * Template Service for choosing the required template for acknowledgement
 * 
 * @author Himaja Dhanyamraju
 *
 */
@Service
public class TemplateServiceImpl implements TemplateService {

	@Autowired
	private TemplateDao templateDao;

	/**
	 * This method takes the list of templates, template file formats and template
	 * types from database and chooses the required template for creation of
	 * acknowledgement
	 * 
	 * * @param templateName 
	 *            to define the template name
	 * 
	 * @return single template
	 */

	public Template getTemplate(String templateName) {
		Template ackTemplate = new Template();

		List<Template> templates = templateDao.getAllTemplates();
		List<TemplateType> templateTypes = templateDao.getAllTemplateTypes();
		List<TemplateFileFormat> templateFileFormats = templateDao.getAllTemplateFileFormats();

		/*
		 * choosing a template for which the code is matched with template_type_code and
		 * template_file_format_code
		 */
		for (Template template : templates) {
			if (template.getName().equals(templateName)) {
			for (TemplateType type : templateTypes) {
				if (template.getLangCode().equals(type.getPkTmpltCode().getLangCode()) && template.getTemplateTypCode().equals(type.getPkTmpltCode().getCode())) {
					for (TemplateFileFormat fileFormat : templateFileFormats) {
						if (template.getLangCode().equals(fileFormat.getPkTfftCode().getLangCode()) && template.getFileFormatCode().equals(fileFormat.getPkTfftCode().getCode())) {
							ackTemplate = template;
						}
					}
				}
			}
		}
		}
		return ackTemplate;
	}
	

	public String getHtmlTemplate(String templateName) throws RegBaseCheckedException {
		return getTemplate(templateName).getFileTxt();
	}
}
