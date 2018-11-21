package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.constant.DeviceErrorCode;
import io.mosip.kernel.masterdata.constant.DeviceTypeErrorCode;
import io.mosip.kernel.masterdata.dto.DeviceTypeDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeRequestDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeResponseDto;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DeviceTypeRepository;
import io.mosip.kernel.masterdata.service.DeviceTypeService;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

@Service
public class DeviceTypeServiceImpl implements DeviceTypeService {

	@Autowired
	private MetaDataUtils metaUtils;

	@Autowired
	DeviceTypeRepository deviceTypeRepository;

	@Autowired
	ObjectMapperUtil objectMapperUtil;



	@Override
	public DeviceTypeResponseDto addDeviceType(DeviceTypeRequestDto deviceTypes) {

		List<DeviceType> entities = metaUtils.setCreateMetaData(deviceTypes.getDeviceTypeDtoRequest(),
				DeviceType.class);
		
		DeviceTypeResponseDto deviceTypeResponseDto = new DeviceTypeResponseDto();
		List<DeviceTypeDto> deviceTypeDtoList = null;
		List<DeviceType> renDeviceTypeList = null;

		try {
			renDeviceTypeList = deviceTypeRepository.saveAll(entities);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DeviceTypeErrorCode.DEVICE_TYPE_INSERT_EXCEPTION.getErrorCode(),
					DeviceTypeErrorCode.DEVICE_TYPE_INSERT_EXCEPTION.getErrorMessage());
		}
		if (!renDeviceTypeList.isEmpty()) {
			deviceTypeDtoList = objectMapperUtil.mapDeviceTypeDto(renDeviceTypeList);
		} else {
			throw new DataNotFoundException(DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		deviceTypeResponseDto.setSuccessfullyCreatedDevices(deviceTypeDtoList);
		return deviceTypeResponseDto;
	}

}
