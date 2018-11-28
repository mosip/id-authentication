package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.DeviceSpecificationErrorCode;
import io.mosip.kernel.masterdata.dto.DeviceSpecPostResponseDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationRequestDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeCodeAndLanguageCodeAndId;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DeviceSpecificationRepository;
import io.mosip.kernel.masterdata.service.DeviceSpecificationService;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;

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
	MapperUtils objMapper;

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
					.findByLangCodeAndIsDeletedFalse(languageCode);
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
					.findByLangCodeAndDeviceTypeCodeAndIsDeletedFalse(languageCode, deviceTypeCode);
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
	public DeviceTypeCodeAndLanguageCodeAndId saveDeviceSpecification(DeviceSpecificationRequestDto deviceSpecifications) {
		DeviceSpecification renDeviceSpecification = new DeviceSpecification();

		DeviceSpecification entity = metaUtils
				.setCreateMetaData(deviceSpecifications.getRequest().getDeviceSpecificationDto(), DeviceSpecification.class);
		try {
			 renDeviceSpecification = deviceSpecificationRepository.create(entity);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_INSERT_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_INSERT_EXCEPTION.getErrorMessage());
		}
			DeviceTypeCodeAndLanguageCodeAndId deviceTypeCodeAndLanguageCodeId = new DeviceTypeCodeAndLanguageCodeAndId();
				dataMapper.map(renDeviceSpecification, deviceTypeCodeAndLanguageCodeId, true, null, null, true);
			
		return deviceTypeCodeAndLanguageCodeId;	
	}

}
