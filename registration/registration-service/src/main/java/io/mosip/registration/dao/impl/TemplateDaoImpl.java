package io.mosip.registration.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.registration.dao.TemplateDao;
import io.mosip.registration.entity.Template;
import io.mosip.registration.entity.TemplateFileFormat;
import io.mosip.registration.entity.TemplateType;
import io.mosip.registration.repositories.TemplateFileFormatRepository;
import io.mosip.registration.repositories.TemplateRepository;
import io.mosip.registration.repositories.TemplateTypeRepository;

/**
 * DaoImpl for calling the respective template repositories and getting data from database
 * 
 * @author Himaja Dhanyamraju
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
		return templateRepository.findByIsActiveTrue();
	}
	
	public List<TemplateType> getAllTemplateTypes(){
		return typeRepository.findByIsActiveTrue();
	}
	
	public List<TemplateFileFormat> getAllTemplateFileFormats(){
		return fileFormatRepository.findByIsActiveTrue();
	}	
}
