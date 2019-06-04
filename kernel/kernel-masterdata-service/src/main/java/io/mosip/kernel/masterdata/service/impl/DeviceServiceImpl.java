package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.DeviceErrorCode;
import io.mosip.kernel.masterdata.dto.DeviceDto;
import io.mosip.kernel.masterdata.dto.DeviceLangCodeDtypeDto;
import io.mosip.kernel.masterdata.dto.DeviceRegistrationCenterDto;
import io.mosip.kernel.masterdata.dto.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceLangCodeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.Device;
import io.mosip.kernel.masterdata.entity.DeviceHistory;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDevice;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachineDevice;
import io.mosip.kernel.masterdata.entity.id.IdAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.DeviceRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterDeviceRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineDeviceRepository;
import io.mosip.kernel.masterdata.service.DeviceHistoryService;
import io.mosip.kernel.masterdata.service.DeviceService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * This class have methods to fetch and save Device Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Service
public class DeviceServiceImpl implements DeviceService {

	/**
	 * Field to hold Device Repository object
	 */
	@Autowired
	DeviceRepository deviceRepository;
	/**
	 * Field to hold Device Service object
	 */
	@Autowired
	DeviceHistoryService deviceHistoryService;

	@Autowired
	RegistrationCenterDeviceRepository registrationCenterDeviceRepository;

	@Autowired
	RegistrationCenterMachineDeviceRepository registrationCenterMachineDeviceRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DeviceService#getDeviceLangCode(java.lang.
	 * String)
	 */
	@Override
	public DeviceResponseDto getDeviceLangCode(String langCode) {
		List<Device> deviceList = null;
		List<DeviceDto> deviceDtoList = null;
		DeviceResponseDto deviceResponseDto = new DeviceResponseDto();
		try {
			deviceList = deviceRepository.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(DeviceErrorCode.DEVICE_FETCH_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_FETCH_EXCEPTION.getErrorMessage() + "  " + ExceptionUtils.parseException(e));
		}
		if (deviceList != null && !deviceList.isEmpty()) {
			deviceDtoList = MapperUtils.mapAll(deviceList, DeviceDto.class);

		} else {
			throw new DataNotFoundException(DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		deviceResponseDto.setDevices(deviceDtoList);
		return deviceResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DeviceService#
	 * getDeviceLangCodeAndDeviceType(java.lang.String, java.lang.String)
	 */
	@Override
	public DeviceLangCodeResponseDto getDeviceLangCodeAndDeviceType(String langCode, String dtypeCode) {

		List<Object[]> objectList = null;
		List<DeviceLangCodeDtypeDto> deviceLangCodeDtypeDtoList = null;
		DeviceLangCodeResponseDto deviceLangCodeResponseDto = new DeviceLangCodeResponseDto();
		try {
			objectList = deviceRepository.findByLangCodeAndDtypeCode(langCode, dtypeCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(DeviceErrorCode.DEVICE_FETCH_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_FETCH_EXCEPTION.getErrorMessage() + "  " + ExceptionUtils.parseException(e));
		}
		if (objectList != null && !objectList.isEmpty()) {
			deviceLangCodeDtypeDtoList = MapperUtils.mapDeviceDto(objectList);
		} else {
			throw new DataNotFoundException(DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		deviceLangCodeResponseDto.setDevices(deviceLangCodeDtypeDtoList);
		return deviceLangCodeResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DeviceService#saveDevice(io.mosip.kernel.
	 * masterdata.dto.RequestDto)
	 */
	@Override
	@Transactional
	public IdAndLanguageCodeID createDevice(DeviceDto deviceDto) {
		Device device = null;

		Device entity = MetaDataUtils.setCreateMetaData(deviceDto, Device.class);
		DeviceHistory entityHistory = MetaDataUtils.setCreateMetaData(deviceDto, DeviceHistory.class);
		entityHistory.setEffectDateTime(entity.getCreatedDateTime());
		entityHistory.setCreatedDateTime(entity.getCreatedDateTime());

		try {

			device = deviceRepository.create(entity);
			deviceHistoryService.createDeviceHistory(entityHistory);

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(DeviceErrorCode.DEVICE_INSERT_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_INSERT_EXCEPTION.getErrorMessage() + " " + ExceptionUtils.parseException(e));
		}

		IdAndLanguageCodeID idAndLanguageCodeID = new IdAndLanguageCodeID();
		MapperUtils.map(device, idAndLanguageCodeID);

		return idAndLanguageCodeID;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DeviceService#updateDevice(io.mosip.kernel
	 * .masterdata.dto.RequestDto)
	 */
	@Override
	@Transactional
	public IdAndLanguageCodeID updateDevice(DeviceDto deviceRequestDto) {
		Device entity = null;
		Device updatedDevice = null;
		try {
			Device oldDevice = deviceRepository.findByIdAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(
					deviceRequestDto.getId(), deviceRequestDto.getLangCode());

			if (oldDevice != null) {
				entity = MetaDataUtils.setUpdateMetaData(deviceRequestDto, oldDevice, false);
				updatedDevice = deviceRepository.update(entity);
				DeviceHistory deviceHistory = new DeviceHistory();
				MapperUtils.map(updatedDevice, deviceHistory);
				MapperUtils.setBaseFieldValue(updatedDevice, deviceHistory);

				deviceHistory.setEffectDateTime(updatedDevice.getUpdatedDateTime());
				deviceHistory.setUpdatedDateTime(updatedDevice.getUpdatedDateTime());

				deviceHistoryService.createDeviceHistory(deviceHistory);
			} else {
				throw new RequestException(DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorCode(),
						DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(DeviceErrorCode.DEVICE_INSERT_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_UPDATE_EXCEPTION.getErrorMessage() + " " + ExceptionUtils.parseException(e));
		}

		IdAndLanguageCodeID idAndLanguageCodeID = new IdAndLanguageCodeID();
		idAndLanguageCodeID.setId(entity.getId());
		idAndLanguageCodeID.setLangCode(entity.getLangCode());

		return idAndLanguageCodeID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DeviceService#deleteDevice(java.lang.
	 * String)
	 */
	@Override
	@Transactional
	public IdResponseDto deleteDevice(String id) {
		List<Device> foundDeviceList = new ArrayList<>();
		Device deletedDevice = null;
		try {
			foundDeviceList = deviceRepository.findByIdAndIsDeletedFalseOrIsDeletedIsNull(id);

			if (!foundDeviceList.isEmpty()) {
				for (Device foundDevice : foundDeviceList) {

					List<RegistrationCenterMachineDevice> registrationCenterMachineDeviceList = registrationCenterMachineDeviceRepository
							.findByDeviceIdAndIsDeletedFalseOrIsDeletedIsNull(foundDevice.getId());
					List<RegistrationCenterDevice> registrationCenterDeviceList = registrationCenterDeviceRepository
							.findByDeviceIdAndIsDeletedFalseOrIsDeletedIsNull(foundDevice.getId());
					if (registrationCenterMachineDeviceList.isEmpty() && registrationCenterDeviceList.isEmpty()) {

						MetaDataUtils.setDeleteMetaData(foundDevice);
						deletedDevice = deviceRepository.update(foundDevice);

						DeviceHistory deviceHistory = new DeviceHistory();
						MapperUtils.map(deletedDevice, deviceHistory);
						MapperUtils.setBaseFieldValue(deletedDevice, deviceHistory);

						deviceHistory.setEffectDateTime(deletedDevice.getDeletedDateTime());
						deviceHistory.setDeletedDateTime(deletedDevice.getDeletedDateTime());
						deviceHistoryService.createDeviceHistory(deviceHistory);
					} else {
						throw new RequestException(DeviceErrorCode.DEPENDENCY_EXCEPTION.getErrorCode(),
								DeviceErrorCode.DEPENDENCY_EXCEPTION.getErrorMessage());
					}
				}
			} else {
				throw new RequestException(DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorCode(),
						DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorMessage());
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(DeviceErrorCode.DEVICE_DELETE_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_DELETE_EXCEPTION.getErrorMessage() + " " + ExceptionUtils.parseException(e));
		}
		IdResponseDto idResponseDto = new IdResponseDto();
		idResponseDto.setId(id);
		return idResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.MachineService#
	 * getRegistrationCenterMachineMapping1(java.lang.String)
	 */
	@Override
	public PageDto<DeviceRegistrationCenterDto> getDevicesByRegistrationCenter(String regCenterId, int page, int size,
			String orderBy, String direction) {
		PageDto<DeviceRegistrationCenterDto> pageDto = new PageDto<>();
		List<DeviceRegistrationCenterDto> deviceRegistrationCenterDtoList = null;
		Page<Device> pageEntity = null;

		try {
			pageEntity = deviceRepository.findDeviceByRegCenterId(regCenterId,
					PageRequest.of(page, size, Sort.by(Direction.fromString(direction), orderBy)));
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DeviceErrorCode.REGISTRATION_CENTER_DEVICE_FETCH_EXCEPTION.getErrorCode(),
					DeviceErrorCode.REGISTRATION_CENTER_DEVICE_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		if (pageEntity != null && !pageEntity.getContent().isEmpty()) {
			deviceRegistrationCenterDtoList = MapperUtils.mapAll(pageEntity.getContent(),
					DeviceRegistrationCenterDto.class);
			for (DeviceRegistrationCenterDto deviceRegistrationCenterDto : deviceRegistrationCenterDtoList) {
				deviceRegistrationCenterDto.setRegCentId(regCenterId);
			}
		} else {
			throw new RequestException(DeviceErrorCode.DEVICE_REGISTRATION_CENTER_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_REGISTRATION_CENTER_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		pageDto.setPageNo(pageEntity.getNumber());
		pageDto.setPageSize(pageEntity.getSize());
		pageDto.setSort(pageEntity.getSort());
		pageDto.setTotalItems(pageEntity.getTotalElements());
		pageDto.setTotalPages(pageEntity.getTotalPages());
		pageDto.setData(deviceRegistrationCenterDtoList);

		return pageDto;

	}

}
