package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.GenderTypeErrorCode;
import io.mosip.kernel.masterdata.dto.GenderTypeDto;
import io.mosip.kernel.masterdata.dto.GenderTypeResponseDto;
import io.mosip.kernel.masterdata.entity.GenderType;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.GenderTypeRepository;
import io.mosip.kernel.masterdata.service.GenderTypeService;

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
	private ModelMapper mapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.GenderTypeService#getAllGenderTypes()
	 */
	@Override
	public GenderTypeResponseDto getAllGenderTypes() {
		GenderTypeResponseDto genderResponseDto = null;
		List<GenderTypeDto> genderDto = null;
		List<GenderType> genderType = null;

		try {
			genderType = genderTypeRepository.findAll(GenderType.class);
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(GenderTypeErrorCode.GENDER_TYPE_FETCH_EXCEPTION.getErrorCode(),
					GenderTypeErrorCode.GENDER_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}
		if (!(genderType.isEmpty())) {
			genderDto = mapper.map(genderType, new TypeToken<List<GenderTypeDto>>() {
			}.getType());
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
	 * @see io.mosip.kernel.masterdata.service.GenderTypeService#
	 * getGenderTypeByLanguageCode(java.lang.String)
	 */
	@Override
	public GenderTypeResponseDto getGenderTypeByLanguageCode(String languageCode) {
		GenderTypeResponseDto genderResponseDto = null;
		List<GenderTypeDto> genderListDto = null;
		List<GenderType> gender = new ArrayList<>();

		try {
			gender = genderTypeRepository.findGenderByLanguageCode(languageCode);
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(GenderTypeErrorCode.GENDER_TYPE_FETCH_EXCEPTION.getErrorCode(),
					GenderTypeErrorCode.GENDER_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}
		if (gender.isEmpty()) {
			throw new DataNotFoundException(GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorCode(),
					GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorMessage());
		}
		genderListDto = mapper.map(gender, new TypeToken<List<GenderTypeDto>>() {
		}.getType());

		genderResponseDto = new GenderTypeResponseDto();
		genderResponseDto.setGenderType(genderListDto);

		return genderResponseDto;
	}

}
