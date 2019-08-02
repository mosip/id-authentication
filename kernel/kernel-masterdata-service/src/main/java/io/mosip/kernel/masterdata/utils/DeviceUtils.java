package io.mosip.kernel.masterdata.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.DeviceSpecificationErrorCode;
import io.mosip.kernel.masterdata.constant.DeviceTypeErrorCode;
import io.mosip.kernel.masterdata.constant.RegistrationCenterDeviceErrorCode;
import io.mosip.kernel.masterdata.constant.RegistrationCenterErrorCode;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDevice;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DeviceSpecificationRepository;
import io.mosip.kernel.masterdata.repository.DeviceTypeRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterDeviceRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterRepository;

@Component
public class DeviceUtils {
	
	@Autowired
	private DeviceTypeRepository deviceTypeRepository;

	@Autowired
	private DeviceSpecificationRepository deviceSpecificationRepository;

	@Autowired
	private RegistrationCenterDeviceRepository deviceCenterRepository;

	@Autowired
	private RegistrationCenterRepository centerRepository;

	public List<DeviceSpecification> getDeviceSpec() {
		try {
			return deviceSpecificationRepository.findAllDeviceSpecByIsActiveAndIsDeletedIsNullOrFalse();
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_DATA_FETCH_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_DATA_FETCH_EXCEPTION.getErrorMessage());
		}
	}

	public List<DeviceType> getDeviceTypes() {
		try {
			return deviceTypeRepository.findAllDeviceTypeByIsActiveAndIsDeletedFalseOrNull();

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(DeviceTypeErrorCode.DEVICE_TYPE_FETCH_EXCEPTION.getErrorCode(),
					DeviceTypeErrorCode.DEVICE_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}
	}

	public List<RegistrationCenterDevice> getAllDeviceCentersList() {
		try {
			return deviceCenterRepository.findAllCenterDevices();
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_FETCH_EXCEPTION.getErrorMessage());
		}
	}

	public List<RegistrationCenter> getAllRegistrationCenters() {
		try {
			return centerRepository.findAllByIsDeletedFalseOrIsDeletedIsNull();
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterErrorCode.REGISTRATION_CENTER_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterErrorCode.REGISTRATION_CENTER_FETCH_EXCEPTION.getErrorMessage());
		}
	}

}
