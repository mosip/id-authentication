package org.mosip.registration.service;

import static org.mosip.registration.constants.RegProcessorExceptionEnum.REG_TEMPLATE_IO_EXCEPTION;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.mosip.registration.constants.RegConstants;
import org.mosip.registration.dao.TemplateDao;
import org.mosip.registration.entity.Template;
import org.mosip.registration.entity.TemplateFileFormat;
import org.mosip.registration.entity.TemplateType;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Template Manager for choosing the required template for acknowledgement
 * 
 * @author M1044292
 *
 */
@Service
public class TemplateService {

	@Autowired
	private TemplateDao templateDao;

	/**
	 * takes the list of templates, template file formats and template types from
	 * database and chooses the required template for creation of acknowledgement
	 * 
	 * @return single template
	 */
	public Template getTemplate() {
		Template ackTemplate = new Template();

		List<Template> templates = templateDao.getAllTemplates();
		List<TemplateType> templateTypes = templateDao.getAllTemplateTypes();
		List<TemplateFileFormat> templateFileFormats = templateDao.getAllTemplateFileFormats();

		// choosing a template for which the code is matched with template_type_code and
		// template_file_format_code
		for (Template template : templates) {
			for (TemplateType type : templateTypes) {
				if (template.getLang_code().equals(type.getPk_tmplt_code().getLang_code())) {
					for (TemplateFileFormat fileFormat : templateFileFormats) {
						if (template.getLang_code().equals(fileFormat.getPk_tfft_code().getLang_code())) {
							ackTemplate = template;
						}
					}
				}
			}
		}
		return ackTemplate;
	}

	/**
	 * creates a vm file and stores the template data coming from the database into
	 * the file
	 * 
	 * @return vm file in which the template is loaded
	 * @throws RegBaseCheckedException
	 */
	public File createReceipt() throws RegBaseCheckedException {
		Template template = getTemplate();
		File ackTemplate = new File(RegConstants.TEMPLATE_PATH);

		try (FileWriter fileWriter = new FileWriter(ackTemplate)) {
			// check if file exist, otherwise create the file before writing
			fileWriter.write(template.getFile_txt());
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(REG_TEMPLATE_IO_EXCEPTION.getErrorCode(),
					REG_TEMPLATE_IO_EXCEPTION.getErrorMessage());
		}
		return ackTemplate;
	}
}
