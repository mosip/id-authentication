package io.mosip.registration.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.dao.TemplateDao;
import io.mosip.registration.entity.Template;
import io.mosip.registration.entity.TemplateFileFormat;
import io.mosip.registration.entity.TemplateType;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Template Manager for choosing the required template for acknowledgement
 * 
 * @author M1044292
 *
 */
@Service
public class TemplateServiceImpl implements TemplateService {

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
				if (template.getLangCode().equals(type.getPkTmpltCode().getLangCode())) {
					for (TemplateFileFormat fileFormat : templateFileFormats) {
						if (template.getLangCode().equals(fileFormat.getPkTfftCode().getLangCode())) {
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
	public String createReceipt() throws RegBaseCheckedException {
		Template template = getTemplate();
		/*File ackTemplate = new File(RegConstants.TEMPLATE_PATH);

		try (FileWriter fileWriter = new FileWriter(ackTemplate)) {
			// check if file exist, otherwise create the file before writing
			fileWriter.write(template.getFileTxt());
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(REG_TEMPLATE_IO_EXCEPTION.getErrorCode(),
					REG_TEMPLATE_IO_EXCEPTION.getErrorMessage());
		}*/
		return template.getFileTxt();
	}
}
