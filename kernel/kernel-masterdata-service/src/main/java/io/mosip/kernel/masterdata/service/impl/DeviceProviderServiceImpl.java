package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.deviceprovidermanager.spi.DeviceProviderService;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.masterdata.constant.DeviceProviderManagementErrorCode;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.dto.DeviceProviderDto;
import io.mosip.kernel.masterdata.dto.DigitalIdDto;
import io.mosip.kernel.masterdata.dto.ValidateDeviceDto;
import io.mosip.kernel.masterdata.dto.ValidateDeviceHistoryDto;
import io.mosip.kernel.masterdata.dto.getresponse.ResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DeviceProviderExtnDto;
import io.mosip.kernel.masterdata.entity.DeviceProvider;
import io.mosip.kernel.masterdata.entity.DeviceProviderHistory;
import io.mosip.kernel.masterdata.entity.MOSIPDeviceService;
import io.mosip.kernel.masterdata.entity.MOSIPDeviceServiceHistory;
import io.mosip.kernel.masterdata.entity.RegisteredDevice;
import io.mosip.kernel.masterdata.entity.RegisteredDeviceHistory;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.exception.ValidationException;
import io.mosip.kernel.masterdata.repository.DeviceProviderHistoryRepository;
import io.mosip.kernel.masterdata.repository.DeviceProviderRepository;
import io.mosip.kernel.masterdata.repository.MOSIPDeviceServiceHistoryRepository;
import io.mosip.kernel.masterdata.repository.MOSIPDeviceServiceRepository;
import io.mosip.kernel.masterdata.repository.RegisteredDeviceHistoryRepository;
import io.mosip.kernel.masterdata.repository.RegisteredDeviceRepository;

import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * Device provider service class
 * 
 * @author Srinivasan
 * @author Megha Tanga
 *
 */
@Service
public class DeviceProviderServiceImpl implements DeviceProviderService<ResponseDto,ValidateDeviceDto,ValidateDeviceHistoryDto,DeviceProviderDto,DeviceProviderExtnDto> {

	private static final String UTC_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	private static final String REGISTERED = "Registered";

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
	public ResponseDto validateDeviceProviders(ValidateDeviceDto validateDeviceDto) {
		ResponseDto responseDto = new ResponseDto();
		findRegisteredDevice(validateDeviceDto.getDeviceCode());
		isDeviceProviderPresent(validateDeviceDto.getDigitalId().getDpId());
		isValidServiceSoftwareVersion(validateDeviceDto.getDeviceServiceVersion());
		checkMappingBetweenProviderAndService(validateDeviceDto.getDigitalId().getDpId(),
				validateDeviceDto.getDeviceServiceVersion(),validateDeviceDto.getDigitalId());
		//checkMappingBetweenSwVersionDeviceTypeAndDeviceSubType(validateDeviceDto.getDeviceCode());
		checkMappingBetweenSwVersionDeviceTypeAndDeviceSubType(validateDeviceDto.getDeviceServiceVersion(),validateDeviceDto.getDeviceCode());
		validateDeviceCodeAndDigitalId(validateDeviceDto.getDeviceCode(), validateDeviceDto.getDigitalId());
		responseDto.setStatus(MasterDataConstant.VALID);
		responseDto.setMessage("Device  details validated successfully");

		return responseDto;
	}

	private RegisteredDevice findRegisteredDevice(String deviceCode) {
		RegisteredDevice registeredDevice = null;
		try {
			registeredDevice = registeredDeviceRepository.findByCodeAndIsActiveIsTrue(deviceCode);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorCode(),
					DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorMessage());
		}

		if (registeredDevice == null) {
			throw new DataNotFoundException(DeviceProviderManagementErrorCode.DEVICE_DOES_NOT_EXIST.getErrorCode(),
					DeviceProviderManagementErrorCode.DEVICE_DOES_NOT_EXIST.getErrorMessage());
		}
		if (!registeredDevice.getStatusCode().equalsIgnoreCase(REGISTERED)) {
			throw new RequestException(DeviceProviderManagementErrorCode.DEVICE_REVOKED_OR_RETIRED.getErrorCode(),
					DeviceProviderManagementErrorCode.DEVICE_REVOKED_OR_RETIRED.getErrorMessage());
		}

		return registeredDevice;

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
			throw new DataNotFoundException(DeviceProviderManagementErrorCode.DEVICE_PROVIDER_INACTIVE.getErrorCode(),
					DeviceProviderManagementErrorCode.DEVICE_PROVIDER_INACTIVE.getErrorMessage());
		}

		return true;

	}

	private boolean isValidServiceSoftwareVersion(String serviceSoftwareVersion) {
		List<MOSIPDeviceService> deviceServices = null;
		try {
			deviceServices = deviceServiceRepository.findBySwVersionAndIsActiveIsTrue(serviceSoftwareVersion);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorCode(),
					DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorMessage());
		}
		if (deviceServices.isEmpty()) {
			throw new DataNotFoundException(DeviceProviderManagementErrorCode.MDS_INACTIVE_STATE.getErrorCode(),
					DeviceProviderManagementErrorCode.MDS_INACTIVE_STATE.getErrorMessage());
		}

		return true;
	}

	private boolean checkMappingBetweenProviderAndService(String providerId, String swServiceVersion, DigitalIdDto digitalIdDto) {
		MOSIPDeviceService deviceService = null;
		try {
			deviceService = deviceServiceRepository.findByDeviceProviderIdAndSwVersionAndMakeAndModel(providerId, swServiceVersion,digitalIdDto.getMake(),digitalIdDto.getModel());
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorCode(),
					DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorMessage());
		}

		if (deviceService == null) {
			throw new DataNotFoundException(
					DeviceProviderManagementErrorCode.SOFTWARE_VERSION_IS_NOT_A_MATCH.getErrorCode(),
					DeviceProviderManagementErrorCode.SOFTWARE_VERSION_IS_NOT_A_MATCH.getErrorMessage());
		}
		return true;

	}

	/**
	 * Check mapping between sw version device type and device sub type.
	 *
	 * @param deviceCode
	 *            the device code
	 * @return true, if successful
	 */
	private boolean checkMappingBetweenSwVersionDeviceTypeAndDeviceSubType(String swVersion,String deviceCode) {
	    RegisteredDevice registeredDevice = null;
		MOSIPDeviceService mosipDeviceService=null;
		try {
			registeredDevice = registeredDeviceRepository.findByCodeAndIsActiveIsTrue(deviceCode);
			
			mosipDeviceService=deviceServiceRepository.findByDeviceDetail(swVersion, registeredDevice.getDeviceTypeCode(),
					registeredDevice.getDeviceSTypeCode(), registeredDevice.getMake(), registeredDevice.getModel(),
					registeredDevice.getDpId());
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorCode(),
					DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorMessage());
		}

		if (mosipDeviceService == null) {
			throw new DataNotFoundException(
					DeviceProviderManagementErrorCode.SOFTWARE_VERSION_IS_NOT_A_MATCH.getErrorCode(),
					DeviceProviderManagementErrorCode.SOFTWARE_VERSION_IS_NOT_A_MATCH.getErrorMessage());
		}

		return true;

	}

	private void validateDeviceCodeAndDigitalId(String deviceCode, DigitalIdDto digitalIdDto) {
		RegisteredDevice registeredDevice = findRegisteredDevice(deviceCode);
		List<ServiceError> serviceErrors = new ArrayList<>();
		if (!registeredDevice.getMake().equals(digitalIdDto.getMake())) {
			ServiceError serviceError = new ServiceError();
			serviceError
					.setErrorCode(DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorCode());
			serviceError.setMessage(String.format(
					DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorMessage(),
					digitalIdDto.getMake()));
			serviceErrors.add(serviceError);
		}
		if (!registeredDevice.getModel().equals(digitalIdDto.getModel())) {
			ServiceError serviceError = new ServiceError();
			serviceError
					.setErrorCode(DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorCode());
			serviceError.setMessage(String.format(
					DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorMessage(),
					digitalIdDto.getModel()));
			serviceErrors.add(serviceError);
		}
		if (!registeredDevice.getDpId().equals(digitalIdDto.getDpId())) {
			ServiceError serviceError = new ServiceError();
			serviceError
					.setErrorCode(DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorCode());
			serviceError.setMessage(String.format(
					DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorMessage(),
					digitalIdDto.getDp()));
			serviceErrors.add(serviceError);
		}
		if (!registeredDevice.getDp().equals(digitalIdDto.getDp())) {
			ServiceError serviceError = new ServiceError();
			serviceError
					.setErrorCode(DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorCode());
			serviceError.setMessage(String.format(
					DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorMessage(),
					digitalIdDto.getDp()));
			serviceErrors.add(serviceError);
		}
		if (!registeredDevice.getSerialNo().equals(digitalIdDto.getSerialNo())) {
			ServiceError serviceError = new ServiceError();
			serviceError
					.setErrorCode(DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorCode());
			serviceError.setMessage(String.format(
					DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorMessage(),
					digitalIdDto.getSerialNo()));
			serviceErrors.add(serviceError);
		}
		if (!serviceErrors.isEmpty()) {
			throw new ValidationException(serviceErrors);
		}else {
			serviceErrors=null;
		}

	}

	@Override
	public ResponseDto validateDeviceProviderHistory(ValidateDeviceHistoryDto validateDeviceDto) {
		ResponseDto responseDto = new ResponseDto();
		responseDto.setStatus(MasterDataConstant.INVALID);
		responseDto.setMessage("Device details history is invalid");
		LocalDateTime effTimes = parseToLocalDateTime(validateDeviceDto.getTimeStamp());
		RegisteredDeviceHistory registeredDeviceHistory=isRegisteredDeviceHistory(validateDeviceDto.getDeviceCode(), effTimes);
		isDeviceProviderHistoryPresent(validateDeviceDto.getDigitalId().getDpId(), effTimes);
		isValidServiceVersionFromHistory(validateDeviceDto.getDeviceServiceVersion(), effTimes);
		/*checkMappingBetweenProviderAndDeviceCodeHistory(validateDeviceDto.getDeviceCode(),
				validateDeviceDto.getDigitalId().getDp(), effTimes);*/
		//checkMappingBetweenSwVersionDeviceTypeAndDeviceSubType(validateDeviceDto.getDeviceCode());
		checkMappingBetweenSwVersionDeviceTypeAndDeviceSubType(validateDeviceDto.getDeviceServiceVersion(),validateDeviceDto.getDeviceCode());
		validateDigitalIdWithRegisteredDeviceHistory(registeredDeviceHistory,validateDeviceDto.getDigitalId());
		responseDto.setStatus(MasterDataConstant.VALID);
		responseDto.setMessage("Device details history validated successfully");

		return responseDto;
	}

	private void validateDigitalIdWithRegisteredDeviceHistory(RegisteredDeviceHistory registeredDevice,
			DigitalIdDto digitalIdDto) {
		List<ServiceError> serviceErrors = new ArrayList<>();
		if (!registeredDevice.getMake().equals(digitalIdDto.getMake())) {
			ServiceError serviceError = new ServiceError();
			serviceError
					.setErrorCode(DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorCode());
			serviceError.setMessage(String.format(
					DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorMessage(),
					digitalIdDto.getMake()));
			serviceErrors.add(serviceError);
		}
		if (!registeredDevice.getModel().equals(digitalIdDto.getModel())) {
			ServiceError serviceError = new ServiceError();
			serviceError
					.setErrorCode(DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorCode());
			serviceError.setMessage(String.format(
					DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorMessage(),
					digitalIdDto.getModel()));
			serviceErrors.add(serviceError);
		}
		if (!registeredDevice.getDpId().equals(digitalIdDto.getDpId())) {
			ServiceError serviceError = new ServiceError();
			serviceError
					.setErrorCode(DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorCode());
			serviceError.setMessage(String.format(
					DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorMessage(),
					digitalIdDto.getDp()));
			serviceErrors.add(serviceError);
		}
		if (!registeredDevice.getDp().equals(digitalIdDto.getDp())) {
			ServiceError serviceError = new ServiceError();
			serviceError
					.setErrorCode(DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorCode());
			serviceError.setMessage(String.format(
					DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorMessage(),
					digitalIdDto.getDp()));
			serviceErrors.add(serviceError);
		}
		if (!registeredDevice.getSerialNo().equals(digitalIdDto.getSerialNo())) {
			ServiceError serviceError = new ServiceError();
			serviceError
					.setErrorCode(DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorCode());
			serviceError.setMessage(String.format(
					DeviceProviderManagementErrorCode.PROVIDER_AND_DEVICE_CODE_NOT_MAPPED.getErrorMessage(),
					digitalIdDto.getSerialNo()));
			serviceErrors.add(serviceError);
		}
		if (!serviceErrors.isEmpty()) {
			throw new ValidationException(serviceErrors);
		}else {
			serviceErrors=null;
		}
		
	}

	private boolean isValidServiceVersionFromHistory(String deviceServiceVersion, LocalDateTime effTimes) {
		List<MOSIPDeviceServiceHistory> deviceServiceHistory = null;
		try {
			deviceServiceHistory = deviceServiceHistoryRepository
					.findByIdAndIsActiveIsTrueAndByEffectiveTimes(deviceServiceVersion, effTimes);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorCode(),
					DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorMessage());
		}
		if (deviceServiceHistory.isEmpty()) {
			throw new RequestException(
					DeviceProviderManagementErrorCode.SOFTWARE_VERSION_IS_NOT_A_MATCH.getErrorCode(),
					DeviceProviderManagementErrorCode.SOFTWARE_VERSION_IS_NOT_A_MATCH.getErrorMessage());
		}

		return true;
	}

	private RegisteredDeviceHistory isRegisteredDeviceHistory(String deviceCode, LocalDateTime effTimes) {
		RegisteredDeviceHistory registeredDeviceHistory = null;
		try {
			registeredDeviceHistory = registeredDeviceHistoryRepository
					.findRegisteredDeviceHistoryByIdAndEffTimes(deviceCode, effTimes);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorCode(),
					DeviceProviderManagementErrorCode.DATABASE_EXCEPTION.getErrorMessage());
		}

		if (registeredDeviceHistory == null) {
			throw new RequestException(DeviceProviderManagementErrorCode.DEVICE_DOES_NOT_EXIST.getErrorCode(),
					DeviceProviderManagementErrorCode.DEVICE_DOES_NOT_EXIST.getErrorMessage());
		}
		if (!registeredDeviceHistory.getStatusCode().equalsIgnoreCase(REGISTERED)) {
			throw new RequestException(
					DeviceProviderManagementErrorCode.DEVICE_REVOKED_OR_RETIRED.getErrorCode(),
					DeviceProviderManagementErrorCode.DEVICE_REVOKED_OR_RETIRED.getErrorMessage());
		}
		return registeredDeviceHistory;

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
			throw new RequestException(
					DeviceProviderManagementErrorCode.DEVICE_PROVIDER_NOT_EXIST.getErrorCode(),
					DeviceProviderManagementErrorCode.DEVICE_PROVIDER_NOT_EXIST.getErrorMessage());
		}

		return true;

	}

	public LocalDateTime parseToLocalDateTime(String dateTime) {
		return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(UTC_DATETIME_PATTERN));
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DeviceProviderService#createDeviceProvider
	 * (io.mosip.kernel.masterdata.dto.DeviceProviderDto)
	 */
	@Override
	@Transactional
	public DeviceProviderExtnDto createDeviceProvider(DeviceProviderDto dto) {
		DeviceProvider entity = null;
		DeviceProvider crtDeviceProvider = null;
		try {

			if (deviceProviderRepository.findById(DeviceProvider.class, dto.getId()) != null) {
				throw new RequestException(DeviceProviderManagementErrorCode.DEVICE_PROVIDER_EXIST.getErrorCode(),
						String.format(DeviceProviderManagementErrorCode.DEVICE_PROVIDER_EXIST.getErrorMessage(),
								dto.getId()));
			}
			entity = MetaDataUtils.setCreateMetaData(dto, DeviceProvider.class);
			entity.setIsActive(true);
			crtDeviceProvider = deviceProviderRepository.create(entity);

			// add new row to the history table
			DeviceProviderHistory entityHistory = new DeviceProviderHistory();
			MapperUtils.map(crtDeviceProvider, entityHistory);
			MapperUtils.setBaseFieldValue(crtDeviceProvider, entityHistory);
			entityHistory.setEffectivetimes(crtDeviceProvider.getCreatedDateTime());
			entityHistory.setCreatedDateTime(crtDeviceProvider.getCreatedDateTime());
			deviceProviderHistoryRepository.create(entityHistory);

		} catch (DataAccessLayerException | DataAccessException ex) {
			throw new MasterDataServiceException(
					DeviceProviderManagementErrorCode.DEVICE_PROVIDER_INSERTION_EXCEPTION.getErrorCode(),
					DeviceProviderManagementErrorCode.DEVICE_PROVIDER_INSERTION_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(ex));
		}
		return MapperUtils.map(crtDeviceProvider, DeviceProviderExtnDto.class);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DeviceProviderService#updateDeviceProvider
	 * (io.mosip.kernel.masterdata.dto.DeviceProviderDto)
	 */
	@Override
	@Transactional
	public DeviceProviderExtnDto updateDeviceProvider(DeviceProviderDto dto) {
		DeviceProvider entity = null;
		DeviceProvider updtDeviceProvider = null;
		DeviceProvider renDeviceProvider = null;
		try {
			renDeviceProvider = deviceProviderRepository.findById(DeviceProvider.class, dto.getId());

			if (renDeviceProvider == null) {
				throw new RequestException(DeviceProviderManagementErrorCode.DEVICE_PROVIDER_NOT_EXIST.getErrorCode(),
						String.format(DeviceProviderManagementErrorCode.DEVICE_PROVIDER_NOT_EXIST.getErrorMessage(),
								dto.getId()));
			}
			entity = MetaDataUtils.setUpdateMetaData(dto, renDeviceProvider, false);
			updtDeviceProvider = deviceProviderRepository.update(entity);

			// add new row to the history table
			DeviceProviderHistory entityHistory = new DeviceProviderHistory();
			MapperUtils.map(updtDeviceProvider, entityHistory);
			MapperUtils.setBaseFieldValue(updtDeviceProvider, entityHistory);
			entityHistory.setEffectivetimes(updtDeviceProvider.getUpdatedDateTime());
			entityHistory.setCreatedDateTime(updtDeviceProvider.getUpdatedDateTime());
			deviceProviderHistoryRepository.create(entityHistory);

		} catch (DataAccessLayerException | DataAccessException ex) {
			throw new MasterDataServiceException(
					DeviceProviderManagementErrorCode.DEVICE_PROVIDER_UPDATE_EXCEPTION.getErrorCode(),
					DeviceProviderManagementErrorCode.DEVICE_PROVIDER_UPDATE_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(ex));
		}
		return MapperUtils.map(updtDeviceProvider, DeviceProviderExtnDto.class);
	}

	

}
