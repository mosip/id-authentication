package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.DeviceProviderManagementErrorCode;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.dto.getresponse.ResponseDto;
import io.mosip.kernel.masterdata.entity.DeviceProvider;

import io.mosip.kernel.masterdata.entity.MOSIPDeviceService;
import io.mosip.kernel.masterdata.entity.MOSIPDeviceServiceHistory;
import io.mosip.kernel.masterdata.entity.DeviceProviderHistory;
import io.mosip.kernel.masterdata.entity.DeviceService;
import io.mosip.kernel.masterdata.entity.DeviceServiceHistory;
import io.mosip.kernel.masterdata.entity.RegisteredDevice;
import io.mosip.kernel.masterdata.entity.RegisteredDeviceHistory;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DeviceProviderHistoryRepository;
import io.mosip.kernel.masterdata.repository.DeviceProviderRepository;
import io.mosip.kernel.masterdata.repository.MOSIPDeviceServiceRepository;
import io.mosip.kernel.masterdata.repository.MOSIPDeviceServiceHistoryRepository;
import io.mosip.kernel.masterdata.repository.RegisteredDeviceHistoryRepository;
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

	private static final String UTC_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	@Autowired
	private RegisteredDeviceRepository registeredDeviceRepository;

	@Autowired
	private DeviceProviderRepository deviceProviderRepository;

	@Autowired
	private MOSIPDeviceServiceRepository deviceServiceRepository;

	@Autowired
	private DeviceProviderHistoryRepository deviceProviderHistoryRepository;

	@Autowired
	private RegisteredDeviceHistoryRepository registeredDeviceHistoryRepository;

	@Autowired
	private MOSIPDeviceServiceHistoryRepository deviceServiceHistoryRepository;


	@Override
	public ResponseDto validateDeviceProviders(String deviceCode, String deviceProviderId, String deviceServiceId,
			String deviceServiceVersion) {
		ResponseDto responseDto = new ResponseDto();
		responseDto.setStatus(MasterDataConstant.INVALID);
		responseDto.setMessage(MasterDataConstant.INVALID);
		if (isRegisteredDevice(deviceCode) && isDeviceProviderPresent(deviceProviderId)
				&& isValidServiceId(deviceServiceId, deviceServiceVersion)
				&& checkMappingBetweenProviderAndService(deviceProviderId, deviceServiceId)) {
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

	private boolean checkMappingBetweenProviderAndService(String providerId, String serviceId) {
		MOSIPDeviceService deviceService = null;
		try {
			deviceService = deviceServiceRepository.findByIdAndDProviderId(serviceId,providerId);
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

	@Override
	public ResponseDto validateDeviceProviderHistory(String deviceCode, String deviceProviderId, String deviceServiceId,
			String deviceServiceVersion, String timeStamp) {
		ResponseDto responseDto = new ResponseDto();
		responseDto.setStatus(MasterDataConstant.INVALID);
		responseDto.setMessage(MasterDataConstant.INVALID);
		LocalDateTime effTimes = parseToLocalDateTime(timeStamp);
		if (isRegisteredDeviceHistory(deviceCode, effTimes)
				&& isDeviceProviderHistoryPresent(deviceProviderId, effTimes)
				&& isValidServiceIdFromHistory(deviceServiceId, deviceServiceVersion, effTimes)
				&& checkMappingBetweenProviderHistoryAndService(deviceServiceId, deviceProviderId, effTimes)) {
			responseDto.setStatus(MasterDataConstant.VALID);
			responseDto.setMessage(MasterDataConstant.VALID);
		}
		return responseDto;
	}

	private boolean checkMappingBetweenProviderHistoryAndService(String id, String deviceProviderId,
			LocalDateTime effTimes) {
		MOSIPDeviceServiceHistory deviceServiceHistory = null;
		try {
			deviceServiceHistory = deviceServiceHistoryRepository.findByIdAndDProviderId(id, deviceProviderId,
					effTimes);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorCode(),
					DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorMessage());
		}

		if (deviceServiceHistory == null) {
			throw new MasterDataServiceException(
					DeviceProviderManagementErrorCode.PROVIDER_AND_SERVICE_ID_NOT_MAPPED.getErrorCode(),
					DeviceProviderManagementErrorCode.PROVIDER_AND_SERVICE_ID_NOT_MAPPED.getErrorMessage());
		}
		return true;

	}

	private boolean isValidServiceIdFromHistory(String deviceServiceId, String deviceServiceVersion,
			LocalDateTime effTimes) {
		MOSIPDeviceServiceHistory deviceServiceHistory = null;
		try {
			deviceServiceHistory = deviceServiceHistoryRepository
					.findByIdAndIsActiveIsTrueAndByEffectiveTimes(deviceServiceId, effTimes);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorCode(),
					DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorMessage());
		}
		if (deviceServiceHistory == null) {
			throw new MasterDataServiceException(DeviceProviderManagementErrorCode.MDS_INACTIVE_STATE.getErrorCode(),
					DeviceProviderManagementErrorCode.MDS_INACTIVE_STATE.getErrorMessage());
		}

		if (!deviceServiceHistory.getSwVersion().equals(deviceServiceVersion)) {
			throw new MasterDataServiceException(
					DeviceProviderManagementErrorCode.SOFTWARE_VERSION_IS_NOT_A_MATCH.getErrorCode(),
					DeviceProviderManagementErrorCode.SOFTWARE_VERSION_IS_NOT_A_MATCH.getErrorMessage());
		}
		return true;
	}

	private boolean isRegisteredDeviceHistory(String deviceCode, LocalDateTime effTimes) {
		RegisteredDeviceHistory registeredDeviceHistory = null;
		try {
			registeredDeviceHistory = registeredDeviceHistoryRepository
					.findRegisteredDeviceHistoryByIdAndEffTimes(deviceCode, effTimes);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorCode(),
					DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorMessage());
		}

		if (registeredDeviceHistory == null) {
			throw new MasterDataServiceException(DeviceProviderManagementErrorCode.DEVICE_DOES_NOT_EXIST.getErrorCode(),
					DeviceProviderManagementErrorCode.DEVICE_DOES_NOT_EXIST.getErrorMessage());
		}
		if (!registeredDeviceHistory.getStatusCode().equalsIgnoreCase("Registered")) {
			throw new MasterDataServiceException(
					DeviceProviderManagementErrorCode.DEVICE_REVOKED_OR_RETIRED.getErrorCode(),
					DeviceProviderManagementErrorCode.DEVICE_REVOKED_OR_RETIRED.getErrorMessage());
		}
		return true;

	}

	private boolean isDeviceProviderHistoryPresent(String deviceProviderId, LocalDateTime timeStamp) {
		DeviceProviderHistory deviceProviderHistory = null;

		try {
			deviceProviderHistory = deviceProviderHistoryRepository
					.findDeviceProviderHisByIdAndEffTimes(deviceProviderId, timeStamp);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorCode(),
					DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorMessage());
		}
		if (deviceProviderHistory == null) {
			throw new MasterDataServiceException(
					DeviceProviderManagementErrorCode.DEVICE_PROVIDER_NOT_EXIST.getErrorCode(),
					DeviceProviderManagementErrorCode.DEVICE_PROVIDER_NOT_EXIST.getErrorMessage());
		}

		return true;

	}

	public LocalDateTime parseToLocalDateTime(String dateTime) {
		return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(UTC_DATETIME_PATTERN));
	}

}
