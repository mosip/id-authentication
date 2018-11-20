package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.constant.DeviceSpecificationErrorCode;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationCreateResponseDto;
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
			deviceSpecificationDtoList = objMapper.mapAll(deviceSpecificationList, DeviceSpecificationDto.class);
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
			deviceSpecificationDtoList = objMapper.mapAll(deviceSpecificationList, DeviceSpecificationDto.class);
			return deviceSpecificationDtoList;
		} else {
			throw new DataNotFoundException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
	}

	@Override
	public DeviceSpecificationCreateResponseDto addDeviceSpecification(
			List<DeviceSpecification> deviceSpecificationList) {
		DeviceSpecificationCreateResponseDto respDto = new DeviceSpecificationCreateResponseDto();
		List<DeviceSpecification> renDeviceSpecificationList = null;
		List<DeviceSpecificationDto> deviceSpecificationDtos = new ArrayList<>();

		try {
			renDeviceSpecificationList = deviceSpecificationRepository.saveAll(deviceSpecificationList);
			
			/*DeviceSpecification entity = new DeviceSpecification();
			entity.setId("DS-01");
			entity.setName("TVS");
			entity.setModel("TVS");
			entity.setDeviceTypeCode("Phone");
			entity.setMinDriverversion("TVS");
			entity.setLangCode("ENG");
			entity.setBrand("laptop");
			entity.setIsActive(true);
			entity.setCreatedBy("admin");
			entity.setCreatedtimes(LocalDateTime.now());*/

//			DeviceType devType = new DeviceType();
//			devType.setDeviceTypeId(new DeviceTypePk("BLR", "hin"));
//			devType.setName("banti");
//			devType.setIsActive(true);
//			devType.setCreatedBy("admin");
//			devType.setCreatedtimes(LocalDateTime.now());

//			entity.setDeviceType(devType);

			//DeviceSpecification re = deviceSpecificationRepository.create(entity);
		

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (renDeviceSpecificationList != null && !renDeviceSpecificationList.isEmpty()) {
			deviceSpecificationDtos = objMapper.mapDeviceSpecification(renDeviceSpecificationList);
		
		 } else {
		 throw new DataNotFoundException(
	 DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
		 DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		 }
		respDto.setSuccessfully_created_device_specification(deviceSpecificationDtos);
		return respDto;
	}

}
