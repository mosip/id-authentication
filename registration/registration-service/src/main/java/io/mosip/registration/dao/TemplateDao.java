package io.mosip.registration.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.registration.entity.Template;
import io.mosip.registration.entity.TemplateFileFormat;
import io.mosip.registration.entity.TemplateType;

@Repository
public interface TemplateDao {

	List<Template> getAllTemplates();
	List<TemplateType> getAllTemplateTypes();
	List<TemplateFileFormat> getAllTemplateFileFormats();
}
