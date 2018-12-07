package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.GenderTypeErrorCode;
import io.mosip.kernel.masterdata.dto.GenderTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.GenderTypeResponseDto;
import io.mosip.kernel.masterdata.entity.Gender;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.GenderTypeRepository;
import io.mosip.kernel.masterdata.service.GenderTypeService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * This class contains service methods to fetch gender type data from DB
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Service
public class GenderTypeServiceImpl implements GenderTypeService {

	@Autowired
	GenderTypeRepository genderTypeRepository;

	@Autowired
	private MapperUtils objectMapperUtil;

	@Autowired
	private DataMapper dataMapper;

	@Autowired
	private MetaDataUtils metaDataUtils;

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
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(GenderTypeErrorCode.GENDER_TYPE_FETCH_EXCEPTION.getErrorCode(),
					ExceptionUtils.parseException(e));
		}
		if (!(genderType.isEmpty())) {
			genderDto = objectMapperUtil.mapAll(genderType, GenderTypeDto.class);
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
		List<Gender> gender = new ArrayList<>();

		try {
			gender = genderTypeRepository.findGenderByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(langCode);
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(GenderTypeErrorCode.GENDER_TYPE_FETCH_EXCEPTION.getErrorCode(),
					ExceptionUtils.parseException(e));
		}
		if (gender != null && !gender.isEmpty()) {
			genderListDto = objectMapperUtil.mapAll(gender, GenderTypeDto.class);
		}
		else {
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
	public CodeAndLanguageCodeID createGenderType(RequestDto<GenderTypeDto> genderRequestDto) {
		Gender entity = metaDataUtils.setCreateMetaData(genderRequestDto.getRequest(), Gender.class);
		Gender gender;
		try {
			gender = genderTypeRepository.create(entity);
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(GenderTypeErrorCode.GENDER_TYPE_INSERT_EXCEPTION.getErrorCode(),
					ExceptionUtils.parseException(e));
		}
		CodeAndLanguageCodeID codeLangCodeId = new CodeAndLanguageCodeID();
		dataMapper.map(gender, codeLangCodeId, true, null, null, true);
		return codeLangCodeId;

	}

}
