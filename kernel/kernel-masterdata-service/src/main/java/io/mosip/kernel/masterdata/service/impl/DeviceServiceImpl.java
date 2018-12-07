package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.DeviceErrorCode;
import io.mosip.kernel.masterdata.dto.DeviceDto;
import io.mosip.kernel.masterdata.dto.DeviceLangCodeDtypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceLangCodeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.Device;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DeviceRepository;
import io.mosip.kernel.masterdata.service.DeviceService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * This class have methods to fetch a Device Details
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
	 * Field to hold ObjectMapperUtil object
	 */
	@Autowired
	MapperUtils objectMapperUtil;

	@Autowired
	MetaDataUtils metaDataUtils;

	@Autowired
	DataMapper dataMapper;

	/**
	 * Method used for fetch all Device details based on given language code
	 * 
	 * @return DeviceDto returning Device Details based on given language code
	 * 
	 * @param langCode
	 *            pass language as string
	 * 
	 * @throws MasterDataServiceException
	 *             While Fetching Device Detail If fails to fetch required Device
	 *             Detail
	 * 
	 * @throws DeviceMappingException
	 *             If not able to map Device detail entity with Device Detail Dto
	 * 
	 * @throws DataNotFoundException
	 *             If given required Device ID and language not found
	 * 
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
			deviceDtoList = objectMapperUtil.mapAllNew(deviceList, DeviceDto.class);
					
		} else {
			throw new DataNotFoundException(DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		deviceResponseDto.setDevices(deviceDtoList);
		return deviceResponseDto;
	}

	/**
	 * Method used for fetch all Device details based on given language code and
	 * Device type
	 * 
	 * @return DeviceDto returning all Device Detail based on language code and
	 *         Device type
	 * 
	 * @param langCode
	 *            pass language code as String
	 * @param dtypeCode
	 *            pass Device type as String
	 * 
	 * @throws MasterDataServiceException
	 *             While Fetching Device Detail If fails to fetch required Device
	 *             Detail
	 * @throws DeviceMappingException
	 *             If not able to map Device detail entity with Device Detail Dto
	 * @throws DataNotFoundException
	 *             If given required Device ID and language not found
	 * 
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
			deviceLangCodeDtypeDtoList = objectMapperUtil.mapDeviceDto(objectList);
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
	public IdResponseDto saveDevice(RequestDto<DeviceDto> deviceDto) {
		Device device;

		Device entity = metaDataUtils.setCreateMetaData(deviceDto.getRequest(), Device.class);
		try {
			device = deviceRepository.create(entity);
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceErrorCode.DEVICE_CREATE_EXCEPTION.getErrorCode(),
					ExceptionUtils.parseException(e));
		}
		IdResponseDto idResponseDto = new IdResponseDto();
		objectMapperUtil.mapNew(device, idResponseDto);

		return idResponseDto;

	}

}
