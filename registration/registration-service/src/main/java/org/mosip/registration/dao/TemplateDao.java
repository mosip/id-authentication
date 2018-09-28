package org.mosip.registration.dao;

import java.util.List;

import org.mosip.registration.entity.Template;
import org.mosip.registration.entity.TemplateFileFormat;
import org.mosip.registration.entity.TemplateType;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateDao {

	List<Template> getAllTemplates();
	List<TemplateType> getAllTemplateTypes();
	List<TemplateFileFormat> getAllTemplateFileFormats();
}
