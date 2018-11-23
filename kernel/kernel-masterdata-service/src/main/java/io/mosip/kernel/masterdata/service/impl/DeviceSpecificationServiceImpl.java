package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.datamapper.exception.DataMapperException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.DeviceSpecificationErrorCode;
import io.mosip.kernel.masterdata.constant.DeviceTypeErrorCode;
import io.mosip.kernel.masterdata.dto.DeviceSpecPostResponseDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationRequestDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationResponseDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeCodeAndLanguageCode;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DeviceSpecificationRepository;
import io.mosip.kernel.masterdata.service.DeviceSpecificationService;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

/**
 * Service class has methods to save and fetch DeviceSpecification Details
 * 
 * @author Megha Tanga
 * @author Uday
 * @since 1.0.0
 *
 */
@Service
public class DeviceSpecificationServiceImpl implements DeviceSpecificationService {
	@Autowired
	DeviceSpecificationRepository deviceSpecificationRepository;
	@Autowired
	ObjectMapperUtil objMapper;

	@Autowired
	private MetaDataUtils metaUtils;
	
	@Autowired
	private DataMapper dataMapper;

	@Override
	public List<DeviceSpecificationDto> findDeviceSpecificationByLangugeCode(String languageCode) {
		List<DeviceSpecification> deviceSpecificationList = null;
		List<DeviceSpecificationDto> deviceSpecificationDtoList = null;
		try {
			deviceSpecificationList = deviceSpecificationRepository
					.findByLangCodeAndIsActiveTrueAndIsDeletedFalse(languageCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_DATA_FETCH_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_DATA_FETCH_EXCEPTION.getErrorMessage());
		}
		if (deviceSpecificationList != null && !deviceSpecificationList.isEmpty()) {
			deviceSpecificationDtoList = objMapper.mapDeviceSpecification(deviceSpecificationList);
			return deviceSpecificationDtoList;
		} else {
			throw new DataNotFoundException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
	}

	@Override
	public List<DeviceSpecificationDto> findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(String languageCode,
			String deviceTypeCode) {
		List<DeviceSpecification> deviceSpecificationList = null;
		List<DeviceSpecificationDto> deviceSpecificationDtoList = null;
		try {
			deviceSpecificationList = deviceSpecificationRepository
					.findByLangCodeAndDeviceTypeCodeAndIsActiveTrueAndIsDeletedFalse(languageCode, deviceTypeCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_DATA_FETCH_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_DATA_FETCH_EXCEPTION.getErrorMessage());
		}
		if (deviceSpecificationList != null && !deviceSpecificationList.isEmpty()) {
			deviceSpecificationDtoList = objMapper.mapDeviceSpecification(deviceSpecificationList);
			return deviceSpecificationDtoList;
		} else {
			throw new DataNotFoundException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
	}

	@Override
	public DeviceSpecPostResponseDto addDeviceSpecifications(DeviceSpecificationRequestDto deviceSpecifications) {
		DeviceSpecPostResponseDto respDto = new DeviceSpecPostResponseDto();
		List<DeviceSpecification> renDeviceSpecificationList = null;
		List<DeviceSpecificationDto> deviceSpecificationDtos = null;

		List<DeviceSpecification> entities = metaUtils
				.setCreateMetaData(deviceSpecifications.getRequest().getDeviceSpecificationDtos(), DeviceSpecification.class);
		try {
			renDeviceSpecificationList = deviceSpecificationRepository.saveAll(entities);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_INSERT_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_INSERT_EXCEPTION.getErrorMessage());
		}
		List<DeviceTypeCodeAndLanguageCode> deviceTypeCodeLangCodes = new ArrayList<>();
		renDeviceSpecificationList.forEach(renDeviceSpecification -> {
			DeviceTypeCodeAndLanguageCode deviceTypeCodeAndLanguageCode = new DeviceTypeCodeAndLanguageCode();
			try {
				dataMapper.map(renDeviceSpecification, deviceTypeCodeAndLanguageCode, true, null, null, true);
			} catch (DataMapperException e) {
				throw new MasterDataServiceException(
						DeviceTypeErrorCode.DEVICE_TYPE_MAPPING_EXCEPTION.getErrorCode(), e.getMessage());
			}
			deviceTypeCodeLangCodes.add(deviceTypeCodeAndLanguageCode);
		});
		respDto.setResults(deviceTypeCodeLangCodes);
		return respDto;	
	}

}
