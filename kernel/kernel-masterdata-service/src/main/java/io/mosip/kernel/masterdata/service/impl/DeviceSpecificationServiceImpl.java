package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.constant.DeviceSpecificationErrorCode;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DeviceSpecificationRepository;
import io.mosip.kernel.masterdata.service.DeviceSpecificationService;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

@Service
public class DeviceSpecificationServiceImpl implements DeviceSpecificationService {
	@Autowired
	DeviceSpecificationRepository deviceSpecificationRepository;
	@Autowired
	ObjectMapperUtil objMapper;

	@Override
	public List<DeviceSpecificationDto> findDeviceSpecificationByLangugeCode(String languageCode) {
		List<DeviceSpecification> deviceSpecificationsEntitys = null;
		List<DeviceSpecificationDto> deviceSpecificationDtos = null;
		try {
			deviceSpecificationsEntitys = deviceSpecificationRepository.findByLangCode(languageCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_DATA_FETCH_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_DATA_FETCH_EXCEPTION.getErrorMessage());
		}
		if (deviceSpecificationsEntitys != null && !deviceSpecificationsEntitys.isEmpty()) {
			deviceSpecificationDtos = objMapper.mapAll(deviceSpecificationsEntitys, DeviceSpecificationDto.class);
			return deviceSpecificationDtos;
		} else {
			throw new DataNotFoundException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
	}

	@Override
	public List<DeviceSpecificationDto> findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(String languageCode,
			String deviceTypeCode) {
		List<DeviceSpecification> deviceSpecificationsEntitys = null;
		List<DeviceSpecificationDto> deviceSpecificationDtos = null;
		try {
			deviceSpecificationsEntitys = deviceSpecificationRepository.findByLangCodeAndDeviceTypeCode(languageCode,
					deviceTypeCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_DATA_FETCH_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_DATA_FETCH_EXCEPTION.getErrorMessage());
		}
		if (deviceSpecificationsEntitys != null && !deviceSpecificationsEntitys.isEmpty()) {
			deviceSpecificationDtos = objMapper.mapAll(deviceSpecificationsEntitys, DeviceSpecificationDto.class);
			return deviceSpecificationDtos;
		} else {
			throw new DataNotFoundException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}

	}

}
