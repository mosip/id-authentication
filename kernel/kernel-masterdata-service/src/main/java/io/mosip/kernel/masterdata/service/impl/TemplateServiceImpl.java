package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.masterdata.constant.TemplateErrorCode;
import io.mosip.kernel.masterdata.dto.TemplateDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.TemplateResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.TemplateExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.Template;
import io.mosip.kernel.masterdata.entity.id.IdAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.TemplateRepository;
import io.mosip.kernel.masterdata.service.TemplateService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MasterdataSearchHelper;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.PageUtils;
import io.mosip.kernel.masterdata.validator.FilterTypeValidator;

/**
 * 
 * @author Neha
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@Service
public class TemplateServiceImpl implements TemplateService {

	@Autowired
	private TemplateRepository templateRepository;

	private List<Template> templateList;

	private List<TemplateDto> templateDtoList;

	private TemplateResponseDto templateResponseDto = new TemplateResponseDto();
	
	@Autowired
	private FilterTypeValidator filterTypeValidator;
	
	@Autowired
	private MasterdataSearchHelper masterDataSearchHelper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.TemplateService#getAllTemplate()
	 */
	@Override
	public TemplateResponseDto getAllTemplate() {
		try {
			templateList = templateRepository.findAllByIsDeletedFalseOrIsDeletedIsNull(Template.class);
		} catch (DataAccessException | DataAccessLayerException exception) {
			throw new MasterDataServiceException(TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorCode(),
					TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(exception));
		}
		if (templateList != null && !templateList.isEmpty()) {
			templateDtoList = MapperUtils.mapAll(templateList, TemplateDto.class);
		} else {
			throw new DataNotFoundException(TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorCode(),
					TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorMessage());
		}
		templateResponseDto.setTemplates(templateDtoList);
		return templateResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.TemplateService#
	 * getAllTemplateByLanguageCode(java.lang.String)
	 */
	@Override
	public TemplateResponseDto getAllTemplateByLanguageCode(String languageCode) {
		try {
			templateList = templateRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(languageCode);
		} catch (DataAccessException | DataAccessLayerException exception) {
			throw new MasterDataServiceException(TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorCode(),
					TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(exception));
		}
		if (templateList != null && !templateList.isEmpty()) {
			templateDtoList = MapperUtils.mapAll(templateList, TemplateDto.class);
		} else {
			throw new DataNotFoundException(TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorCode(),
					TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorMessage());
		}
		templateResponseDto.setTemplates(templateDtoList);
		return templateResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.TemplateService#
	 * getAllTemplateByLanguageCodeAndTemplateTypeCode(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public TemplateResponseDto getAllTemplateByLanguageCodeAndTemplateTypeCode(String languageCode,
			String templateTypeCode) {
		try {
			templateList = templateRepository.findAllByLangCodeAndTemplateTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(
					languageCode, templateTypeCode);
		} catch (DataAccessException | DataAccessLayerException exception) {
			throw new MasterDataServiceException(TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorCode(),
					TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(exception));
		}
		if (templateList != null && !templateList.isEmpty()) {
			templateDtoList = MapperUtils.mapAll(templateList, TemplateDto.class);
		} else {
			throw new DataNotFoundException(TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorCode(),
					TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorMessage());
		}
		templateResponseDto.setTemplates(templateDtoList);
		return templateResponseDto;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.TemplateService#createTemplate(io.mosip.
	 * kernel.masterdata.dto.TemplateDto)
	 */
	@Override
	public IdAndLanguageCodeID createTemplate(TemplateDto template) {
		Template entity = MetaDataUtils.setCreateMetaData(template, Template.class);
		Template templateEntity;
		try {
			templateEntity = templateRepository.create(entity);

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(TemplateErrorCode.TEMPLATE_INSERT_EXCEPTION.getErrorCode(),
					TemplateErrorCode.TEMPLATE_INSERT_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}

		IdAndLanguageCodeID idAndLanguageCodeID = new IdAndLanguageCodeID();
		MapperUtils.map(templateEntity, idAndLanguageCodeID);

		return idAndLanguageCodeID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.TemplateService#updateTemplates(io.mosip.
	 * kernel.masterdata.dto.TemplateDto)
	 */
	@Override
	public IdAndLanguageCodeID updateTemplates(TemplateDto template) {
		IdAndLanguageCodeID idAndLanguageCodeID = new IdAndLanguageCodeID();
		try {
			Template entity = templateRepository.findTemplateByIDAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(
					template.getId(), template.getLangCode());
			if (!EmptyCheckUtils.isNullEmpty(entity)) {
				MetaDataUtils.setUpdateMetaData(template, entity, false);
				templateRepository.update(entity);
				idAndLanguageCodeID.setId(entity.getId());
				idAndLanguageCodeID.setLangCode(entity.getLangCode());
			} else {
				throw new RequestException(TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorCode(),
						TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(TemplateErrorCode.TEMPLATE_UPDATE_EXCEPTION.getErrorCode(),
					TemplateErrorCode.TEMPLATE_UPDATE_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}
		return idAndLanguageCodeID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.TemplateService#deleteTemplates(java.lang.
	 * String)
	 */
	@Transactional
	@Override
	public IdResponseDto deleteTemplates(String id) {
		try {
			int updatedRows = templateRepository.deleteTemplate(id, MetaDataUtils.getCurrentDateTime(),
					MetaDataUtils.getContextUser());
			if (updatedRows < 1) {
				throw new RequestException(TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorCode(),
						TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorMessage());
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(TemplateErrorCode.TEMPLATE_DELETE_EXCEPTION.getErrorCode(),
					TemplateErrorCode.TEMPLATE_DELETE_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}
		IdResponseDto idResponseDto = new IdResponseDto();
		idResponseDto.setId(id);
		return idResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.TemplateService#
	 * getAllTemplateByTemplateTypeCode(java.lang.String)
	 */
	@Override
	public TemplateResponseDto getAllTemplateByTemplateTypeCode(String templateTypeCode) {
		List<Template> templates;
		List<TemplateDto> templateDtos;
		try {
			templates = templateRepository
					.findAllByTemplateTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(templateTypeCode);
		} catch (DataAccessException | DataAccessLayerException exception) {
			throw new MasterDataServiceException(TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorCode(),
					TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(exception));
		}
		if (templates != null && !templates.isEmpty()) {
			templateDtos = MapperUtils.mapAll(templates, TemplateDto.class);
		} else {
			throw new DataNotFoundException(TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorCode(),
					TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorMessage());
		}
		TemplateResponseDto responseDto = new TemplateResponseDto();
		responseDto.setTemplates(templateDtos);
		return responseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.TemplateService#getTemplates(int,
	 * int, java.lang.String, java.lang.String)
	 */
	@Override
	public PageDto<TemplateExtnDto> getTemplates(int pageNumber, int pageSize, String sortBy, String orderBy) {
		List<TemplateExtnDto> templates = null;
		PageDto<TemplateExtnDto> pageDto = null;
		try {
			Page<Template> pageData = templateRepository
					.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Direction.fromString(orderBy), sortBy)));
			if (pageData != null && pageData.getContent() != null && !pageData.getContent().isEmpty()) {
				templates = MapperUtils.mapAll(pageData.getContent(), TemplateExtnDto.class);
				pageDto = new PageDto<>(pageData.getNumber(), pageData.getTotalPages(), pageData.getTotalElements(),
						templates);
			} else {
				throw new DataNotFoundException(TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorCode(),
						TemplateErrorCode.TEMPLATE_NOT_FOUND.getErrorMessage());
			}
		} catch (DataAccessException | DataAccessLayerException exception) {
			throw new MasterDataServiceException(TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorCode(),
					TemplateErrorCode.TEMPLATE_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(exception));
		}
		return pageDto;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.masterdata.service.TemplateService#searchTemplates(io.mosip.kernel.masterdata.dto.request.SearchDto)
	 */
	@Override
	public PageResponseDto<TemplateExtnDto> searchTemplates(SearchDto searchDto) {
		PageResponseDto<TemplateExtnDto> pageDto = new PageResponseDto<>();
		List<TemplateExtnDto> templates = null;
		if (filterTypeValidator.validate(TemplateExtnDto.class, searchDto.getFilters())) {
			Page<Template> page = masterDataSearchHelper.searchMasterdata(Template.class, searchDto, null);
			if (page.getContent() != null && !page.getContent().isEmpty()) {
				pageDto = PageUtils.pageResponse(page);
				templates = MapperUtils.mapAll(page.getContent(), TemplateExtnDto.class);
				pageDto.setData(templates);
			}
		}
		return pageDto;
		
	}
}