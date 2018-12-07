package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.BiometricAttributeErrorCode;
import io.mosip.kernel.masterdata.dto.BiometricAttributeDto;
import io.mosip.kernel.masterdata.entity.BiometricAttribute;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.BiometricAttributeRepository;
import io.mosip.kernel.masterdata.service.BiometricAttributeService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * This class have methods to fetch a biomettic attribute
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */

@Service
public class BiometricAttributeServiecImpl implements BiometricAttributeService {
	@Autowired
	private BiometricAttributeRepository biometricAttributeRepository;
	@Autowired
	private MapperUtils mapperUtil;

	@Autowired
	private MetaDataUtils metaUtils;

	@Override
	public List<BiometricAttributeDto> getBiometricAttribute(String biometricTypeCode, String langCode) {
		List<BiometricAttributeDto> attributesDto = null;
		List<BiometricAttribute> attributes = null;
		try {
			attributes = biometricAttributeRepository
					.findByBiometricTypeCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(biometricTypeCode, langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					BiometricAttributeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorCode(),
					BiometricAttributeErrorCode.BIOMETRIC_TYPE_FETCH_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}
		if (attributes != null && !attributes.isEmpty()) {
			attributesDto = mapperUtil.mapAll(attributes, BiometricAttributeDto.class);
		} else {
			throw new DataNotFoundException(
					BiometricAttributeErrorCode.BIOMETRICATTRIBUTE_NOT_FOUND_EXCEPTION.getErrorCode(),
					BiometricAttributeErrorCode.BIOMETRICATTRIBUTE_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		return attributesDto;
	}

	@Override
	public CodeAndLanguageCodeID createBiometricAttribute(BiometricAttributeDto biometricAttribute) {
		BiometricAttribute entity = metaUtils.setCreateMetaData(biometricAttribute, BiometricAttribute.class);
		try {
			entity = biometricAttributeRepository.create(entity);
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(
					BiometricAttributeErrorCode.BIOMETRICATTRIBUTE_INSERT_EXCEPTION.getErrorCode(),
					BiometricAttributeErrorCode.BIOMETRICATTRIBUTE_INSERT_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}
		CodeAndLanguageCodeID codeAndLanguageCodeId = new CodeAndLanguageCodeID();

		mapperUtil.mapNew(entity, codeAndLanguageCodeId);

		return codeAndLanguageCodeId;
	}

}
