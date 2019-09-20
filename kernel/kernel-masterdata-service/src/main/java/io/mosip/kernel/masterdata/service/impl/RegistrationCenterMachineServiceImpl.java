package io.mosip.kernel.masterdata.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.masterdata.constant.MachineErrorCode;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.constant.RegistrationCenterDeviceErrorCode;
import io.mosip.kernel.masterdata.constant.RegistrationCenterMachineErrorCode;
import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDto;
import io.mosip.kernel.masterdata.dto.ResponseRrgistrationCenterMachineDto;
import io.mosip.kernel.masterdata.dto.getresponse.ResponseDto;
import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDevice;
import io.mosip.kernel.masterdata.entity.RegistrationCenterHistory;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachine;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachineHistory;
import io.mosip.kernel.masterdata.entity.Zone;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineHistoryID;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.MachineRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterHistoryRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineHistoryRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.ZoneUtils;

/**
 * Service Implementation for {@link RegistrationCenterMachineService}
 * 
 * @author Dharmesh Khandelwal
 * @author Bal Vikash Sharma
 * @author Srinivasan
 * @since 1.0.0
 */
@Service
public class RegistrationCenterMachineServiceImpl implements RegistrationCenterMachineService {

	/**
	 * {@link RegistrationCenterMachineRepository} instance
	 */
	@Autowired
	private RegistrationCenterMachineRepository registrationCenterMachineRepository;
	/**
	 * {@link RegistrationCenterMachineHistoryRepository} instance
	 */
	@Autowired
	private RegistrationCenterMachineHistoryRepository registrationCenterMachineHistoryRepository;

	@Autowired
	private ZoneUtils zoneUtil;

	@Value("${mosip.primary-language}")
	private String primaryLang;

	@Autowired
	private MachineRepository machineRepository;

	@Autowired
	private RegistrationCenterHistoryRepository registrationCenterHistoryRepository;

	@Autowired
	private RegistrationCenterRepository regRepo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.RegistrationCenterMachineService#
	 * createRegistrationCenterAndMachine(io.mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	@Transactional
	public ResponseRrgistrationCenterMachineDto createRegistrationCenterAndMachine(
			RegistrationCenterMachineDto requestDto) {
		ResponseRrgistrationCenterMachineDto responseRrgistrationCenterMachineDto = null;

		try {
			RegistrationCenterMachine registrationCenterMachine = MetaDataUtils.setCreateMetaData(requestDto,
					RegistrationCenterMachine.class);
			RegistrationCenterMachine savedRegistrationCenterMachine = registrationCenterMachineRepository
					.create(registrationCenterMachine);

			RegistrationCenterMachineHistory registrationCenterMachineHistory = MetaDataUtils
					.setCreateMetaData(requestDto, RegistrationCenterMachineHistory.class);
			registrationCenterMachineHistory.getRegistrationCenterMachineHistoryPk()
					.setEffectivetimes(savedRegistrationCenterMachine.getCreatedDateTime());
			registrationCenterMachineHistoryRepository.create(registrationCenterMachineHistory);

			responseRrgistrationCenterMachineDto = MapperUtils.map(
					savedRegistrationCenterMachine.getRegistrationCenterMachinePk(),
					ResponseRrgistrationCenterMachineDto.class);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_CREATE_EXCEPTION.getErrorCode(),
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_CREATE_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

		return responseRrgistrationCenterMachineDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.RegistrationCenterMachineService#
	 * deleteRegistrationCenterMachineMapping(java.lang.String, java.lang.String)
	 */
	@Transactional
	@Override
	public RegistrationCenterMachineID deleteRegistrationCenterMachineMapping(String regCenterId, String machineId) {
		RegistrationCenterMachineID registrationCenterMachineID = null;
		try {
			registrationCenterMachineID = new RegistrationCenterMachineID(regCenterId, machineId);
			Optional<RegistrationCenterMachine> registrationCenterMachine = registrationCenterMachineRepository
					.findAllNondeletedMappings(registrationCenterMachineID);
			if (!registrationCenterMachine.isPresent()) {
				throw new RequestException(
						RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_DATA_NOT_FOUND.getErrorCode(),
						RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_DATA_NOT_FOUND
								.getErrorMessage());
			} else {
				RegistrationCenterMachine centerMachine = registrationCenterMachine.get();
				centerMachine = MetaDataUtils.setDeleteMetaData(centerMachine);
				RegistrationCenterMachineHistory history = MapperUtils.map(centerMachine,
						RegistrationCenterMachineHistory.class);
				history.setRegistrationCenterMachineHistoryPk(
						MapperUtils.map(registrationCenterMachineID, RegistrationCenterMachineHistoryID.class));
				history.getRegistrationCenterMachineHistoryPk().setEffectivetimes(centerMachine.getDeletedDateTime());
				MapperUtils.setBaseFieldValue(centerMachine, history);
				registrationCenterMachineHistoryRepository.create(history);
				registrationCenterMachineRepository.update(centerMachine);
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_DELETE_EXCEPTION.getErrorCode(),
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_DELETE_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		return registrationCenterMachineID;
	}

	@Override
	@Transactional
	public ResponseDto unMapCenterToMachineMapping(String regCenterId, String machineId) {

		List<Zone> userZones = zoneUtil.getUserZones();
		boolean isMachineMappedToUserZone = false;
		boolean isRegCenterMappedToUserZone = false;
		boolean isInSameHierarchy = false;
		Zone registrationCenterZone = null;
		userZones = userZones.stream().filter(zone -> zone.getLangCode().equals(primaryLang))
				.collect(Collectors.toList());
		Machine machine = getZoneFromMachineRepoByMachineId(machineId, primaryLang);
		List<RegistrationCenter> registrationCenters = getZoneFromRegCenterRepoByRegCenterId(regCenterId, primaryLang);
		for (Zone zone : userZones) {
			if (zone.getCode().equals(machine.getZoneCode())) {
				isMachineMappedToUserZone = true;
			}

			if (zone.getCode().equals(registrationCenters.get(0).getZoneCode())) {
				isRegCenterMappedToUserZone = true;
				registrationCenterZone = zone;
			}
		}

		if (!isMachineMappedToUserZone || !isRegCenterMappedToUserZone) {
			throw new RequestException(
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_ZONE_INVALID.getErrorCode(),
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_ZONE_INVALID.getErrorMessage());
		}
		Objects.requireNonNull(registrationCenterZone, "registrationCenterZone is empty");
		String hierarchyPath = registrationCenterZone.getHierarchyPath();
		List<String> zoneHierarchy = Arrays.asList(hierarchyPath.split("/"));
		isInSameHierarchy = zoneHierarchy.stream().anyMatch(zone -> zone.equals(machine.getZoneCode()));
		if (!isInSameHierarchy) {
			throw new MasterDataServiceException(
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_NOT_IN_SAME_HIERARCHY.getErrorCode(),
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_NOT_IN_SAME_HIERARCHY
							.getErrorMessage());
		}
		RegistrationCenterMachine registrationCenterMachine = registrationCenterMachineRepository
				.findByRegIdAndMachineId(regCenterId, machineId, primaryLang);
		if (registrationCenterMachine == null) {
			throw new MasterDataServiceException(
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_DATA_NOT_FOUND.getErrorCode(),
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_DATA_NOT_FOUND.getErrorMessage());

		}
		if (registrationCenterMachine.getIsActive()) {
			registrationCenterMachine.setIsActive(false);
			updateAndCreateHistoryInRegistrationCenterMachine(registrationCenterMachine, registrationCenters);
		} else {
			throw new MasterDataServiceException(
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_STATUS.getErrorCode(),
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_STATUS.getErrorMessage());
		}

		ResponseDto statusResponseDto = new ResponseDto();
		statusResponseDto.setStatus(MasterDataConstant.SUCCESS);
		statusResponseDto.setMessage(machineId + "is successfully unmapped to the Registration Center" + regCenterId);

		return statusResponseDto;
	}

	/**
	 * @param registrationCenterMachine
	 */
	private void updateAndCreateHistoryInRegistrationCenterMachine(RegistrationCenterMachine registrationCenterMachine,
			List<RegistrationCenter> registration) {
		RegistrationCenter registrationCenter = registration.get(0);
		int decreasedKioskTime = registrationCenter.getNumberOfKiosks() - 1;
		registrationCenter.setNumberOfKiosks((short) decreasedKioskTime);
		MetaDataUtils.setUpdateMetaData(new RegistrationCenterHistory(), registrationCenter, false);

		RegistrationCenterMachineHistory registrationCenterMachineHistory = MetaDataUtils
				.setCreateMetaData(registrationCenterMachine, RegistrationCenterMachineHistory.class);
		registrationCenterMachineHistory.setRegistrationCenterMachineHistoryPk(MapperUtils.map(
				registrationCenterMachine.getRegistrationCenterMachinePk(), RegistrationCenterMachineHistoryID.class));
		registrationCenterMachineHistory.getRegistrationCenterMachineHistoryPk()
				.setEffectivetimes(DateUtils.getUTCCurrentDateTime());

		MapperUtils.setBaseFieldValue(registrationCenterMachine, registrationCenterMachineHistory);
		registrationCenterMachine.setUpdatedDateTime(DateUtils.getUTCCurrentDateTime());
		registrationCenterMachine.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

		RegistrationCenterHistory registrationCenterHistory = MapperUtils.map(registrationCenter,
				RegistrationCenterHistory.class);

		MetaDataUtils.setCreateMetaData(registrationCenterHistory, RegistrationCenterHistory.class);
		registrationCenterHistory.setEffectivetimes(DateUtils.getUTCCurrentDateTime());

		try {
			// Decrease no of kiosk
			regRepo.update(registrationCenter);

			registrationCenterHistoryRepository.create(registrationCenterHistory);
			// Update registration center machine with active false
			registrationCenterMachineRepository.update(registrationCenterMachine);
			// Audit the changes in history table
			registrationCenterMachineHistoryRepository.create(registrationCenterMachineHistory);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
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
			List<RegistrationCenter> registrationCenters = regRepo.findByRegIdAndLangCode(regCenterId, langCode);
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

	private Machine getZoneFromMachineRepoByMachineId(String machineId, String langCode) {
		try {
			Machine machine = machineRepository.findMachineByIdAndLangCode(machineId, langCode);
			if (machine == null) {
				throw new RequestException(MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorCode(),
						MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorMessage());

			}
			return machine;
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

	}

	@Override
	public ResponseDto mapCenterToMachineMapping(String regCenterId, String machineId) {
		List<Zone> userZones = zoneUtil.getUserZones();
		boolean isMachineMappedToUserZone = false;
		boolean isRegCenterMappedToUserZone = false;
		boolean isInSameHierarchy = false;
		Zone registrationCenterZone = null;
		userZones = userZones.stream().filter(zone -> zone.getLangCode().equals(primaryLang))
				.collect(Collectors.toList());
		Machine machine = getZoneFromMachineRepoByMachineId(machineId, primaryLang);
		List<RegistrationCenter> registrationCenters = getZoneFromRegCenterRepoByRegCenterId(regCenterId, primaryLang);
		for (Zone zone : userZones) {
			if (zone.getCode().equals(machine.getZoneCode())) {
				isMachineMappedToUserZone = true;
			}

			if (zone.getCode().equals(registrationCenters.get(0).getZoneCode())) {
				isRegCenterMappedToUserZone = true;
				registrationCenterZone = zone;
			}
		}

		if (!isMachineMappedToUserZone || !isRegCenterMappedToUserZone) {
			throw new RequestException(
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_ZONE_INVALID.getErrorCode(),
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_ZONE_INVALID.getErrorMessage());
		}
		Objects.requireNonNull(registrationCenterZone, "registrationCenterZone is empty");
		String hierarchyPath = registrationCenterZone.getHierarchyPath();
		List<String> zoneHierarchy = Arrays.asList(hierarchyPath.split("/"));
		isInSameHierarchy = zoneHierarchy.stream().anyMatch(zone -> zone.equals(machine.getZoneCode()));
		if (!isInSameHierarchy) {
			throw new MasterDataServiceException(
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_NOT_IN_SAME_HIERARCHY.getErrorCode(),
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_NOT_IN_SAME_HIERARCHY
							.getErrorMessage());
		}
		if ((!machine.getIsActive() && machine.getIsDeleted())
				|| (!registrationCenters.get(0).getIsActive() && registrationCenters.get(0).getIsDeleted())) {
			throw new MasterDataServiceException(
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_DECOMMISIONED_STATE.getErrorCode(),
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_DECOMMISIONED_STATE
							.getErrorMessage());
		}

		RegistrationCenterMachine registrationCenterMachine = registrationCenterMachineRepository
				.findByRegIdAndMachineId(regCenterId, machineId, primaryLang);
		if (registrationCenterMachine != null && registrationCenterMachine.getIsActive()) {
			throw new MasterDataServiceException(
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_ALREADY_ACTIVE.getErrorCode(),
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_ALREADY_ACTIVE.getErrorMessage());
		} else if (registrationCenterMachine != null && !registrationCenterMachine.getIsActive()) {
			registrationCenterMachine.setIsActive(true);
			updateRegMachineAndCreateInHistory(registrationCenterMachine, registrationCenters.get(0));
		} else {
			if (getRegistrationDeviceMapping(machineId) != null) {
				throw new MasterDataServiceException(
						RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_ALREADY_ACTIVE.getErrorCode(),
						RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_ALREADY_ACTIVE
								.getErrorMessage());
			}
			createRegMachineAndHistoryInRegistrationCenterMachine(machine, registrationCenters.get(0));
		}

		ResponseDto responseDto = new ResponseDto();
		responseDto.setStatus(MasterDataConstant.SUCCESS);
		responseDto.setMessage("MachineId " + machineId + " has been mapped to registration center " + regCenterId);
		return responseDto;
	}

	private void updateRegMachineAndCreateInHistory(RegistrationCenterMachine registrationCenterMachine,
			RegistrationCenter registrationCenter) {
		int increaseKioskTime = registrationCenter.getNumberOfKiosks() + 1;
		registrationCenter.setNumberOfKiosks((short) increaseKioskTime);
		MetaDataUtils.setUpdateMetaData(new RegistrationCenterHistory(), registrationCenter, false);
		RegistrationCenterMachineHistory registrationCenterMachineHistory = MetaDataUtils
				.setCreateMetaData(registrationCenterMachine, RegistrationCenterMachineHistory.class);
		registrationCenterMachineHistory.setRegistrationCenterMachineHistoryPk(MapperUtils.map(
				registrationCenterMachine.getRegistrationCenterMachinePk(), RegistrationCenterMachineHistoryID.class));
		registrationCenterMachineHistory.getRegistrationCenterMachineHistoryPk()
				.setEffectivetimes(DateUtils.getUTCCurrentDateTime());
		MapperUtils.setBaseFieldValue(registrationCenterMachine, registrationCenterMachineHistory);
		registrationCenterMachine.setUpdatedDateTime(DateUtils.getUTCCurrentDateTime());
		registrationCenterMachine.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
		RegistrationCenterHistory registrationCenterHistory = MapperUtils.map(registrationCenter,
				RegistrationCenterHistory.class);
		MetaDataUtils.setCreateMetaData(registrationCenterHistory, RegistrationCenterHistory.class);
		registrationCenterHistory.setEffectivetimes(DateUtils.getUTCCurrentDateTime());
		try {
			// Increase no of kiosk
			regRepo.update(registrationCenter);
			registrationCenterHistoryRepository.create(registrationCenterHistory);
			// Update registration center machine with active false
			registrationCenterMachineRepository.update(registrationCenterMachine);
			// Audit the changes in history table
			registrationCenterMachineHistoryRepository.create(registrationCenterMachineHistory);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

	}

	/**
	 * @param registrationCenterMachine
	 */
	private void createRegMachineAndHistoryInRegistrationCenterMachine(Machine machine,
			RegistrationCenter registrationCenter) {
		RegistrationCenterMachine registrationCenterMachine = new RegistrationCenterMachine();
		RegistrationCenterMachineID registrationCenterMachineID = new RegistrationCenterMachineID();
		registrationCenterMachineID.setMachineId(machine.getId());
		registrationCenterMachineID.setRegCenterId(registrationCenter.getId());
		registrationCenterMachine.setRegistrationCenterMachinePk(registrationCenterMachineID);
		registrationCenterMachine.setLangCode(primaryLang);
		registrationCenterMachine.setIsActive(Boolean.TRUE);
		registrationCenterMachine.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
		registrationCenterMachine.setCreatedDateTime(DateUtils.getUTCCurrentDateTime());

		int increaseKioskTime = registrationCenter.getNumberOfKiosks() + 1;
		registrationCenter.setNumberOfKiosks((short) increaseKioskTime);
		MetaDataUtils.setUpdateMetaData(new RegistrationCenterHistory(), registrationCenter, false);
		RegistrationCenterMachineHistory registrationCenterMachineHistory = MetaDataUtils
				.setCreateMetaData(registrationCenterMachine, RegistrationCenterMachineHistory.class);
		registrationCenterMachineHistory.setRegistrationCenterMachineHistoryPk(MapperUtils.map(
				registrationCenterMachine.getRegistrationCenterMachinePk(), RegistrationCenterMachineHistoryID.class));
		registrationCenterMachineHistory.getRegistrationCenterMachineHistoryPk()
				.setEffectivetimes(DateUtils.getUTCCurrentDateTime());
		MapperUtils.setBaseFieldValue(registrationCenterMachine, registrationCenterMachineHistory);
		RegistrationCenterHistory registrationCenterHistory = MapperUtils.map(registrationCenter,
				RegistrationCenterHistory.class);
		MetaDataUtils.setCreateMetaData(registrationCenterHistory, RegistrationCenterHistory.class);
		registrationCenterHistory.setEffectivetimes(DateUtils.getUTCCurrentDateTime());

		try {
			// Increase no of kiosk
			regRepo.update(registrationCenter);
			registrationCenterHistoryRepository.create(registrationCenterHistory);
			// Update registration center machine with active false
			registrationCenterMachineRepository.create(registrationCenterMachine);
			// Audit the changes in history table
			registrationCenterMachineHistoryRepository.create(registrationCenterMachineHistory);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

	}

	/**
	 * 
	 * @param machineId
	 * @return {@link RegistrationCenterDevice}
	 */
	private RegistrationCenterMachine getRegistrationDeviceMapping(String machineId) {
		try {
			return registrationCenterMachineRepository.findByMachineId(machineId, primaryLang);
		} catch (DataAccessException | DataAccessLayerException ex) {
			throw new MasterDataServiceException(
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_FETCH_EXCEPTION.getErrorMessage());
		}
	}

}
