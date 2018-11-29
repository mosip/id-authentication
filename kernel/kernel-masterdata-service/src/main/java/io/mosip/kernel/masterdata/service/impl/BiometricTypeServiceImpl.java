package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.constant.BiometricTypeErrorCode;
import io.mosip.kernel.masterdata.dto.BiometricTypeDto;
import io.mosip.kernel.masterdata.dto.BiometricTypeRequestDto;
import io.mosip.kernel.masterdata.dto.BiometricTypeResponseDto;
import io.mosip.kernel.masterdata.entity.BiometricType;
import io.mosip.kernel.masterdata.entity.CodeAndLanguageCodeId;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.BiometricTypeRepository;
import io.mosip.kernel.masterdata.service.BiometricTypeService;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * 
 * @author Neha
 * @since 1.0.0
 */
@Service
public class BiometricTypeServiceImpl implements BiometricTypeService {

	@Autowired
	private MapperUtils objectMapperUtil;

	@Autowired
	private MetaDataUtils metaDataUtils;

	@Autowired
	private BiometricTypeRepository biometricTypeRepository;

	private List<BiometricTypeDto> biometricTypeDtoList;
	private List<BiometricType> biometricTypesList;

	/**
	 * Method to fetch all Biometric Type details
	 * 
	 * @return BiometricTypeDTO list
	 * 
	 * @throws BiometricTypeFetchException
	 *             If fails to fetch required Biometric Type
	 * 
	 * @throws BiometricTypeMappingException
	 *             If not able to map Biometric Type entity with BiometricType Dto
	 * 
	 * @throws BiometricTypeNotFoundException
	 *             If given required Biometric Type not found
	 */
	@Override
	public BiometricTypeResponseDto getAllBiometricTypes() {
		try {
			biometricTypesList = biometricTypeRepository.findAllByIsDeletedFalse(BiometricType.class);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(BiometricTypeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorCode(),
					BiometricTypeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}

		if (!(biometricTypesList.isEmpty())) {
			biometricTypeDtoList = objectMapperUtil.mapAll(biometricTypesList, BiometricTypeDto.class);
		} else {
			throw new DataNotFoundException(BiometricTypeErrorCode.BIOMETRIC_TYPE_NOT_FOUND.getErrorCode(),
					BiometricTypeErrorCode.BIOMETRIC_TYPE_NOT_FOUND.getErrorMessage());
		}
		BiometricTypeResponseDto biometricTypeResponseDto = new BiometricTypeResponseDto();
		biometricTypeResponseDto.setBiometrictypes(biometricTypeDtoList);
		return biometricTypeResponseDto;
	}

	/**
	 * Method to fetch all Biometric Type details based on language code
	 * 
	 * @param langCode
	 *            The language code
	 * 
	 * @return BiometricTypeDTO list
	 * 
	 * @throws BiometricTypeFetchException
	 *             If fails to fetch required Biometric Type
	 * 
	 * @throws BiometricTypeMappingException
	 *             If not able to map Biometric Type entity with BiometricType Dto
	 * 
	 * @throws BiometricTypeNotFoundException
	 *             If given required Biometric Type not found
	 */
	@Override
	public BiometricTypeResponseDto getAllBiometricTypesByLanguageCode(String langCode) {
		try {
			biometricTypesList = biometricTypeRepository.findAllByLangCodeAndIsDeletedFalse(langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(BiometricTypeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorCode(),
					BiometricTypeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}

		if (!(biometricTypesList.isEmpty())) {
			biometricTypeDtoList = objectMapperUtil.mapAll(biometricTypesList, BiometricTypeDto.class);
		} else {
			throw new DataNotFoundException(BiometricTypeErrorCode.BIOMETRIC_TYPE_NOT_FOUND.getErrorCode(),
					BiometricTypeErrorCode.BIOMETRIC_TYPE_NOT_FOUND.getErrorMessage());
		}
		BiometricTypeResponseDto biometricTypeResponseDto = new BiometricTypeResponseDto();
		biometricTypeResponseDto.setBiometrictypes(biometricTypeDtoList);
		return biometricTypeResponseDto;
	}

	/**
	 * Method to fetch all Biometric Type details based on id and language code
	 * 
	 * @param code
	 *            The id of Biometric Type
	 * 
	 * @param langCode
	 *            The language code
	 * 
	 * @return BiometricTypeDTO list
	 * 
	 * @throws BiometricTypeFetchException
	 *             If fails to fetch required Biometric Type
	 * 
	 * @throws BiometricTypeMappingException
	 *             If not able to map Biometric Type entity with BiometricType Dto
	 * 
	 * @throws BiometricTypeNotFoundException
	 *             If given required Biometric Type not found
	 */
	@Override
	public BiometricTypeResponseDto getBiometricTypeByCodeAndLangCode(String code, String langCode) {
		BiometricType biometricType;
		BiometricTypeDto biometricTypeDto;
		try {
			biometricType = biometricTypeRepository.findByCodeAndLangCodeAndIsDeletedFalse(code, langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(BiometricTypeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorCode(),
					BiometricTypeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}

		if (biometricType != null) {
			biometricTypeDto = objectMapperUtil.map(biometricType, BiometricTypeDto.class);
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

	@Override
	public CodeAndLanguageCodeId addBiometricType(BiometricTypeRequestDto biometricTypeRequestDto) {

		BiometricType entity = metaDataUtils.setCreateMetaData(biometricTypeRequestDto.getRequest().getBiometricType(), BiometricType.class);
		BiometricType biometricType = null;
		
		try {
			biometricType = biometricTypeRepository.create(entity);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		
		CodeAndLanguageCodeId codeAndLanguageCodeId = new CodeAndLanguageCodeId();
		codeAndLanguageCodeId.setCode(biometricType.getCode());
		
		return codeAndLanguageCodeId;
	}

}
