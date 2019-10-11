package io.mosip.kernel.masterdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.DeviceProviderManagementErrorCode;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.dto.getresponse.ResponseDto;
import io.mosip.kernel.masterdata.entity.DeviceProvider;
import io.mosip.kernel.masterdata.entity.MOSIPDeviceService;
import io.mosip.kernel.masterdata.entity.RegisteredDevice;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DeviceProviderRepository;
import io.mosip.kernel.masterdata.repository.MOSIPDeviceServiceRepository;
import io.mosip.kernel.masterdata.repository.RegisteredDeviceRepository;
import io.mosip.kernel.masterdata.service.DeviceProviderService;

/**
 * Device provider service class
 * 
 * @author Srinivasan
 *
 */
@Service
public class DeviceProviderServiceImpl implements DeviceProviderService {

	@Autowired
	private RegisteredDeviceRepository registeredDeviceRepository;

	@Autowired
	private DeviceProviderRepository deviceProviderRepository;

	@Autowired
	private MOSIPDeviceServiceRepository deviceServiceRepository;

	@Override
	public ResponseDto validateDeviceProviders(String deviceCode, String deviceProviderId, String deviceServiceId,
			String deviceServiceVersion) {
		ResponseDto responseDto = new ResponseDto();
		responseDto.setStatus(MasterDataConstant.INVALID);
		responseDto.setMessage(MasterDataConstant.INVALID);
		if (isRegisteredDevice(deviceCode) && isDeviceProviderPresent(deviceProviderId)
				&& isValidServiceId(deviceServiceId, deviceServiceVersion)
				&& checkMappingBetweenProviderAndService(deviceProviderId, deviceServiceVersion)) {
			responseDto.setStatus(MasterDataConstant.VALID);
			responseDto.setMessage(MasterDataConstant.VALID);
		}
		return responseDto;
	}

	private boolean isRegisteredDevice(String deviceCode) {
		RegisteredDevice registeredDevice = null;
		boolean isRegistered = false;
		try {
			registeredDevice = registeredDeviceRepository.findByCodeAndIsActiveIsTrue(deviceCode);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorCode(),
					DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorMessage());
		}

		if (registeredDevice == null) {
			throw new MasterDataServiceException(DeviceProviderManagementErrorCode.DEVICE_DOES_NOT_EXIST.getErrorCode(),
					DeviceProviderManagementErrorCode.DEVICE_DOES_NOT_EXIST.getErrorMessage());
		}
		if (!registeredDevice.getStatusCode().equalsIgnoreCase("Registered")) {
			throw new MasterDataServiceException(
					DeviceProviderManagementErrorCode.DEVICE_REVOKED_OR_RETIRED.getErrorCode(),
					DeviceProviderManagementErrorCode.DEVICE_REVOKED_OR_RETIRED.getErrorMessage());
		}
		isRegistered = true;

		return isRegistered;

	}

	private boolean isDeviceProviderPresent(String deviceProviderId) {
		DeviceProvider deviceProvider = null;

		try {
			deviceProvider = deviceProviderRepository.findByIdAndIsActiveIsTrue(deviceProviderId);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorCode(),
					DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorMessage());
		}
		if (deviceProvider == null) {
			throw new MasterDataServiceException(
					DeviceProviderManagementErrorCode.DEVICE_PROVIDER_INACTIVE.getErrorCode(),
					DeviceProviderManagementErrorCode.DEVICE_PROVIDER_INACTIVE.getErrorMessage());
		}

		return true;

	}

	private boolean isValidServiceId(String serviceId, String serviceSoftwareVersion) {
		MOSIPDeviceService deviceService = null;
		try {
			deviceService = deviceServiceRepository.findByIdAndIsActiveIsTrue(serviceId);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorCode(),
					DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorMessage());
		}
		if (deviceService == null) {
			throw new MasterDataServiceException(DeviceProviderManagementErrorCode.MDS_INACTIVE_STATE.getErrorCode(),
					DeviceProviderManagementErrorCode.MDS_INACTIVE_STATE.getErrorMessage());
		}

		if (!deviceService.getSwVersion().equals(serviceSoftwareVersion)) {
			throw new MasterDataServiceException(
					DeviceProviderManagementErrorCode.SOFTWARE_VERSION_IS_NOT_A_MATCH.getErrorCode(),
					DeviceProviderManagementErrorCode.SOFTWARE_VERSION_IS_NOT_A_MATCH.getErrorMessage());
		}

		return true;
	}

	private boolean checkMappingBetweenProviderAndService(String providerId, String serviceVersion) {
		MOSIPDeviceService deviceService = null;
		try {
			deviceService = deviceServiceRepository.findByIdAndDeviceProviderId(providerId, serviceVersion);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorCode(),
					DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorMessage());
		}

		if (deviceService == null) {
			throw new MasterDataServiceException(
					DeviceProviderManagementErrorCode.PROVIDER_AND_SERVICE_ID_NOT_MAPPED.getErrorCode(),
					DeviceProviderManagementErrorCode.PROVIDER_AND_SERVICE_ID_NOT_MAPPED.getErrorMessage());
		}
		return true;

	}

}
