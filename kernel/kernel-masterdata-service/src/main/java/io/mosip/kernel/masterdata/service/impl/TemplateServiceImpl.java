package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.constant.TemplateErrorCode;
import io.mosip.kernel.masterdata.dto.TemplateDto;
import io.mosip.kernel.masterdata.entity.Template;
import io.mosip.kernel.masterdata.exception.TemplateFetchException;
import io.mosip.kernel.masterdata.exception.TemplateMappingException;
import io.mosip.kernel.masterdata.exception.TemplateNotFoundException;
import io.mosip.kernel.masterdata.repository.TemplateRepository;
import io.mosip.kernel.masterdata.service.TemplateService;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

@Service
public class TemplateServiceImpl implements TemplateService {

	@Autowired
	private TemplateRepository templateRepository;

	@Autowired
	private ObjectMapperUtil objectMapperUtil;

	private List<Template> templateList;

	private List<TemplateDto> templateDtoList;

	/**
	 * To fetch all the {@link Template} based on language code
	 * 
	 * @return {@link List<TemplateDto>}
	 */
	@Override
	public List<TemplateDto> getAllTemplate() {
		try {
			templateList = templateRepository.findAll(Template.class);
		} catch(DataAccessException exception) {
			throw new TemplateFetchException(TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorCode(), TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorMessage());
		}
		if(!(templateList.isEmpty())) {
			try {
				templateDtoList = objectMapperUtil.mapAll(templateList, TemplateDto.class);
			} catch(IllegalArgumentException | ConfigurationException | MappingException excetion) {
				throw new TemplateMappingException(TemplateErrorCode.TEMPLATE_MAPPING_EXCEPTION.getErrorCode(), TemplateErrorCode.TEMPLATE_MAPPING_EXCEPTION.getErrorMessage());
			}
		} else {
			throw new TemplateNotFoundException(TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorCode(), TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorMessage());
		}
		return templateDtoList;
	}

	/**
	 * To fetch all the {@link Template} based on language code
	 * 
	 * @param languageCode
	 * @return {@link List<Template>}
	 */
	@Override
	public List<TemplateDto> getAllTemplateByLanguageCode(String languageCode) {
		try {
			templateList = templateRepository.findAllByLanguageCode(languageCode);
		} catch(DataAccessException exception) {
			throw new TemplateFetchException(TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorCode(), TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorMessage());
		}
		if(!(templateList.isEmpty())) {
			try {
				templateDtoList = objectMapperUtil.mapAll(templateList, TemplateDto.class);
			} catch(IllegalArgumentException | ConfigurationException | MappingException exception) {
				throw new TemplateMappingException(TemplateErrorCode.TEMPLATE_MAPPING_EXCEPTION.getErrorCode(), TemplateErrorCode.TEMPLATE_MAPPING_EXCEPTION.getErrorMessage());
			}
		} else {
			throw new TemplateNotFoundException(TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorCode(), TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorMessage());
		}
		return templateDtoList;
	}

	/**
	 * To fetch all the {@link Template} based on language code and template type
	 * code
	 * 
	 * @param languageCode
	 * @param templateTypeCode
	 * @return {@link List<Template>}
	 */
	@Override
	public List<TemplateDto> getAllTemplateByLanguageCodeAndTemplateTypeCode(String languageCode,
			String templateTypeCode) {
		try {
			templateList = templateRepository.findAllByLanguageCodeAndTemplateTypeCode(languageCode, templateTypeCode);
		} catch (DataAccessException exception) {
			throw new TemplateFetchException(TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorCode(), TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorMessage());
		}
		if(!(templateList.isEmpty())) {
			try {
				templateDtoList = objectMapperUtil.mapAll(templateList, TemplateDto.class);
			} catch(IllegalArgumentException | ConfigurationException | MappingException exception) {
				throw new TemplateMappingException(TemplateErrorCode.TEMPLATE_MAPPING_EXCEPTION.getErrorCode(), TemplateErrorCode.TEMPLATE_MAPPING_EXCEPTION.getErrorMessage());
			}
		} else {
			throw new TemplateNotFoundException(TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorCode(), TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorMessage());
		}
		return templateDtoList;
	}

}
