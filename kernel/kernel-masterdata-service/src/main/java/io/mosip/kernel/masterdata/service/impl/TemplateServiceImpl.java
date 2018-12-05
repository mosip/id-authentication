package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.TemplateErrorCode;
import io.mosip.kernel.masterdata.dto.TemplateDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.Template;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.TemplateRepository;
import io.mosip.kernel.masterdata.service.TemplateService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

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

	@Autowired
	private MetaDataUtils metaUtils;

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
			templateList = templateRepository.findAllByLangCodeAndIsDeletedFalse(languageCode);
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
			templateList = templateRepository.findAllByLangCodeAndTemplateTypeCodeAndIsDeletedFalse(languageCode,
					templateTypeCode);
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

	@Override
	public IdResponseDto createTemplate(TemplateDto template) {
		Template entity = metaUtils.setCreateMetaData(template, Template.class);
		Template templateEntity;
		try {
			templateEntity = templateRepository.create(entity);

		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(TemplateErrorCode.TEMPLATE_INSERT_EXCEPTION.getErrorCode(),
					TemplateErrorCode.TEMPLATE_INSERT_EXCEPTION.getErrorMessage() + "  "
							+ ExceptionUtils.parseException(e));
		}

		IdResponseDto idResponseDto = new IdResponseDto();
		objectMapperUtil.mapNew(templateEntity, idResponseDto);

		return idResponseDto;
	}

}
