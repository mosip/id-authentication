package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.GenderTypeErrorCode;
import io.mosip.kernel.masterdata.dto.GenderDto;
import io.mosip.kernel.masterdata.dto.GenderRequestDto;
import io.mosip.kernel.masterdata.dto.GenderTypeDto;
import io.mosip.kernel.masterdata.dto.GenderTypeResponseDto;
import io.mosip.kernel.masterdata.entity.GenderType;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.GenderTypeRepository;
import io.mosip.kernel.masterdata.service.GenderTypeService;
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
		List<GenderType> genderType = null;

		try {
			genderType = genderTypeRepository.findAll(GenderType.class);
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(GenderTypeErrorCode.GENDER_TYPE_FETCH_EXCEPTION.getErrorCode(),
					GenderTypeErrorCode.GENDER_TYPE_FETCH_EXCEPTION.getErrorMessage());
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
	 * @see io.mosip.kernel.masterdata.service.GenderTypeService#
	 * getGenderTypeByLanguageCode(java.lang.String)
	 */
	@Override
	public GenderTypeResponseDto getGenderTypeByLanguageCode(String languageCode) {
		GenderTypeResponseDto genderResponseDto = null;
		List<GenderTypeDto> genderListDto = null;
		List<GenderType> gender = new ArrayList<>();

		try {
			gender = genderTypeRepository.findGenderByLanguageCodeAndIsDeletedFalse(languageCode);
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(GenderTypeErrorCode.GENDER_TYPE_FETCH_EXCEPTION.getErrorCode(),
					GenderTypeErrorCode.GENDER_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}
		if (gender.isEmpty()) {
			throw new DataNotFoundException(GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorCode(),
					GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorMessage());
		}
		genderListDto = objectMapperUtil.mapAll(gender, GenderTypeDto.class);

		genderResponseDto = new GenderTypeResponseDto();
		genderResponseDto.setGenderType(genderListDto);

		return genderResponseDto;
	}
	
	@Override
	public GenderTypeResponseDto saveGenderType(GenderRequestDto genderRequestDto) {
		List<GenderDto> genderRequestDtos = genderRequestDto.getGenderList();
		List<GenderType> genderList = null;
		List<GenderType> genderResultantEntities = null;
		GenderTypeResponseDto genderCodeResponseDto = new GenderTypeResponseDto();
		if (!genderRequestDtos.isEmpty()) {
			genderList = metaDataUtils.setCreateMetaData(genderRequestDtos, GenderType.class);
			try {
				genderResultantEntities = genderList.stream()
						.map(genderObj -> genderTypeRepository.save(genderObj)).collect(Collectors.toList());
			} catch (DataAccessException ex) {
				ex.printStackTrace();
				throw new MasterDataServiceException(GenderTypeErrorCode.GENDER_TYPE_FETCH_EXCEPTION.getErrorCode(),
						GenderTypeErrorCode.GENDER_TYPE_FETCH_EXCEPTION.getErrorMessage());
			}
		} else {
			
			throw new DataNotFoundException(GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorCode(),
					GenderTypeErrorCode.GENDER_TYPE_NOT_FOUND.getErrorMessage());
		}
		List<GenderTypeDto> genderCodeDtos = objectMapperUtil.mapAll(genderResultantEntities,
				GenderTypeDto.class);
		
        genderCodeResponseDto.setGenderType(genderCodeDtos);
		return genderCodeResponseDto;
	}

	

}
