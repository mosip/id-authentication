package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.GenderTypeErrorCode;
import io.mosip.kernel.masterdata.dto.GenderTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.GenderTypeResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.entity.Gender;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.GenderTypeRepository;
import io.mosip.kernel.masterdata.service.GenderTypeService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

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
			genderType = genderTypeRepository.findAll(Gender.class);
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
	public CodeAndLanguageCodeID saveGenderType(RequestDto<GenderTypeDto> genderRequestDto) {
		Gender entity = MetaDataUtils.setCreateMetaData(genderRequestDto.getRequest(), Gender.class);
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
	public CodeAndLanguageCodeID updateGenderType(RequestDto<GenderTypeDto> gender) {
		GenderTypeDto genderTypeDto = gender.getRequest();
        CodeAndLanguageCodeID genderTypeId = new CodeAndLanguageCodeID();
        MapperUtils.mapFieldValues(genderTypeDto, genderTypeId);
		try {
			int updatedRows= genderTypeRepository.updateGenderType(genderTypeDto.getCode(), genderTypeDto.getLangCode(),
					genderTypeDto.getGenderName(), genderTypeDto.getIsActive(), MetaDataUtils.getCurrentDateTime(),
					MetaDataUtils.getContextUser());
			if (updatedRows < 1) {
				throw new RequestException(GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorCode(),
						GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(GenderTypeErrorCode.GENDER_TYPE_UPDATE_EXCEPTION.getErrorCode(),
					GenderTypeErrorCode.GENDER_TYPE_UPDATE_EXCEPTION.getErrorMessage()+ExceptionUtils.parseException(e));
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
			int updatedRows = genderTypeRepository.deleteGenderType(code, MetaDataUtils.getCurrentDateTime(),MetaDataUtils.getContextUser());
			if (updatedRows < 1) {
				throw new RequestException(GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorCode(),
						GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(GenderTypeErrorCode.GENDER_TYPE_DELETE_EXCEPTION.getErrorCode(),
					GenderTypeErrorCode.GENDER_TYPE_DELETE_EXCEPTION.getErrorMessage()+ExceptionUtils.parseException(e));
		}
		CodeResponseDto codeResponseDto = new CodeResponseDto();
		codeResponseDto.setCode(code);
		return codeResponseDto;
	}

}
