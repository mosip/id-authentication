package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
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
import io.mosip.kernel.masterdata.constant.GenderTypeErrorCode;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.dto.GenderTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.GenderTypeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.StatusResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.GenderExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.ColumnValue;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.Gender;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.GenderTypeRepository;
import io.mosip.kernel.masterdata.service.GenderTypeService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MasterDataFilterHelper;
import io.mosip.kernel.masterdata.utils.MasterdataSearchHelper;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.PageUtils;
import io.mosip.kernel.masterdata.validator.FilterColumnValidator;
import io.mosip.kernel.masterdata.validator.FilterTypeValidator;

/**
 * This class contains service methods to fetch gender type data from DB
 * 
 * @author Sidhant Agarwal
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
@Service
public class GenderTypeServiceImpl implements GenderTypeService {

	@Autowired
	FilterTypeValidator filterTypeValidator;

	@Autowired
	FilterColumnValidator filterColumnValidator;

	@Autowired
	MasterdataSearchHelper masterDataSearchHelper;

	@Autowired
	MasterDataFilterHelper masterDataFilterHelper;

	@Autowired
	private PageUtils pageUtils;

	@Autowired
	GenderTypeRepository genderTypeRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.GenderTypeService#getAllGenderTypes()
	 */
	@Override
	public GenderTypeResponseDto getAllGenderTypes() {
		GenderTypeResponseDto genderResponseDto = null;
		List<GenderTypeDto> genderDto = null;
		List<Gender> genderType = null;

		try {
			genderType = genderTypeRepository.findAllByIsActiveAndIsDeleted();
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(GenderTypeErrorCode.GENDER_TYPE_FETCH_EXCEPTION.getErrorCode(),
					ExceptionUtils.parseException(e));
		}
		if (!(genderType.isEmpty())) {
			genderDto = MapperUtils.mapAll(genderType, GenderTypeDto.class);
		} else {
			throw new DataNotFoundException(GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorCode(),
					GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorMessage());
		}
		genderResponseDto = new GenderTypeResponseDto();
		genderResponseDto.setGenderType(genderDto);

		return genderResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.GenderTypeService#getGenderTypeByLangCode(
	 * java.lang.String)
	 */
	@Override
	public GenderTypeResponseDto getGenderTypeByLangCode(String langCode) {
		GenderTypeResponseDto genderResponseDto = null;
		List<GenderTypeDto> genderListDto = null;
		List<Gender> gender = null;

		try {
			gender = genderTypeRepository.findGenderByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(langCode);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(GenderTypeErrorCode.GENDER_TYPE_FETCH_EXCEPTION.getErrorCode(),
					ExceptionUtils.parseException(e));
		}
		if (gender != null && !gender.isEmpty()) {
			genderListDto = MapperUtils.mapAll(gender, GenderTypeDto.class);
		} else {
			throw new DataNotFoundException(GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorCode(),
					GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorMessage());
		}

		genderResponseDto = new GenderTypeResponseDto();
		genderResponseDto.setGenderType(genderListDto);

		return genderResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.GenderTypeService#createGenderType(io.
	 * mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public CodeAndLanguageCodeID saveGenderType(GenderTypeDto genderRequestDto) {
		Gender entity = MetaDataUtils.setCreateMetaData(genderRequestDto, Gender.class);
		Gender gender;
		try {
			gender = genderTypeRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(GenderTypeErrorCode.GENDER_TYPE_INSERT_EXCEPTION.getErrorCode(),
					ExceptionUtils.parseException(e));
		}
		CodeAndLanguageCodeID codeLangCodeId = new CodeAndLanguageCodeID();
		MapperUtils.map(gender, codeLangCodeId);
		return codeLangCodeId;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.GenderTypeService#updateGenderType(io.
	 * mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Transactional
	@Override
	public CodeAndLanguageCodeID updateGenderType(GenderTypeDto genderTypeDto) {
		CodeAndLanguageCodeID genderTypeId = new CodeAndLanguageCodeID();
		MapperUtils.mapFieldValues(genderTypeDto, genderTypeId);
		try {
			int updatedRows = genderTypeRepository.updateGenderType(genderTypeDto.getCode(),
					genderTypeDto.getLangCode(), genderTypeDto.getGenderName(), genderTypeDto.getIsActive(),
					MetaDataUtils.getCurrentDateTime(), MetaDataUtils.getContextUser());
			if (updatedRows < 1) {
				throw new RequestException(GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorCode(),
						GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(GenderTypeErrorCode.GENDER_TYPE_UPDATE_EXCEPTION.getErrorCode(),
					GenderTypeErrorCode.GENDER_TYPE_UPDATE_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		return genderTypeId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.GenderTypeService#deleteGenderType(java.
	 * lang.String, java.lang.String)
	 */
	@Transactional
	@Override
	public CodeResponseDto deleteGenderType(String code) {
		try {
			int updatedRows = genderTypeRepository.deleteGenderType(code, MetaDataUtils.getCurrentDateTime(),
					MetaDataUtils.getContextUser());
			if (updatedRows < 1) {
				throw new RequestException(GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorCode(),
						GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(GenderTypeErrorCode.GENDER_TYPE_DELETE_EXCEPTION.getErrorCode(),
					GenderTypeErrorCode.GENDER_TYPE_DELETE_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		CodeResponseDto codeResponseDto = new CodeResponseDto();
		codeResponseDto.setCode(code);
		return codeResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.GenderTypeService#validateGender(java.lang
	 * .String)
	 */
	@Override
	public StatusResponseDto validateGender(String genderName) {
		StatusResponseDto statusResponseDto = null;
		boolean isPresent = false;
		try {
			statusResponseDto = new StatusResponseDto();
			isPresent = genderTypeRepository.isGenderNamePresent(genderName);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(GenderTypeErrorCode.GENDER_TYPE_FETCH_EXCEPTION.getErrorCode(),
					GenderTypeErrorCode.GENDER_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}
		if (isPresent) {
			statusResponseDto.setStatus(MasterDataConstant.VALID);
		} else {
			statusResponseDto.setStatus(MasterDataConstant.INVALID);
		}

		return statusResponseDto;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.GenderTypeService#getGenderTypes(int,
	 * int, java.lang.String, java.lang.String)
	 */
	@Override
	public PageDto<GenderExtnDto> getGenderTypes(int pageNumber, int pageSize, String sortBy, String orderBy) {
		List<GenderExtnDto> genderTypes = null;
		PageDto<GenderExtnDto> genderTypesPages = null;
		try {
			Page<Gender> pageData = genderTypeRepository
					.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Direction.fromString(orderBy), sortBy)));
			if (pageData != null && pageData.getContent() != null && !pageData.getContent().isEmpty()) {
				genderTypes = MapperUtils.mapAll(pageData.getContent(), GenderExtnDto.class);
				genderTypesPages = new PageDto<>(pageData.getNumber(), pageData.getTotalPages(),
						pageData.getTotalElements(), genderTypes);
			} else {
				throw new DataNotFoundException(GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorCode(),
						GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(GenderTypeErrorCode.GENDER_TYPE_FETCH_EXCEPTION.getErrorCode(),
					GenderTypeErrorCode.GENDER_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}
		return genderTypesPages;
	}

	@Override
	public PageResponseDto<GenderExtnDto> searchGenderTypes(SearchDto request) {
		PageResponseDto<GenderExtnDto> pageDto = new PageResponseDto<>();
		List<GenderExtnDto> genderTypeExtns = null;
		if (filterTypeValidator.validate(GenderExtnDto.class, request.getFilters())) {
			pageUtils.validateSortField(Gender.class, request.getSort());
			Page<Gender> page = masterDataSearchHelper.searchMasterdata(Gender.class, request, null);
			if (page.getContent() != null && !page.getContent().isEmpty()) {
				pageDto = PageUtils.pageResponse(page);
				genderTypeExtns = MapperUtils.mapAll(page.getContent(), GenderExtnDto.class);
				pageDto.setData(genderTypeExtns);
			}
		}
		return pageDto;
	}

	@Override
	public FilterResponseDto genderFilterValues(FilterValueDto request) {
		FilterResponseDto filterResponseDto = new FilterResponseDto();
		List<ColumnValue> columnValueList = new ArrayList<>();
		ColumnValue columnValue;
		if (filterColumnValidator.validate(FilterDto.class, request.getFilters(), Gender.class)) {
			for (FilterDto filterDto : request.getFilters()) {
				List<String> filterValues = masterDataFilterHelper.filterValues(Gender.class, filterDto, request);
				for (String filterValue : filterValues) {
					if (filterValue != null) {
						columnValue = new ColumnValue();
						columnValue.setFieldID(filterDto.getColumnName());
						columnValue.setFieldValue(filterValue);
						columnValueList.add(columnValue);
					}
				}
			}
			filterResponseDto.setFilters(columnValueList);
		}
		return filterResponseDto;
	}

}
