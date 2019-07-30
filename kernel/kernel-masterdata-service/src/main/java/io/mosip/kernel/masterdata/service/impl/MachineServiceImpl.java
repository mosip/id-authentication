package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.MachineErrorCode;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.dto.MachineRegistrationCenterDto;
import io.mosip.kernel.masterdata.dto.MachineTypeDto;
import io.mosip.kernel.masterdata.dto.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.MachineResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.dto.response.ColumnValue;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.MachineSearchDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.entity.MachineHistory;
import io.mosip.kernel.masterdata.entity.MachineSpecification;
import io.mosip.kernel.masterdata.entity.MachineType;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachine;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachineDevice;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachine;
import io.mosip.kernel.masterdata.entity.Zone;
import io.mosip.kernel.masterdata.entity.id.IdAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.MachineRepository;
import io.mosip.kernel.masterdata.repository.MachineSpecificationRepository;
import io.mosip.kernel.masterdata.repository.MachineTypeRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineDeviceRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineUserRepository;
import io.mosip.kernel.masterdata.service.MachineHistoryService;
import io.mosip.kernel.masterdata.service.MachineService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MasterDataFilterHelper;
import io.mosip.kernel.masterdata.utils.MasterdataSearchHelper;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.OptionalFilter;
import io.mosip.kernel.masterdata.utils.PageUtils;
import io.mosip.kernel.masterdata.utils.ZoneUtils;
import io.mosip.kernel.masterdata.validator.FilterColumnValidator;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;
import io.mosip.kernel.masterdata.validator.FilterTypeValidator;

/**
 * This class have methods to fetch a Machine Details
 * 
 * @author Megha Tanga
 * @author Ritesh Sinha
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Service
public class MachineServiceImpl implements MachineService {

	/**
	 * Field to hold Machine Repository object
	 */
	@Autowired
	MachineRepository machineRepository;

	@Autowired
	MachineSpecificationRepository machineSpecificationRepository;

	@Autowired
	MachineHistoryService machineHistoryService;

	@Autowired
	MachineTypeRepository machineTypeRepository;

	@Autowired
	RegistrationCenterMachineRepository registrationCenterMachineRepository;

	@Autowired
	RegistrationCenterMachineUserRepository registrationCenterMachineUserRepository;

	@Autowired
	RegistrationCenterMachineDeviceRepository registrationCenterMachineDeviceRepository;

	@Autowired
	private MasterdataSearchHelper masterdataSearchHelper;

	@Autowired
	private FilterTypeValidator filterValidator;

	@Autowired
	private MasterDataFilterHelper masterDataFilterHelper;

	@Autowired
	private FilterColumnValidator filterColumnValidator;

	@Autowired
	private ZoneUtils zoneUtils;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.MachineService#getMachine(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public MachineResponseDto getMachine(String id, String langCode) {
		List<Machine> machineList = null;
		List<MachineDto> machineDtoList = null;
		MachineResponseDto machineResponseIdDto = new MachineResponseDto();
		try {
			machineList = machineRepository.findAllByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(id, langCode);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(MachineErrorCode.MACHINE_FETCH_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_FETCH_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}
		if (machineList != null && !machineList.isEmpty()) {
			machineDtoList = MapperUtils.mapAll(machineList, MachineDto.class);
		} else {

			throw new DataNotFoundException(MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorMessage());

		}
		machineResponseIdDto.setMachines(machineDtoList);
		return machineResponseIdDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.MachineService#getMachineAll()
	 */
	@Override
	public MachineResponseDto getMachineAll() {
		List<Machine> machineList = null;

		List<MachineDto> machineDtoList = null;
		MachineResponseDto machineResponseDto = new MachineResponseDto();
		try {
			machineList = machineRepository.findAllByIsDeletedFalseOrIsDeletedIsNull();

		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(MachineErrorCode.MACHINE_FETCH_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_FETCH_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}
		if (machineList != null && !machineList.isEmpty()) {
			machineDtoList = MapperUtils.mapAll(machineList, MachineDto.class);

		} else {
			throw new DataNotFoundException(MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		machineResponseDto.setMachines(machineDtoList);
		return machineResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.MachineService#getMachine(java.lang.
	 * String)
	 */
	@Override
	public MachineResponseDto getMachine(String langCode) {
		MachineResponseDto machineResponseDto = new MachineResponseDto();
		List<Machine> machineList = null;
		List<MachineDto> machineDtoList = null;
		try {
			machineList = machineRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(langCode);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(MachineErrorCode.MACHINE_FETCH_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_FETCH_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}
		if (machineList != null && !machineList.isEmpty()) {
			machineDtoList = MapperUtils.mapAll(machineList, MachineDto.class);

		} else {
			throw new DataNotFoundException(MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		machineResponseDto.setMachines(machineDtoList);
		return machineResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.MachineService#createMachine(io.mosip.
	 * kernel.masterdata.dto.RequestDto)
	 */
	@Override
	@Transactional
	public IdAndLanguageCodeID createMachine(MachineDto machine) {
		Machine crtMachine = null;
		Machine entity = MetaDataUtils.setCreateMetaData(machine, Machine.class);
		MachineHistory entityHistory = MetaDataUtils.setCreateMetaData(machine, MachineHistory.class);
		entityHistory.setEffectDateTime(entity.getCreatedDateTime());
		entityHistory.setCreatedDateTime(entity.getCreatedDateTime());
		try {
			crtMachine = machineRepository.create(entity);
			machineHistoryService.createMachineHistory(entityHistory);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(MachineErrorCode.MACHINE_INSERT_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_INSERT_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}

		IdAndLanguageCodeID idAndLanguageCodeID = new IdAndLanguageCodeID();
		MapperUtils.map(crtMachine, idAndLanguageCodeID);

		return idAndLanguageCodeID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.MachineService#updateMachine(io.mosip.
	 * kernel.masterdata.dto.RequestDto)
	 */
	@Override
	@Transactional
	public IdAndLanguageCodeID updateMachine(MachineDto machine) {
		Machine updMachine = null;
		try {
			Machine renmachine = machineRepository
					.findMachineByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNullWithoutActiveStatusCheck(
							machine.getId(), machine.getLangCode());

			if (renmachine != null) {
				MetaDataUtils.setUpdateMetaData(machine, renmachine, false);
				updMachine = machineRepository.update(renmachine);

				MachineHistory machineHistory = new MachineHistory();
				MapperUtils.map(updMachine, machineHistory);
				MapperUtils.setBaseFieldValue(updMachine, machineHistory);
				machineHistory.setEffectDateTime(updMachine.getUpdatedDateTime());
				machineHistory.setUpdatedDateTime(updMachine.getUpdatedDateTime());
				machineHistoryService.createMachineHistory(machineHistory);
			} else {
				throw new RequestException(MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorCode(),
						MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(MachineErrorCode.MACHINE_UPDATE_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_UPDATE_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}

		IdAndLanguageCodeID idAndLanguageCodeID = new IdAndLanguageCodeID();
		MapperUtils.map(updMachine, idAndLanguageCodeID);
		return idAndLanguageCodeID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.MachineService#deleteMachine(java.lang.
	 * String)
	 */
	@Override
	@Transactional
	public IdResponseDto deleteMachine(String id) {
		Machine delMachine = null;
		try {
			List<Machine> renMachineList = machineRepository.findMachineByIdAndIsDeletedFalseorIsDeletedIsNull(id);
			if (!renMachineList.isEmpty()) {
				for (Machine renMachine : renMachineList) {

					List<RegistrationCenterMachine> registrationCenterMachineList = registrationCenterMachineRepository
							.findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(renMachine.getId());
					List<RegistrationCenterUserMachine> registrationCenterMachineUser = registrationCenterMachineUserRepository
							.findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(renMachine.getId());
					List<RegistrationCenterMachineDevice> registrationCenterMachineDevice = registrationCenterMachineDeviceRepository
							.findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(renMachine.getId());

					if (registrationCenterMachineList.isEmpty() && registrationCenterMachineUser.isEmpty()
							&& registrationCenterMachineDevice.isEmpty()) {
						MetaDataUtils.setDeleteMetaData(renMachine);
						delMachine = machineRepository.update(renMachine);

						MachineHistory machineHistory = new MachineHistory();
						MapperUtils.map(delMachine, machineHistory);
						MapperUtils.setBaseFieldValue(delMachine, machineHistory);

						machineHistory.setEffectDateTime(delMachine.getDeletedDateTime());
						machineHistory.setDeletedDateTime(delMachine.getDeletedDateTime());
						machineHistoryService.createMachineHistory(machineHistory);
					} else {
						throw new RequestException(MachineErrorCode.DEPENDENCY_EXCEPTION.getErrorCode(),
								MachineErrorCode.DEPENDENCY_EXCEPTION.getErrorMessage());
					}
				}
			} else {
				throw new RequestException(MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorCode(),
						MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorMessage());
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(MachineErrorCode.MACHINE_DELETE_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_DELETE_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}

		IdResponseDto idResponseDto = new IdResponseDto();
		idResponseDto.setId(id);
		return idResponseDto;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.MachineService#
	 * getRegistrationCenterMachineMapping1(java.lang.String)
	 */
	@Override
	public PageDto<MachineRegistrationCenterDto> getMachinesByRegistrationCenter(String regCenterId, int page, int size,
			String orderBy, String direction) {
		PageDto<MachineRegistrationCenterDto> pageDto = new PageDto<>();
		List<MachineRegistrationCenterDto> machineRegistrationCenterDtoList = null;
		Page<Machine> pageEntity = null;

		try {
			pageEntity = machineRepository.findMachineByRegCenterId(regCenterId,
					PageRequest.of(page, size, Sort.by(Direction.fromString(direction), orderBy)));
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					MachineErrorCode.REGISTRATION_CENTER_MACHINE_FETCH_EXCEPTION.getErrorCode(),
					MachineErrorCode.REGISTRATION_CENTER_MACHINE_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		if (pageEntity != null && !pageEntity.getContent().isEmpty()) {
			machineRegistrationCenterDtoList = MapperUtils.mapAll(pageEntity.getContent(),
					MachineRegistrationCenterDto.class);
			for (MachineRegistrationCenterDto machineRegistrationCenterDto1 : machineRegistrationCenterDtoList) {
				machineRegistrationCenterDto1.setRegCentId(regCenterId);
			}
		} else {
			throw new RequestException(MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorMessage());
		}

		pageDto.setPageNo(pageEntity.getNumber());
		pageDto.setPageSize(pageEntity.getSize());
		pageDto.setSort(pageEntity.getSort());
		pageDto.setTotalItems(pageEntity.getTotalElements());
		pageDto.setTotalPages(pageEntity.getTotalPages());
		pageDto.setData(machineRegistrationCenterDtoList);

		return pageDto;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.MachineService#searchMachine(io.mosip.
	 * kernel.masterdata.dto.request.SearchDto)
	 */
	@Override
	public PageResponseDto<MachineSearchDto> searchMachine(SearchDto dto) {
		PageResponseDto<MachineSearchDto> pageDto = new PageResponseDto<>();
		List<MachineSearchDto> machines = null;
		List<SearchFilter> addList = new ArrayList<>();
		List<SearchFilter> removeList = new ArrayList<>();
		List<String> mappedMachineIdList = null;
		List<SearchFilter> zoneFilter = new ArrayList<>();
		List<Zone> zones = null;
		boolean flag = true;
		for (SearchFilter filter : dto.getFilters()) {
			String column = filter.getColumnName();
			if (MasterDataConstant.ZONE.equalsIgnoreCase(column)) {
				Zone zone = getZone(filter);
				if (zone != null) {
					zones = zoneUtils.getZones(zone);
					zoneFilter.addAll(buildZoneFilter(zones));
				}
				removeList.add(filter);
				flag = false;
			}

			if (column.equalsIgnoreCase("mapStatus")) {

				if (filter.getValue().equalsIgnoreCase("assigned")) {
					mappedMachineIdList = machineRepository.findMappedMachineId();
					addList.addAll(buildRegistrationCenterMachineTypeSearchFilter(mappedMachineIdList));
					if (addList.isEmpty()) {
						throw new DataNotFoundException(
								MachineErrorCode.MAPPED_MACHINE_ID_NOT_FOUND_EXCEPTION.getErrorCode(), String.format(
										MachineErrorCode.MAPPED_MACHINE_ID_NOT_FOUND_EXCEPTION.getErrorMessage()));
					}

				} else {
					if (filter.getValue().equalsIgnoreCase("unassigned")) {
						mappedMachineIdList = machineRepository.findNotMappedMachineId();
						addList.addAll(buildRegistrationCenterMachineTypeSearchFilter(mappedMachineIdList));
						if (addList.isEmpty()) {
							throw new DataNotFoundException(
									MachineErrorCode.MACHINE_ID_ALREADY_MAPPED_EXCEPTION.getErrorCode(), String.format(
											MachineErrorCode.MACHINE_ID_ALREADY_MAPPED_EXCEPTION.getErrorMessage()));
						}
					} else {
						throw new RequestException(
								MachineErrorCode.INVALID_MACHINE_FILTER_VALUE_EXCEPTION.getErrorCode(),
								MachineErrorCode.INVALID_MACHINE_FILTER_VALUE_EXCEPTION.getErrorMessage());
					}

				}
				removeList.add(filter);
			}

			if (column.equalsIgnoreCase("machineTypeName")) {
				filter.setColumnName("name");
				if (filterValidator.validate(MachineTypeDto.class, Arrays.asList(filter))) {

					Page<MachineType> machineTypes = masterdataSearchHelper.searchMasterdata(MachineType.class,
							new SearchDto(Arrays.asList(filter), Collections.emptyList(), new Pagination(), null),
							null);
					List<SearchFilter> machineCodeFilter = buildMachineTypeSearchFilter(machineTypes.getContent());
					if (machineCodeFilter.isEmpty()) {
						throw new DataNotFoundException(
								MachineErrorCode.MACHINE_ID_NOT_FOUND_FOR_NAME_EXCEPTION.getErrorCode(),
								String.format(
										MachineErrorCode.MACHINE_ID_NOT_FOUND_FOR_NAME_EXCEPTION.getErrorMessage(),
										filter.getValue()));
					}
					Page<MachineSpecification> machineSpecification = masterdataSearchHelper.searchMasterdata(
							MachineSpecification.class,
							new SearchDto(machineCodeFilter, Collections.emptyList(), new Pagination(), null), null);

					removeList.add(filter);
					addList.addAll(buildMachineSpecificationSearchFilter(machineSpecification.getContent()));
					if (addList.isEmpty()) {
						throw new DataNotFoundException(
								MachineErrorCode.MACHINE_SPECIFICATION_ID_NOT_FOUND_FOR_NAME_EXCEPTION.getErrorCode(),
								String.format(MachineErrorCode.MACHINE_SPECIFICATION_ID_NOT_FOUND_FOR_NAME_EXCEPTION
										.getErrorMessage(), filter.getValue()));
					}

				}
			}

		}
		if (flag) {
			zones = zoneUtils.getUserZones();
			if (zones != null && !zones.isEmpty())
				zoneFilter.addAll(buildZoneFilter(zones));
			else
				throw new MasterDataServiceException(MachineErrorCode.MACHINE_NOT_TAGGED_TO_ZONE.getErrorCode(),
						MachineErrorCode.MACHINE_NOT_TAGGED_TO_ZONE.getErrorMessage());
		}
		dto.getFilters().removeAll(removeList);

		if (filterValidator.validate(MachineSearchDto.class, dto.getFilters())) {
			OptionalFilter optionalFilter = new OptionalFilter(addList);
			OptionalFilter zoneOptionalFilter = new OptionalFilter(zoneFilter);
			Page<Machine> page = masterdataSearchHelper.searchMasterdata(Machine.class, dto,
					new OptionalFilter[] { optionalFilter, zoneOptionalFilter });
			if (page.getContent() != null && !page.getContent().isEmpty()) {
				pageDto = PageUtils.pageResponse(page);
				machines = MapperUtils.mapAll(page.getContent(), MachineSearchDto.class);
				setMachineMetadata(machines, zones);
				pageDto.setData(machines);
			}

		}
		return pageDto;
	}

	/**
	 * Method to set each machine zone meta data.
	 * 
	 * @param list
	 *            list of {@link MachineSearchDto}.
	 * @param zones
	 *            the list of zones.
	 */
	public void setMachineMetadata(List<MachineSearchDto> list, List<Zone> zones) {
		list.forEach(i -> setZoneMetadata(i, zones));
	}

	/**
	 * Method to set Zone metadata
	 * 
	 * @param machines
	 *            metadata to be added
	 * @param zones
	 *            list of zones
	 * 
	 */
	private void setZoneMetadata(MachineSearchDto machines, List<Zone> zones) {
		Optional<Zone> zone = zones.stream().filter(
				i -> i.getCode().equals(machines.getZoneCode()) && i.getLangCode().equals(machines.getLangCode()))
				.findFirst();
		if (zone.isPresent()) {
			machines.setZone(zone.get().getName());
		}
	}

	/**
	 * Search the zone in the based on the received input filter
	 * 
	 * @param filter
	 *            search input
	 * @return {@link Zone} if successful otherwise throws
	 *         {@link MasterDataServiceException}
	 */
	public Zone getZone(SearchFilter filter) {
		filter.setColumnName(MasterDataConstant.NAME);
		Page<Zone> zones = masterdataSearchHelper.searchMasterdata(Zone.class,
				new SearchDto(Arrays.asList(filter), Collections.emptyList(), new Pagination(), null), null);
		if (zones.hasContent()) {
			return zones.getContent().get(0);
		} else {
			throw new MasterDataServiceException(MachineErrorCode.ZONE_NOT_EXIST.getErrorCode(),
					String.format(MachineErrorCode.ZONE_NOT_EXIST.getErrorMessage(), filter.getValue()));
		}
	}

	/**
	 * Creating Search filter from the passed zones
	 * 
	 * @param zones
	 *            filter to be created with the zones
	 * @return list of {@link SearchFilter}
	 */
	public List<SearchFilter> buildZoneFilter(List<Zone> zones) {
		if (zones != null && !zones.isEmpty()) {
			return zones.stream().filter(Objects::nonNull).map(Zone::getCode).distinct().map(this::buildZoneFilter)
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	/**
	 * Method to create SearchFilter for the recieved zoneCode
	 * 
	 * @param zoneCode
	 *            input from the {@link SearchFilter} has to be created
	 * @return {@link SearchFilter}
	 */
	private SearchFilter buildZoneFilter(String zoneCode) {
		SearchFilter filter = new SearchFilter();
		filter.setColumnName(MasterDataConstant.ZONE_CODE);
		filter.setType(FilterTypeEnum.EQUALS.name());
		filter.setValue(zoneCode);
		return filter;
	}

	/**
	 * This method return Machine Id list filters.
	 * 
	 * @param machineIdList
	 *            the Machine Id list.
	 * @return the list of {@link SearchFilter}.
	 */
	private List<SearchFilter> buildRegistrationCenterMachineTypeSearchFilter(List<String> machineIdList) {
		if (machineIdList != null && !machineIdList.isEmpty())
			return machineIdList.stream().filter(Objects::nonNull).map(this::buildRegistrationCenterMachineType)
					.collect(Collectors.toList());
		return Collections.emptyList();
	}

	/**
	 * This method return Machine Types list filters.
	 * 
	 * @param machineTypes
	 *            the list of Machine Type.
	 * @return the list of {@link SearchFilter}.
	 */
	private List<SearchFilter> buildMachineTypeSearchFilter(List<MachineType> machineTypes) {
		if (machineTypes != null && !machineTypes.isEmpty())
			return machineTypes.stream().filter(Objects::nonNull).map(this::buildMachineType)
					.collect(Collectors.toList());
		return Collections.emptyList();
	}

	/**
	 * This method return Machine Specification list filters.
	 * 
	 * @param machineSpecification
	 *            the list of Machine Specification.
	 * @return the list of {@link SearchFilter}.
	 */
	private List<SearchFilter> buildMachineSpecificationSearchFilter(List<MachineSpecification> machineSpecification) {
		if (machineSpecification != null && !machineSpecification.isEmpty())
			return machineSpecification.stream().filter(Objects::nonNull).map(this::buildMachineSpecification)
					.collect(Collectors.toList());
		return Collections.emptyList();
	}

	/**
	 * This method provide search filter for provided machine id.
	 * 
	 * @param machineId
	 *            the machine id.
	 * @return the {@link SearchFilter}.
	 */
	private SearchFilter buildRegistrationCenterMachineType(String machineId) {
		SearchFilter filter = new SearchFilter();
		filter.setColumnName("id");
		filter.setType(FilterTypeEnum.EQUALS.name());
		filter.setValue(machineId);
		return filter;
	}

	/**
	 * This method provide search filter for provided Machine specification.
	 * 
	 * @param machineSpecification
	 *            the machine specification.
	 * @return the {@link SearchFilter}.
	 */
	private SearchFilter buildMachineSpecification(MachineSpecification machineSpecification) {
		SearchFilter filter = new SearchFilter();
		filter.setColumnName("machineSpecId");
		filter.setType(FilterTypeEnum.EQUALS.name());
		filter.setValue(machineSpecification.getId());
		return filter;
	}

	/**
	 * This method provide search filter for provided Machine Type.
	 * 
	 * @param machineType
	 *            the machine type.
	 * @return the {@link SearchFilter}.
	 */
	private SearchFilter buildMachineType(MachineType machineType) {
		SearchFilter filter = new SearchFilter();
		filter.setColumnName("machineTypeCode");
		filter.setType(FilterTypeEnum.EQUALS.name());
		filter.setValue(machineType.getCode());
		return filter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.MachineService#machineFilterValues(io.
	 * mosip.kernel.masterdata.dto.request.FilterValueDto)
	 */
	@Override
	public FilterResponseDto machineFilterValues(FilterValueDto filterValueDto) {
		FilterResponseDto filterResponseDto = new FilterResponseDto();
		List<ColumnValue> columnValueList = new ArrayList<>();
		if (filterColumnValidator.validate(FilterDto.class, filterValueDto.getFilters(), Machine.class)) {
			for (FilterDto filterDto : filterValueDto.getFilters()) {
				List<?> filterValues = masterDataFilterHelper.filterValues(Machine.class, filterDto, filterValueDto);
				filterValues.forEach(filterValue -> {
					ColumnValue columnValue = new ColumnValue();
					columnValue.setFieldID(filterDto.getColumnName());
					columnValue.setFieldValue(filterValue.toString());
					columnValueList.add(columnValue);
				});
			}
			filterResponseDto.setFilters(columnValueList);
		}
		return filterResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.MachineService#decommissionMachine(java.
	 * lang.String)
	 */
	@Override
	@Transactional
	public IdResponseDto decommissionMachine(String machineId) {
		IdResponseDto machineCodeId = new IdResponseDto();
		MapperUtils.mapFieldValues(machineId, machineCodeId);
		try {
			int updatedRows = machineRepository.decommissionMachine(machineId);
			if (updatedRows < 1) {
				throw new RequestException(MachineErrorCode.MAPPED_MACHINE_ID_NOT_FOUND_EXCEPTION.getErrorCode(),
						MachineErrorCode.MAPPED_MACHINE_ID_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(MachineErrorCode.MACHINE_DECOMMISSION_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_DECOMMISSION_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		machineCodeId.setId(machineId);
		return machineCodeId;
	}
}
