package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.constant.DeviceErrorCode;
import io.mosip.kernel.masterdata.dto.DeviceTypeDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeResponseDto;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.exception.DeviceFetchException;
import io.mosip.kernel.masterdata.exception.DeviceMappingException;
import io.mosip.kernel.masterdata.exception.DeviceNotFoundException;
import io.mosip.kernel.masterdata.repository.DeviceTypeRepository;
import io.mosip.kernel.masterdata.service.DeviceTypeService;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

@Service
public class DeviceTypeServiceImpl implements DeviceTypeService{
	
	@Autowired
	DeviceTypeRepository deviceTypeRepository;
	
	@Autowired
	ObjectMapperUtil objectMapperUtil;

	@Override
	public DeviceTypeResponseDto addDeviceType(List<DeviceType> deviceTypeList){
		DeviceTypeResponseDto deviceTypeResponseDto = new DeviceTypeResponseDto();
		List<DeviceTypeDto> deviceTypeDtoList = new ArrayList<>();
		List<DeviceType> renDeviceTypeList = new ArrayList<>();
		
		try {
			renDeviceTypeList = deviceTypeRepository.saveAll(deviceTypeList);
		} catch (DataAccessException dataAccessLayerException) {
			throw new DeviceFetchException(DeviceErrorCode.DEVICE_FETCH_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_FETCH_EXCEPTION.getErrorMessage());
		}
		if (renDeviceTypeList != null && !renDeviceTypeList.isEmpty()) {
			try {
				deviceTypeDtoList = objectMapperUtil.mapAll(renDeviceTypeList, DeviceTypeDto.class);
			} catch (IllegalArgumentException | ConfigurationException | MappingException exception) {
				throw new DeviceMappingException(DeviceErrorCode.DEVICE_MAPPING_EXCEPTION.getErrorCode(),
						DeviceErrorCode.DEVICE_MAPPING_EXCEPTION.getErrorMessage());
			}
		} else {
			throw new DeviceNotFoundException(DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		deviceTypeResponseDto.setSuccessfully_created_devices(deviceTypeDtoList);
		return deviceTypeResponseDto;
	}
	
}
