package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.constant.TemplateErrorCode;
import io.mosip.kernel.masterdata.dto.TemplateDto;
import io.mosip.kernel.masterdata.entity.Template;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.TemplateRepository;
import io.mosip.kernel.masterdata.service.TemplateService;
import io.mosip.kernel.masterdata.utils.MapperUtils;

/**
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@Service
public class TemplateServiceImpl implements TemplateService {

	@Autowired
	private TemplateRepository templateRepository;

	@Autowired
	private MapperUtils objectMapperUtil;

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
			templateList = templateRepository.findAllByIsDeletedFalse(Template.class);
		} catch (DataAccessException exception) {
			throw new MasterDataServiceException(TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorCode(),
					TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorMessage());
		}
		if (templateList != null && !templateList.isEmpty()) {
			templateDtoList = objectMapperUtil.mapAll(templateList, TemplateDto.class);
		} else {
			throw new DataNotFoundException(TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorCode(),
					TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorMessage());
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
			templateList = templateRepository.findAllByLanguageCodeAndIsDeletedFalse(languageCode);
		} catch (DataAccessException exception) {
			throw new MasterDataServiceException(TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorCode(),
					TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorMessage());
		}
		if (templateList != null && !templateList.isEmpty()) {
			templateDtoList = objectMapperUtil.mapAll(templateList, TemplateDto.class);
		} else {
			throw new DataNotFoundException(TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorCode(),
					TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorMessage());
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
			templateList = templateRepository.findAllByLanguageCodeAndTemplateTypeCodeAndIsDeletedFalse(languageCode, templateTypeCode);
		} catch (DataAccessException exception) {
			throw new MasterDataServiceException(TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorCode(),
					TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorMessage());
		}
		if (templateList != null && !templateList.isEmpty()) {
			templateDtoList = objectMapperUtil.mapAll(templateList, TemplateDto.class);
		} else {
			throw new DataNotFoundException(TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorCode(),
					TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorMessage());
		}
		return templateDtoList;
	}

}
