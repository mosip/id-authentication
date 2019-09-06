package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.constant.RegistrationCenterDeviceErrorCode;
import io.mosip.kernel.masterdata.dto.DeviceAndRegCenterMappingResponseDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterDeviceDto;
import io.mosip.kernel.masterdata.dto.ResponseRegistrationCenterDeviceDto;
import io.mosip.kernel.masterdata.entity.Device;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDevice;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDeviceHistory;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDeviceHistoryPk;
import io.mosip.kernel.masterdata.entity.Zone;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterDeviceID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.DeviceRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterDeviceHistoryRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterDeviceRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterDeviceService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.ZoneUtils;

/**
 * Service Implementation for {@link RegistrationCenterDeviceService}
 * 
 * @author Dharmesh Khandelwal
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Service
public class RegistrationCenterDeviceServiceImpl implements RegistrationCenterDeviceService {

	/**
	 * {@link RegistrationCenterDeviceRepository} instance
	 */
	@Autowired
	private RegistrationCenterDeviceRepository registrationCenterDeviceRepository;
	/**
	 * {@link RegistrationCenterDeviceHistoryRepository} instance
	 */
	@Autowired
	private RegistrationCenterDeviceHistoryRepository registrationCenterDeviceHistoryRepository;

	@Autowired
	private ZoneUtils zoneUtils;

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private RegistrationCenterRepository registrationCenterRepository;

	@Value("${mosip.primary-language}")
	private String primaryLang;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.RegistrationCenterDeviceService#
	 * createRegistrationCenterAndDevice(io.mosip.kernel.masterdata.dto.
	 * RegistrationCenterDeviceDto)
	 */
	@Override
	@Transactional
	public ResponseRegistrationCenterDeviceDto createRegistrationCenterAndDevice(
			RegistrationCenterDeviceDto requestDto) {
		ResponseRegistrationCenterDeviceDto registrationCenterDeviceDto = null;
		try {
			RegistrationCenterDevice registrationCenterDevice = MetaDataUtils.setCreateMetaData(requestDto,
					RegistrationCenterDevice.class);
			RegistrationCenterDevice savedRegistrationCenterDevice = registrationCenterDeviceRepository
					.create(registrationCenterDevice);

			RegistrationCenterDeviceHistory registrationCenterDeviceHistory = MetaDataUtils
					.setCreateMetaData(requestDto, RegistrationCenterDeviceHistory.class);
			registrationCenterDeviceHistory.getRegistrationCenterDeviceHistoryPk()
					.setEffectivetimes(registrationCenterDeviceHistory.getCreatedDateTime());
			registrationCenterDeviceHistoryRepository.create(registrationCenterDeviceHistory);

			registrationCenterDeviceDto = MapperUtils.map(savedRegistrationCenterDevice.getRegistrationCenterDevicePk(),
					ResponseRegistrationCenterDeviceDto.class);

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_CREATE_EXCEPTION.getErrorCode(),
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_CREATE_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

		return registrationCenterDeviceDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.RegistrationCenterDeviceService#
	 * deleteRegistrationCenterDeviceMapping(java.lang.String, java.lang.String)
	 */
	@Transactional
	@Override
	public RegistrationCenterDeviceID deleteRegistrationCenterDeviceMapping(String regCenterId, String deviceId) {
		RegistrationCenterDeviceID registrationCenterDeviceID = null;
		try {
			registrationCenterDeviceID = new RegistrationCenterDeviceID(regCenterId, deviceId);
			Optional<RegistrationCenterDevice> registrationCenterDevice = registrationCenterDeviceRepository
					.findAllNondeletedMappings(registrationCenterDeviceID);
			if (!registrationCenterDevice.isPresent()) {
				throw new RequestException(
						RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_DATA_NOT_FOUND.getErrorCode(),
						RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_DATA_NOT_FOUND.getErrorMessage());
			} else {
				RegistrationCenterDevice centerDevice = registrationCenterDevice.get();
				centerDevice = MetaDataUtils.setDeleteMetaData(centerDevice);
				RegistrationCenterDeviceHistory history = MapperUtils.map(centerDevice,
						RegistrationCenterDeviceHistory.class);
				history.setRegistrationCenterDeviceHistoryPk(
						MapperUtils.map(registrationCenterDeviceID, RegistrationCenterDeviceHistoryPk.class));
				history.getRegistrationCenterDeviceHistoryPk().setEffectivetimes(centerDevice.getDeletedDateTime());
				MapperUtils.setBaseFieldValue(centerDevice, history);
				registrationCenterDeviceHistoryRepository.create(history);
				registrationCenterDeviceRepository.update(centerDevice);
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_DELETE_EXCEPTION.getErrorCode(),
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_DELETE_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		return registrationCenterDeviceID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.RegistrationCenterDeviceService#
	 * unmapDeviceRegCenter(java.lang.String, java.lang.String)
	 */
	@Transactional
	@Override
	public DeviceAndRegCenterMappingResponseDto unmapDeviceRegCenter(String deviceId, String regCenterId) {
		DeviceAndRegCenterMappingResponseDto responseDto = new DeviceAndRegCenterMappingResponseDto();
		try {

			// find given Device id and registration center are in DB or not
			RegistrationCenterDevice registrationCenterDevice = registrationCenterDeviceRepository
					.findByDeviceIdAndRegCenterId(deviceId, regCenterId);
			if (registrationCenterDevice != null) {

				List<String> zoneIds;
				// get user zone and child zones list
				List<Zone> userZones = zoneUtils.getUserZones();
				zoneIds = userZones.parallelStream().map(Zone::getCode).collect(Collectors.toList());

				// get given device id zone
				Device deviceZone = deviceRepository.findByIdAndLangCode(deviceId, primaryLang);
				// get given registration center zone id
				RegistrationCenter regCenterZone = registrationCenterRepository.findByLangCodeAndId(regCenterId,
						primaryLang);
				// check the given device and registration center zones are come under user zone
				if (!(zoneIds.contains(deviceZone.getZoneCode()) && zoneIds.contains(regCenterZone.getZoneCode()))) {
					throw new RequestException(RegistrationCenterDeviceErrorCode.INVALIDE_ZONE.getErrorCode(),
							RegistrationCenterDeviceErrorCode.INVALIDE_ZONE.getErrorMessage());
				}

				// todo

				if (!registrationCenterDevice.getIsActive()) {
					throw new RequestException(
							RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_ALREADY_UNMAPPED_EXCEPTION
									.getErrorCode(),
							RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_ALREADY_UNMAPPED_EXCEPTION
									.getErrorMessage());
				}
				if (registrationCenterDevice.getIsActive()) {
					RegistrationCenterDevice updRegistrationCenterDevice;

					registrationCenterDevice.setIsActive(false);
					registrationCenterDevice.setUpdatedBy(MetaDataUtils.getContextUser());
					registrationCenterDevice.setUpdatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));

					updRegistrationCenterDevice = registrationCenterDeviceRepository.update(registrationCenterDevice);

					// ----------------update history-------------------------------

					RegistrationCenterDeviceHistory registrationCenterDeviceHistory = new RegistrationCenterDeviceHistory();
					MapperUtils.map(updRegistrationCenterDevice, registrationCenterDeviceHistory);
					MapperUtils.setBaseFieldValue(updRegistrationCenterDevice, registrationCenterDeviceHistory);
					registrationCenterDeviceHistory.setRegistrationCenterDeviceHistoryPk(
							MapperUtils.map(new RegistrationCenterDeviceID(regCenterId, deviceId),
									RegistrationCenterDeviceHistoryPk.class));
					registrationCenterDeviceHistory.getRegistrationCenterDeviceHistoryPk()
							.setEffectivetimes(updRegistrationCenterDevice.getUpdatedDateTime());
					registrationCenterDeviceHistory
							.setUpdatedDateTime(updRegistrationCenterDevice.getUpdatedDateTime());
					registrationCenterDeviceHistoryRepository.create(registrationCenterDeviceHistory);
                    
					// set success response
					responseDto.setStatus(MasterDataConstant.UNMAPPED_SUCCESSFULLY);
					responseDto.setMessage(
							String.format(MasterDataConstant.DEVICE_AND_REGISTRATION_CENTER_UNMAPPING_SUCCESS_MESSAGE,
									deviceId, regCenterId));

				}

			} else {
				throw new RequestException(
						RegistrationCenterDeviceErrorCode.DEVICE_AND_REG_CENTER_MAPPING_NOT_FOUND_EXCEPTION
								.getErrorCode(),
						String.format(
								RegistrationCenterDeviceErrorCode.DEVICE_AND_REG_CENTER_MAPPING_NOT_FOUND_EXCEPTION
										.getErrorMessage(),
								deviceId, regCenterId));
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_FETCH_EXCEPTION.getErrorMessage());
		}
		return responseDto;
	}

}
