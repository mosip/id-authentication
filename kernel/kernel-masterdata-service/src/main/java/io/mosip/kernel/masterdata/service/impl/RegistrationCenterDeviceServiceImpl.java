package io.mosip.kernel.masterdata.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.masterdata.constant.DeviceErrorCode;
import io.mosip.kernel.masterdata.constant.MachineErrorCode;
import io.mosip.kernel.masterdata.constant.RegistrationCenterDeviceErrorCode;
import io.mosip.kernel.masterdata.constant.RegistrationCenterMachineErrorCode;
import io.mosip.kernel.masterdata.dto.RegistrationCenterDeviceDto;
import io.mosip.kernel.masterdata.dto.ResponseRegistrationCenterDeviceDto;
import io.mosip.kernel.masterdata.dto.getresponse.ResponseDto;
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
 * @author Srinivasan
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
	private DeviceRepository deviceRepository;

	@Autowired
	private RegistrationCenterRepository registrationCenterRepository;

	@Value("${mosip.primary-language}")
	private String primaryLang;

	@Autowired
	private ZoneUtils zoneUtil;

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

	@Override
	public ResponseDto mapRegistrationCenterWithDevice(String regCenterId, String deviceId) {
		RegistrationCenterDevice registrationCenterDevice = getRegistrationDeviceMapping(regCenterId, deviceId);
		List<Zone> userZones = zoneUtil.getUserZones();
		boolean isMachineMappedToUserZone = false;
		boolean isRegCenterMappedToUserZone = false;
		boolean isInSameHierarchy = false;
		Zone registrationCenterZone = null;
		userZones = userZones.stream().filter(zone -> zone.getLangCode().equals(primaryLang))
				.collect(Collectors.toList());
		Device device = getDeviceBasedOnDeviceIdAndPrimaryLangCode(deviceId, primaryLang);
		List<RegistrationCenter> registrationCenters = getZoneFromRegCenterRepoByRegCenterId(regCenterId, primaryLang);
		for (Zone zone : userZones) {
			if (zone.getCode().equals(device.getZoneCode())) {
				isMachineMappedToUserZone = true;

			}

			if (zone.getCode().equals(registrationCenters.get(0).getZoneCode())) {
				isRegCenterMappedToUserZone = true;
				registrationCenterZone = zone;

			}
		}

		if (!isMachineMappedToUserZone || !isRegCenterMappedToUserZone) {
			throw new RequestException(
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_ZONE_INVALID.getErrorCode(),
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_ZONE_INVALID.getErrorMessage());
		}
		Objects.requireNonNull(registrationCenterZone, "registrationCenterZone is empty");
		String hierarchyPath = registrationCenterZone.getHierarchyPath();
		List<String> zoneHierarchy = Arrays.asList(hierarchyPath.split("/"));
		isInSameHierarchy = zoneHierarchy.stream().anyMatch(zone -> zone.equals(device.getZoneCode()));
		if (isInSameHierarchy) {
			if ((!device.getIsActive() && device.getIsDeleted())
					|| (!registrationCenters.get(0).getIsActive() && registrationCenters.get(0).getIsDeleted())) {
				throw new MasterDataServiceException(
						RegistrationCenterDeviceErrorCode.REGISTATION_CENTER_DEVICE_DECOMMISIONED_STATE.getErrorCode(),
						RegistrationCenterDeviceErrorCode.REGISTATION_CENTER_DEVICE_DECOMMISIONED_STATE
								.getErrorMessage());
			}

			if (registrationCenterDevice != null) {

				if (registrationCenterDevice.getIsActive()) {
					throw new MasterDataServiceException(
							RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_ALREADY_MAPPED.getErrorCode(),
							RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_ALREADY_MAPPED.getErrorMessage());
				} else {
					registrationCenterDevice.setIsActive(Boolean.TRUE);
					updateRegDeviceAndCreateInHistory(registrationCenterDevice);
				}

			} else {

				if (getRegistrationDeviceMapping(deviceId) != null) {
					throw new MasterDataServiceException(
							RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_ALREADY_MAPPED.getErrorCode(),
							RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_ALREADY_MAPPED.getErrorMessage());
				}
				createRegDeviceAndHistoryInRegistrationCenterMachine(device, registrationCenters.get(0));
			}

		} else {
			throw new MasterDataServiceException(
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_NOT_IN_SAME_HIERARCHY.getErrorCode(),
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_NOT_IN_SAME_HIERARCHY
							.getErrorMessage());
		}

		ResponseDto responseDto = new ResponseDto();
		responseDto.setMessage("DeviceId " + deviceId + " has been mapped to registration center " + regCenterId);
		return responseDto;
	}

	/**
	 * @param regCenterId
	 * @param deviceId
	 * @return {@link RegistrationCenterDevice}
	 */
	private RegistrationCenterDevice getRegistrationDeviceMapping(String regcenterId, String deviceId) {
		try {
			return registrationCenterDeviceRepository.findByRegIdAndDeviceIdAndLangCode(regcenterId, deviceId,
					primaryLang);
		} catch (DataAccessException | DataAccessLayerException ex) {
			throw new MasterDataServiceException(
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_FETCH_EXCEPTION.getErrorMessage());
		}
	}

	/**
	 * 
	 * @param deviceId
	 * @return {@link RegistrationCenterDevice}
	 */
	private RegistrationCenterDevice getRegistrationDeviceMapping(String deviceId) {
		try {
			return registrationCenterDeviceRepository.findByDeviceIdAndLangCode(deviceId, primaryLang);
		} catch (DataAccessException | DataAccessLayerException ex) {
			throw new MasterDataServiceException(
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_FETCH_EXCEPTION.getErrorMessage());
		}
	}

	private void createRegDeviceAndHistoryInRegistrationCenterMachine(Device device,
			RegistrationCenter registrationCenter) {

		RegistrationCenterDevice registrationCenterDevice = new RegistrationCenterDevice();
		RegistrationCenterDeviceID registrationCenterDeviceID = new RegistrationCenterDeviceID();
		registrationCenterDeviceID.setDeviceId(device.getId());
		registrationCenterDeviceID.setRegCenterId(registrationCenter.getId());
		registrationCenterDevice.setLangCode(primaryLang);
		registrationCenterDevice.setRegistrationCenterDevicePk(registrationCenterDeviceID);
		registrationCenterDevice.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
		registrationCenterDevice.setIsActive(Boolean.TRUE);
		registrationCenterDevice.setCreatedDateTime(DateUtils.getUTCCurrentDateTime());
		RegistrationCenterDeviceHistory registrationCenterDeviceHistory = MapperUtils.map(registrationCenterDevice,
				RegistrationCenterDeviceHistory.class);
		registrationCenterDeviceHistory.setRegistrationCenterDeviceHistoryPk(MapperUtils.map(
				registrationCenterDevice.getRegistrationCenterDevicePk(), RegistrationCenterDeviceHistoryPk.class));
		registrationCenterDeviceHistory.getRegistrationCenterDeviceHistoryPk()
				.setEffectivetimes(DateUtils.getUTCCurrentDateTime());

		try {
			registrationCenterDeviceRepository.create(registrationCenterDevice);
			registrationCenterDeviceHistoryRepository.create(registrationCenterDeviceHistory);
		} catch (DataAccessException | DataAccessLayerException ex) {
			throw new MasterDataServiceException(
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_CREATE_EXCEPTION.getErrorCode(),
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_CREATE_EXCEPTION.getErrorMessage());
		}
	}

	private void updateRegDeviceAndCreateInHistory(RegistrationCenterDevice registrationCenterDevice) {
		RegistrationCenterDeviceHistory registrationCenterDeviceHistory = MapperUtils.map(registrationCenterDevice,
				RegistrationCenterDeviceHistory.class);
		registrationCenterDeviceHistory.setRegistrationCenterDeviceHistoryPk(MapperUtils.map(
				registrationCenterDevice.getRegistrationCenterDevicePk(), RegistrationCenterDeviceHistoryPk.class));
		RegistrationCenterDeviceHistory registrationCenterDeviceHistoryObj = MetaDataUtils
				.setCreateMetaData(registrationCenterDeviceHistory, RegistrationCenterDeviceHistory.class);
		registrationCenterDeviceHistoryObj.getRegistrationCenterDeviceHistoryPk()
				.setEffectivetimes(DateUtils.getUTCCurrentDateTime());

		registrationCenterDevice = MetaDataUtils.setUpdateMetaData(registrationCenterDevice,
				new RegistrationCenterDevice(), true);
		try {
			registrationCenterDeviceHistoryRepository.create(registrationCenterDeviceHistoryObj);
			registrationCenterDeviceRepository.update(registrationCenterDevice);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_CREATE_EXCEPTION.getErrorCode(),
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_CREATE_EXCEPTION.getErrorMessage());
		}
	}

	/**
	 * Gets the device based on device id and primary lang code.
	 *
	 * @param deviceId
	 *            the device id
	 * @param langCode
	 *            the lang code
	 * @return {@link Device}
	 */
	private Device getDeviceBasedOnDeviceIdAndPrimaryLangCode(String deviceId, String langCode) {
		try {
			Device device = deviceRepository.findByIdAndLangCode(deviceId, langCode);

			if (device == null) {
				throw new RequestException(DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorCode(),
						MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
			return device;
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceErrorCode.DEVICE_FETCH_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_FETCH_EXCEPTION.getErrorMessage());
		}
	}

	/**
	 * Gets the zone from reg center repo by reg center id.
	 *
	 * @param regCenterId
	 *            the reg center id
	 * @param langCode
	 *            the lang code
	 * @return the zone from reg center repo by reg center id
	 */
	private List<RegistrationCenter> getZoneFromRegCenterRepoByRegCenterId(String regCenterId, String langCode) {
		try {
			List<RegistrationCenter> registrationCenters = registrationCenterRepository
					.findByRegIdAndLangCode(regCenterId, langCode);
			if (registrationCenters.isEmpty()) {
				throw new RequestException(
						RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_DATA_NOT_FOUND.getErrorCode(),
						RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_DATA_NOT_FOUND
								.getErrorMessage());
			}
			
			return registrationCenters;
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

	}

}
