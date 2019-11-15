package io.mosip.kernel.masterdata.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.RegisteredDeviceErrorCode;
import io.mosip.kernel.masterdata.dto.DigitalIdDto;
import io.mosip.kernel.masterdata.dto.RegisteredDevicePostReqDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.RegisteredDeviceExtnDto;
import io.mosip.kernel.masterdata.entity.Device;
import io.mosip.kernel.masterdata.entity.RegisteredDevice;
import io.mosip.kernel.masterdata.entity.RegisteredDeviceHistory;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.RegisteredDeviceService#
	 * createRegisteredDevice(io.mosip.kernel.masterdata.dto.
	 * RegisteredDevicePostReqDto)
	 */
	@Transactional
	@Override
	public RegisteredDeviceExtnDto createRegisteredDevice(RegisteredDevicePostReqDto dto) {
		RegisteredDevice entity = null;
		RegisteredDevice mapEntity = null;
		RegisteredDevice crtRegisteredDevice = null;
		RegisteredDeviceExtnDto renRegisteredDeviceExtnDto = null;
		DigitalIdDto digitalIdDto = null;
		String digitalIdJson;

		try {
			if (deviceProviderRepository.findByIdAndNameAndIsDeletedFalseorIsDeletedIsNullAndIsActiveTrue(
					dto.getDigitalIdDto().getDpId(), dto.getDigitalIdDto().getDp()) == null) {
				throw new RequestException(RegisteredDeviceErrorCode.DEVICE_PROVIDER_NOT_EXIST.getErrorCode(),
						RegisteredDeviceErrorCode.DEVICE_PROVIDER_NOT_EXIST.getErrorMessage());
			}

			if (registrationDeviceTypeRepository
					.findByCodeAndIsDeletedFalseorIsDeletedIsNullAndIsActiveTrue(dto.getDeviceTypeCode()) == null) {
				throw new RequestException(RegisteredDeviceErrorCode.DEVICE_TYPE_NOT_EXIST.getErrorCode(),
						String.format(RegisteredDeviceErrorCode.DEVICE_TYPE_NOT_EXIST.getErrorMessage(),
								dto.getDeviceTypeCode()));
			}

			if (registrationDeviceSubTypeRepository
					.findByCodeAndIsDeletedFalseorIsDeletedIsNullAndIsActiveTrue(dto.getDeviceSTypeCode()) == null) {
				throw new RequestException(RegisteredDeviceErrorCode.DEVICE_SUB_TYPE_NOT_EXIST.getErrorCode(),
						String.format(RegisteredDeviceErrorCode.DEVICE_SUB_TYPE_NOT_EXIST.getErrorMessage(),
								dto.getDeviceSTypeCode()));
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

		} catch (DataAccessLayerException | DataAccessException | IOException ex) {
			throw new MasterDataServiceException(
					RegisteredDeviceErrorCode.REGISTERED_DEVICE_INSERTION_EXCEPTION.getErrorCode(),
					RegisteredDeviceErrorCode.REGISTERED_DEVICE_INSERTION_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(ex));
		}
		renRegisteredDeviceExtnDto = MapperUtils.map(crtRegisteredDevice, RegisteredDeviceExtnDto.class);
		renRegisteredDeviceExtnDto.setDigitalIdDto(digitalIdDto);
		return renRegisteredDeviceExtnDto;
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

}
