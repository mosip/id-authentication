package io.mosip.kernel.masterdata.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.masterdata.constant.DeviceRegisterErrorCode;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.constant.RegisteredDeviceErrorCode;
import io.mosip.kernel.masterdata.dto.DeviceDeRegisterResponse;
import io.mosip.kernel.masterdata.dto.DigitalIdDto;
import io.mosip.kernel.masterdata.dto.EncodedRegisteredDeviceResponse;
import io.mosip.kernel.masterdata.dto.RegisteredDevicePostReqDto;
import io.mosip.kernel.masterdata.dto.getresponse.ResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.RegisteredDeviceExtnDto;
import io.mosip.kernel.masterdata.entity.Device;
import io.mosip.kernel.masterdata.entity.DeviceProvider;
import io.mosip.kernel.masterdata.entity.RegisteredDevice;
import io.mosip.kernel.masterdata.entity.RegisteredDeviceHistory;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.DeviceRegisterException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.DeviceProviderRepository;
import io.mosip.kernel.masterdata.repository.DeviceRepository;
import io.mosip.kernel.masterdata.repository.RegisteredDeviceHistoryRepository;
import io.mosip.kernel.masterdata.repository.RegisteredDeviceRepository;
import io.mosip.kernel.masterdata.repository.RegistrationDeviceSubTypeRepository;
import io.mosip.kernel.masterdata.repository.RegistrationDeviceTypeRepository;
import io.mosip.kernel.masterdata.service.RegisteredDeviceService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.validator.registereddevice.RegisteredDeviceConstant;

/**
 * Service Class for Create RegisteredDevice
 * 
 * @author Megha Tanga
 *
 */

@Service
public class RegisteredDeviceServiceImpl implements RegisteredDeviceService {

	@Autowired
	RegisteredDeviceRepository registeredDeviceRepository;

	@Autowired
	RegisteredDeviceHistoryRepository registeredDeviceHistoryRepo;

	@Autowired
	DeviceProviderRepository deviceProviderRepository;

	@Autowired
	RegistrationDeviceTypeRepository registrationDeviceTypeRepository;

	@Autowired
	RegistrationDeviceSubTypeRepository registrationDeviceSubTypeRepository;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	DeviceRepository deviceRepository;
	
	
	/** The registered. */
	private static String REGISTERED = "Registered";

	/** The revoked. */
	private static String REVOKED = "Revoked";

	/** The retired. */
	private static String RETIRED = "Retired";

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.RegisteredDeviceService#
	 * createRegisteredDevice(io.mosip.kernel.masterdata.dto.
	 * RegisteredDevicePostReqDto)
	 */
	@Transactional
	@Override
	public EncodedRegisteredDeviceResponse createRegisteredDevice(RegisteredDevicePostReqDto dto) {
		RegisteredDevice entity = null;
		RegisteredDevice mapEntity = null;
		RegisteredDevice crtRegisteredDevice = null;
		RegisteredDeviceExtnDto renRegisteredDeviceExtnDto = null;
		EncodedRegisteredDeviceResponse encodedRegisteredDeviceResponse = new EncodedRegisteredDeviceResponse();
		DigitalIdDto digitalIdDto = null;
		String digitalIdJson;
		String ecodedRegisteredDeviceExtnDto;
		String strRegisteredDeviceExtnDto;
		try {
			DeviceProvider deviceProvider = deviceProviderRepository.findByIdAndNameAndIsDeletedFalseorIsDeletedIsNullAndIsActiveTrue(
					dto.getDigitalIdDto().getDpId(), dto.getDigitalIdDto().getDp());
			if (deviceProvider == null) {
				throw new RequestException(RegisteredDeviceErrorCode.DEVICE_PROVIDER_NOT_EXIST.getErrorCode(),
						RegisteredDeviceErrorCode.DEVICE_PROVIDER_NOT_EXIST.getErrorMessage());
			}

			if (registrationDeviceTypeRepository
					.findByCodeAndIsDeletedFalseorIsDeletedIsNullAndIsActiveTrue(dto.getDigitalIdDto().getDeviceTypeCode()) == null) {
				throw new RequestException(RegisteredDeviceErrorCode.DEVICE_TYPE_NOT_EXIST.getErrorCode(),
						String.format(RegisteredDeviceErrorCode.DEVICE_TYPE_NOT_EXIST.getErrorMessage(),
								dto.getDigitalIdDto().getDeviceTypeCode()));
			}

			if (registrationDeviceSubTypeRepository
					.findByCodeAndIsDeletedFalseorIsDeletedIsNullAndIsActiveTrue(dto.getDigitalIdDto().getDeviceSTypeCode()) == null) {
				throw new RequestException(RegisteredDeviceErrorCode.DEVICE_SUB_TYPE_NOT_EXIST.getErrorCode(),
						String.format(RegisteredDeviceErrorCode.DEVICE_SUB_TYPE_NOT_EXIST.getErrorMessage(),
								dto.getDigitalIdDto().getDeviceSTypeCode()));
			}

			digitalIdJson = mapper.writeValueAsString(dto.getDigitalIdDto());
			mapEntity = MapperUtils.mapRegisteredDeviceDto(dto, digitalIdJson);
			entity = MetaDataUtils.setCreateMetaData(mapEntity, RegisteredDevice.class);
			entity.setCode(generateCodeValue(dto));
			entity.setIsActive(true);
			crtRegisteredDevice = registeredDeviceRepository.create(entity);

			// add new row to the history table
			RegisteredDeviceHistory entityHistory = new RegisteredDeviceHistory();
			MapperUtils.map(crtRegisteredDevice, entityHistory);
			MapperUtils.setBaseFieldValue(crtRegisteredDevice, entityHistory);
			entityHistory.setDigitalId(digitalIdJson);
			entityHistory.setEffectivetimes(crtRegisteredDevice.getCreatedDateTime());
			entityHistory.setCreatedDateTime(crtRegisteredDevice.getCreatedDateTime());
			registeredDeviceHistoryRepo.create(entityHistory);
			

			digitalIdDto = mapper.readValue(digitalIdJson, DigitalIdDto.class);
			
			renRegisteredDeviceExtnDto = MapperUtils.map(crtRegisteredDevice, RegisteredDeviceExtnDto.class);
			renRegisteredDeviceExtnDto.setDigitalIdDto(digitalIdDto);
			// DTO to String
			strRegisteredDeviceExtnDto = mapper.writeValueAsString(renRegisteredDeviceExtnDto);
			ecodedRegisteredDeviceExtnDto = CryptoUtil.encodeBase64(strRegisteredDeviceExtnDto.getBytes());
			
			encodedRegisteredDeviceResponse.setEnocodedResponse(ecodedRegisteredDeviceExtnDto);

		} catch (DataAccessLayerException | DataAccessException | IOException ex) {
			throw new MasterDataServiceException(
					RegisteredDeviceErrorCode.REGISTERED_DEVICE_INSERTION_EXCEPTION.getErrorCode(),
					RegisteredDeviceErrorCode.REGISTERED_DEVICE_INSERTION_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(ex));
		}
		
		return encodedRegisteredDeviceResponse;
	}

	// check the value of purpose and Serial Num, based on this generate the code
	// value
	private String generateCodeValue(RegisteredDevicePostReqDto dto) {
		String code = "";
			if (dto.getPurpose().equalsIgnoreCase(RegisteredDeviceConstant.REGISTRATION)) {
				List<Device> device = deviceRepository.findDeviceBySerialNumberAndIsDeletedFalseorIsDeletedIsNullNoIsActive(
						dto.getDigitalIdDto().getSerialNo());
				if (device.isEmpty()) {
					throw new RequestException(RegisteredDeviceErrorCode.SERIALNUM_NOT_EXIST.getErrorCode(),
							String.format(RegisteredDeviceErrorCode.SERIALNUM_NOT_EXIST.getErrorMessage(),
									dto.getDigitalIdDto().getSerialNo()));
				}
				// copy Device id as code
				code = device.get(0).getId();
			} else if (dto.getPurpose().equalsIgnoreCase(RegisteredDeviceConstant.AUTH)) {
				// should be uniquely randomly generated
				code = UUID.randomUUID().toString();
			}
		return code;
	}

	@Override
	public DeviceDeRegisterResponse deRegisterDevice(@Valid String deviceCode) {
		RegisteredDevice deviceRegisterEntity = null;
		RegisteredDeviceHistory deviceRegisterHistory = new RegisteredDeviceHistory();
		try {
			deviceRegisterEntity = registeredDeviceRepository.findByCodeAndIsActiveIsTrue(deviceCode);
			if (deviceRegisterEntity != null) {
				deviceRegisterEntity.setStatusCode("Retired");
				MapperUtils.map(deviceRegisterEntity, deviceRegisterHistory);
				deviceRegisterHistory.setEffectivetimes(LocalDateTime.now(ZoneId.of("UTC")));
				registeredDeviceRepository.update(deviceRegisterEntity);
				registeredDeviceHistoryRepo.create(deviceRegisterHistory);
			} else {
				throw new DeviceRegisterException("KER-MSD-xx", "No register device found");
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new DeviceRegisterException("KER-MSD-xx",
					"Error occur while deregistering device details " + ExceptionUtils.parseException(e));
		}
		DeviceDeRegisterResponse response = new DeviceDeRegisterResponse();
		response.setStatus("success");
		response.setMessage("Device deregistered successfully");
		return response;
	}
	
	@Transactional
	@Override
	public ResponseDto updateStatus(String deviceCode, String statusCode) {
		RegisteredDevice deviceRegister = null;
		try {
			deviceRegister = registeredDeviceRepository.findByCodeAndIsActiveIsTrue(deviceCode);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceRegisterErrorCode.DEVICE_REGISTER_FETCH_EXCEPTION.getErrorCode(),
					DeviceRegisterErrorCode.DEVICE_REGISTER_FETCH_EXCEPTION.getErrorMessage() + " " + ExceptionUtils.parseException(e));
		}

		if (deviceRegister == null) {
			throw new DataNotFoundException(DeviceRegisterErrorCode.DATA_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceRegisterErrorCode.DATA_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		if (!Arrays.asList(REGISTERED, REVOKED, RETIRED).contains(statusCode)) {
			throw new RequestException(DeviceRegisterErrorCode.INVALID_STATUS_CODE.getErrorCode(),
					DeviceRegisterErrorCode.INVALID_STATUS_CODE.getErrorMessage());
		}
		deviceRegister.setStatusCode(statusCode);
		updateRegisterDetails(deviceRegister);
		createHistoryDetails(deviceRegister);
		ResponseDto responseDto = new ResponseDto();
		responseDto.setMessage(MasterDataConstant.DEVICE_REGISTER_UPDATE_MESSAGE);
		responseDto.setStatus(MasterDataConstant.SUCCESS);
		return responseDto;
	}
	
	/**
	 * Creates the history details.
	 *
	 * @param deviceRegister
	 *            the device register
	 */
	private void createHistoryDetails(RegisteredDevice deviceRegister) {
		RegisteredDeviceHistory deviceRegisterHistory = new RegisteredDeviceHistory();
		MapperUtils.map(deviceRegister, deviceRegisterHistory);
		deviceRegisterHistory.setEffectivetimes(LocalDateTime.now(ZoneId.of("UTC")));
		try {
			registeredDeviceHistoryRepo.create(deviceRegisterHistory);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(
					DeviceRegisterErrorCode.DEVICE_REGISTER_UPDATE_EXCEPTION.getErrorCode(),
					DeviceRegisterErrorCode.DEVICE_REGISTER_UPDATE_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}

	}

	/**
	 * Update register details.
	 *
	 * @param deviceRegister
	 *            the device register
	 */
	private void updateRegisterDetails(RegisteredDevice deviceRegister) {
		try {
			deviceRegister = MetaDataUtils.setUpdateMetaData(deviceRegister, deviceRegister, false);
			registeredDeviceRepository.update(deviceRegister);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(
					DeviceRegisterErrorCode.DEVICE_REGISTER_UPDATE_EXCEPTION.getErrorCode(),
					DeviceRegisterErrorCode.DEVICE_REGISTER_UPDATE_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}

	}
}
