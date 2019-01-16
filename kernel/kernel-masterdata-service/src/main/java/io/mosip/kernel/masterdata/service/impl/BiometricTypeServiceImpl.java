package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.BiometricTypeErrorCode;
import io.mosip.kernel.masterdata.dto.BiometricTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.BiometricTypeResponseDto;
import io.mosip.kernel.masterdata.entity.BiometricType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.BiometricTypeRepository;
import io.mosip.kernel.masterdata.service.BiometricTypeService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * Service APIs to get Biometric types details
 * 
 * @author Neha
 * @since 1.0.0
 */
@Service
public class BiometricTypeServiceImpl implements BiometricTypeService {

	@Qualifier("biometricTypeTobiometricTypeDtoDefaultMapper")
	@Autowired
	DataMapper<BiometricType,BiometricTypeDto> biometricTypeTobiometricTypeDtoDefaultMapper;

	@Qualifier("biometricTypeToCodeandlanguagecodeDefaultMapper")
	@Autowired
	DataMapper<BiometricType, CodeAndLanguageCodeID> biometricTypeToCodeandlanguagecodeDefaultMapper; 
	
	@Autowired
	private BiometricTypeRepository biometricTypeRepository;
	private List<BiometricTypeDto> biometricTypeDtoList;
	private List<BiometricType> biometricTypesList;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.BiometricTypeService#getAllBiometricTypes(
	 * )
	 */
	@Override
	public BiometricTypeResponseDto getAllBiometricTypes() {
		biometricTypeDtoList = new ArrayList<>();
		try {
			biometricTypesList = biometricTypeRepository.findAllByIsDeletedFalseOrIsDeletedIsNull(BiometricType.class);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(BiometricTypeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorCode(),
					BiometricTypeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}
		if (!(biometricTypesList.isEmpty())) {
			biometricTypesList.forEach(biometricType -> 
				biometricTypeDtoList.add(biometricTypeTobiometricTypeDtoDefaultMapper.map(biometricType))
			);
		} else {
			throw new DataNotFoundException(BiometricTypeErrorCode.BIOMETRIC_TYPE_NOT_FOUND.getErrorCode(),
					BiometricTypeErrorCode.BIOMETRIC_TYPE_NOT_FOUND.getErrorMessage());
		}
		BiometricTypeResponseDto biometricTypeResponseDto = new BiometricTypeResponseDto();
		biometricTypeResponseDto.setBiometrictypes(biometricTypeDtoList);
		return biometricTypeResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.BiometricTypeService#
	 * getAllBiometricTypesByLanguageCode(java.lang.String)
	 */
	@Override
	public BiometricTypeResponseDto getAllBiometricTypesByLanguageCode(String langCode) {
		biometricTypeDtoList = new ArrayList<>();
		try {
			biometricTypesList = biometricTypeRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(langCode);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(BiometricTypeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorCode(),
					BiometricTypeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}
		if (!(biometricTypesList.isEmpty())) {
			biometricTypesList.forEach(biometricType -> 
				biometricTypeDtoList.add(biometricTypeTobiometricTypeDtoDefaultMapper.map(biometricType))
			);
		} else {
			throw new DataNotFoundException(BiometricTypeErrorCode.BIOMETRIC_TYPE_NOT_FOUND.getErrorCode(),
					BiometricTypeErrorCode.BIOMETRIC_TYPE_NOT_FOUND.getErrorMessage());
		}
		BiometricTypeResponseDto biometricTypeResponseDto = new BiometricTypeResponseDto();
		biometricTypeResponseDto.setBiometrictypes(biometricTypeDtoList);
		return biometricTypeResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.BiometricTypeService#
	 * getBiometricTypeByCodeAndLangCode(java.lang.String, java.lang.String)
	 */
	@Override
	public BiometricTypeResponseDto getBiometricTypeByCodeAndLangCode(String code, String langCode) {
		BiometricType biometricType;
		BiometricTypeDto biometricTypeDto; 
		try {
			biometricType = biometricTypeRepository.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(code,
					langCode);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(BiometricTypeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorCode(),
					BiometricTypeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}

		if (biometricType != null) {
			biometricTypeDto=biometricTypeTobiometricTypeDtoDefaultMapper.map(biometricType);
		} else {
			throw new DataNotFoundException(BiometricTypeErrorCode.BIOMETRIC_TYPE_NOT_FOUND.getErrorCode(),
					BiometricTypeErrorCode.BIOMETRIC_TYPE_NOT_FOUND.getErrorMessage());
		}
		List<BiometricTypeDto> biometricTypeDtos = new ArrayList<>();
		biometricTypeDtos.add(biometricTypeDto);
		BiometricTypeResponseDto biometricTypeResponseDto = new BiometricTypeResponseDto();
		biometricTypeResponseDto.setBiometrictypes(biometricTypeDtos);
		return biometricTypeResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.BiometricTypeService#addBiometricType(io.
	 * mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public CodeAndLanguageCodeID createBiometricType(RequestDto<BiometricTypeDto> biometricTypeRequestDto) {
		BiometricType entity = MetaDataUtils.setCreateMetaData(biometricTypeRequestDto.getRequest(),
				BiometricType.class);
		BiometricType biometricType;
		try {
			biometricType = biometricTypeRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(BiometricTypeErrorCode.BIOMETRIC_TYPE_INSERT_EXCEPTION.getErrorCode(),
					BiometricTypeErrorCode.BIOMETRIC_TYPE_INSERT_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}
		return biometricTypeToCodeandlanguagecodeDefaultMapper.map(biometricType);
	 
	}

}
