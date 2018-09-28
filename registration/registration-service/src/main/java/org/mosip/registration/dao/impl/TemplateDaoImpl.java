package org.mosip.registration.dao.impl;

import java.util.List;

import org.mosip.registration.dao.TemplateDao;
import org.mosip.registration.entity.Template;
import org.mosip.registration.entity.TemplateFileFormat;
import org.mosip.registration.entity.TemplateType;
import org.mosip.registration.repositories.TemplateFileFormatRepository;
import org.mosip.registration.repositories.TemplateRepository;
import org.mosip.registration.repositories.TemplateTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * DaoImpl for calling the respective template repositories and getting data from database
 * @author Himaja Dhanyamraju
 * @since 1.0.0
 */
@Repository
public class TemplateDaoImpl implements TemplateDao{

	@Autowired
	private TemplateRepository<Template> templateRepository;
	
	@Autowired
	private TemplateTypeRepository<TemplateType> typeRepository;
	
	@Autowired
	private TemplateFileFormatRepository<TemplateFileFormat> fileFormatRepository;
	
	public List<Template> getAllTemplates(){
		return (List<Template>)templateRepository.findByIsActiveTrue();
	}
	
	public List<TemplateType> getAllTemplateTypes(){
		return (List<TemplateType>)typeRepository.findByIsActiveTrue();
	}
	
	public List<TemplateFileFormat> getAllTemplateFileFormats(){
		return (List<TemplateFileFormat>)fileFormatRepository.findByIsActiveTrue();
	}
	
}
