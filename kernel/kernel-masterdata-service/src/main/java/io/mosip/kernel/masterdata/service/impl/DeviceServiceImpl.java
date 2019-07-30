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
import io.mosip.kernel.masterdata.constant.DeviceErrorCode;
import io.mosip.kernel.masterdata.constant.MachineErrorCode;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.dto.DeviceDto;
import io.mosip.kernel.masterdata.dto.DeviceLangCodeDtypeDto;
import io.mosip.kernel.masterdata.dto.DeviceRegistrationCenterDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeDto;
import io.mosip.kernel.masterdata.dto.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceLangCodeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.dto.response.ColumnValue;
import io.mosip.kernel.masterdata.dto.response.DeviceSearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.Device;
import io.mosip.kernel.masterdata.entity.DeviceHistory;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDevice;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachineDevice;
import io.mosip.kernel.masterdata.entity.Zone;
import io.mosip.kernel.masterdata.entity.id.IdAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.DeviceRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterDeviceRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineDeviceRepository;
import io.mosip.kernel.masterdata.service.DeviceHistoryService;
import io.mosip.kernel.masterdata.service.DeviceService;
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
 * This class have methods to fetch and save Device Details
 * 
 * @author Megha Tanga
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Service
public class DeviceServiceImpl implements DeviceService {

	/**
	 * Field to hold Device Repository object
	 */
	@Autowired
	DeviceRepository deviceRepository;
	/**
	 * Field to hold Device Service object
	 */
	@Autowired
	DeviceHistoryService deviceHistoryService;

	@Autowired
	RegistrationCenterDeviceRepository registrationCenterDeviceRepository;

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
	 * @see
	 * io.mosip.kernel.masterdata.service.DeviceService#getDeviceLangCode(java.lang.
	 * String)
	 */
	@Override
	public DeviceResponseDto getDeviceLangCode(String langCode) {
		List<Device> deviceList = null;
		List<DeviceDto> deviceDtoList = null;
		DeviceResponseDto deviceResponseDto = new DeviceResponseDto();
		try {
			deviceList = deviceRepository.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(DeviceErrorCode.DEVICE_FETCH_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_FETCH_EXCEPTION.getErrorMessage() + "  " + ExceptionUtils.parseException(e));
		}
		if (deviceList != null && !deviceList.isEmpty()) {
			deviceDtoList = MapperUtils.mapAll(deviceList, DeviceDto.class);

		} else {
			throw new DataNotFoundException(DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		deviceResponseDto.setDevices(deviceDtoList);
		return deviceResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DeviceService#
	 * getDeviceLangCodeAndDeviceType(java.lang.String, java.lang.String)
	 */
	@Override
	public DeviceLangCodeResponseDto getDeviceLangCodeAndDeviceType(String langCode, String dtypeCode) {

		List<Object[]> objectList = null;
		List<DeviceLangCodeDtypeDto> deviceLangCodeDtypeDtoList = null;
		DeviceLangCodeResponseDto deviceLangCodeResponseDto = new DeviceLangCodeResponseDto();
		try {
			objectList = deviceRepository.findByLangCodeAndDtypeCode(langCode, dtypeCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(DeviceErrorCode.DEVICE_FETCH_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_FETCH_EXCEPTION.getErrorMessage() + "  " + ExceptionUtils.parseException(e));
		}
		if (objectList != null && !objectList.isEmpty()) {
			deviceLangCodeDtypeDtoList = MapperUtils.mapDeviceDto(objectList);
		} else {
			throw new DataNotFoundException(DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		deviceLangCodeResponseDto.setDevices(deviceLangCodeDtypeDtoList);
		return deviceLangCodeResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DeviceService#saveDevice(io.mosip.kernel.
	 * masterdata.dto.RequestDto)
	 */
	@Override
	@Transactional
	public IdAndLanguageCodeID createDevice(DeviceDto deviceDto) {
		Device device = null;

		Device entity = MetaDataUtils.setCreateMetaData(deviceDto, Device.class);
		DeviceHistory entityHistory = MetaDataUtils.setCreateMetaData(deviceDto, DeviceHistory.class);
		entityHistory.setEffectDateTime(entity.getCreatedDateTime());
		entityHistory.setCreatedDateTime(entity.getCreatedDateTime());

		try {
			// device.setIsActive(false);

			device = deviceRepository.create(entity);
			deviceHistoryService.createDeviceHistory(entityHistory);

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(DeviceErrorCode.DEVICE_INSERT_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_INSERT_EXCEPTION.getErrorMessage() + " " + ExceptionUtils.parseException(e));
		}

		IdAndLanguageCodeID idAndLanguageCodeID = new IdAndLanguageCodeID();
		MapperUtils.map(device, idAndLanguageCodeID);

		return idAndLanguageCodeID;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DeviceService#updateDevice(io.mosip.kernel
	 * .masterdata.dto.RequestDto)
	 */
	@Override
	@Transactional
	public IdAndLanguageCodeID updateDevice(DeviceDto deviceRequestDto) {
		Device entity = null;
		Device updatedDevice = null;
		try {
			Device oldDevice = deviceRepository.findByIdAndLangCodeAndIsDeletedFalseOrIsDeletedIsNullNoIsActive(
					deviceRequestDto.getId(), deviceRequestDto.getLangCode());

			if (oldDevice != null) {
				entity = MetaDataUtils.setUpdateMetaData(deviceRequestDto, oldDevice, false);
				updatedDevice = deviceRepository.update(entity);
				DeviceHistory deviceHistory = new DeviceHistory();
				MapperUtils.map(updatedDevice, deviceHistory);
				MapperUtils.setBaseFieldValue(updatedDevice, deviceHistory);

				deviceHistory.setEffectDateTime(updatedDevice.getUpdatedDateTime());
				deviceHistory.setUpdatedDateTime(updatedDevice.getUpdatedDateTime());

				deviceHistoryService.createDeviceHistory(deviceHistory);
			} else {
				throw new RequestException(DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorCode(),
						DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(DeviceErrorCode.DEVICE_INSERT_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_UPDATE_EXCEPTION.getErrorMessage() + " " + ExceptionUtils.parseException(e));
		}

		IdAndLanguageCodeID idAndLanguageCodeID = new IdAndLanguageCodeID();
		idAndLanguageCodeID.setId(entity.getId());
		idAndLanguageCodeID.setLangCode(entity.getLangCode());

		return idAndLanguageCodeID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DeviceService#deleteDevice(java.lang.
	 * String)
	 */
	@Override
	@Transactional
	public IdResponseDto deleteDevice(String id) {
		List<Device> foundDeviceList = new ArrayList<>();
		Device deletedDevice = null;
		try {
			foundDeviceList = deviceRepository.findByIdAndIsDeletedFalseOrIsDeletedIsNull(id);

			if (!foundDeviceList.isEmpty()) {
				for (Device foundDevice : foundDeviceList) {

					List<RegistrationCenterMachineDevice> registrationCenterMachineDeviceList = registrationCenterMachineDeviceRepository
							.findByDeviceIdAndIsDeletedFalseOrIsDeletedIsNull(foundDevice.getId());
					List<RegistrationCenterDevice> registrationCenterDeviceList = registrationCenterDeviceRepository
							.findByDeviceIdAndIsDeletedFalseOrIsDeletedIsNull(foundDevice.getId());
					if (registrationCenterMachineDeviceList.isEmpty() && registrationCenterDeviceList.isEmpty()) {

						MetaDataUtils.setDeleteMetaData(foundDevice);
						deletedDevice = deviceRepository.update(foundDevice);

						DeviceHistory deviceHistory = new DeviceHistory();
						MapperUtils.map(deletedDevice, deviceHistory);
						MapperUtils.setBaseFieldValue(deletedDevice, deviceHistory);

						deviceHistory.setEffectDateTime(deletedDevice.getDeletedDateTime());
						deviceHistory.setDeletedDateTime(deletedDevice.getDeletedDateTime());
						deviceHistoryService.createDeviceHistory(deviceHistory);
					} else {
						throw new RequestException(DeviceErrorCode.DEPENDENCY_EXCEPTION.getErrorCode(),
								DeviceErrorCode.DEPENDENCY_EXCEPTION.getErrorMessage());
					}
				}
			} else {
				throw new RequestException(DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorCode(),
						DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorMessage());
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(DeviceErrorCode.DEVICE_DELETE_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_DELETE_EXCEPTION.getErrorMessage() + " " + ExceptionUtils.parseException(e));
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
	public PageDto<DeviceRegistrationCenterDto> getDevicesByRegistrationCenter(String regCenterId, int page, int size,
			String orderBy, String direction) {
		PageDto<DeviceRegistrationCenterDto> pageDto = new PageDto<>();
		List<DeviceRegistrationCenterDto> deviceRegistrationCenterDtoList = null;
		Page<Device> pageEntity = null;

		try {
			pageEntity = deviceRepository.findDeviceByRegCenterId(regCenterId,
					PageRequest.of(page, size, Sort.by(Direction.fromString(direction), orderBy)));
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DeviceErrorCode.REGISTRATION_CENTER_DEVICE_FETCH_EXCEPTION.getErrorCode(),
					DeviceErrorCode.REGISTRATION_CENTER_DEVICE_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		if (pageEntity != null && !pageEntity.getContent().isEmpty()) {
			deviceRegistrationCenterDtoList = MapperUtils.mapAll(pageEntity.getContent(),
					DeviceRegistrationCenterDto.class);
			for (DeviceRegistrationCenterDto deviceRegistrationCenterDto : deviceRegistrationCenterDtoList) {
				deviceRegistrationCenterDto.setRegCentId(regCenterId);
			}
		} else {
			throw new RequestException(DeviceErrorCode.DEVICE_REGISTRATION_CENTER_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_REGISTRATION_CENTER_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		pageDto.setPageNo(pageEntity.getNumber());
		pageDto.setPageSize(pageEntity.getSize());
		pageDto.setSort(pageEntity.getSort());
		pageDto.setTotalItems(pageEntity.getTotalElements());
		pageDto.setTotalPages(pageEntity.getTotalPages());
		pageDto.setData(deviceRegistrationCenterDtoList);

		return pageDto;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DeviceService#searchDevice(io.mosip.kernel
	 * .masterdata.dto.request.SearchDto)
	 */
	@Override
	public PageResponseDto<DeviceSearchDto> searchDevice(SearchDto dto) {
		PageResponseDto<DeviceSearchDto> pageDto = new PageResponseDto<>();
		List<DeviceSearchDto> devices = null;
		List<SearchFilter> addList = new ArrayList<>();
		List<SearchFilter> removeList = new ArrayList<>();
		List<String> mappedDeviceIdList = null;
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
					mappedDeviceIdList = deviceRepository.findMappedDeviceId();
					addList.addAll(buildRegistrationCenterDeviceTypeSearchFilter(mappedDeviceIdList));
					if (addList.isEmpty()) {
						throw new DataNotFoundException(
								DeviceErrorCode.MAPPED_DEVICE_ID_NOT_FOUND_EXCEPTION.getErrorCode(),
								String.format(DeviceErrorCode.MAPPED_DEVICE_ID_NOT_FOUND_EXCEPTION.getErrorMessage()));
					}

				} else {
					if (filter.getValue().equalsIgnoreCase("unassigned")) {
						mappedDeviceIdList = deviceRepository.findNotMappedDeviceId();
						addList.addAll(buildRegistrationCenterDeviceTypeSearchFilter(mappedDeviceIdList));
						if (addList.isEmpty()) {
							throw new DataNotFoundException(
									DeviceErrorCode.DEVICE_ID_ALREADY_MAPPED_EXCEPTION.getErrorCode(), String.format(
											DeviceErrorCode.DEVICE_ID_ALREADY_MAPPED_EXCEPTION.getErrorMessage()));
						}
					} else {
						throw new RequestException(DeviceErrorCode.INVALID_DEVICE_FILTER_VALUE_EXCEPTION.getErrorCode(),
								DeviceErrorCode.INVALID_DEVICE_FILTER_VALUE_EXCEPTION.getErrorMessage());
					}

				}
				removeList.add(filter);
			}

			if (column.equalsIgnoreCase("deviceTypeName")) {
				filter.setColumnName(MasterDataConstant.NAME);
				if (filterValidator.validate(DeviceTypeDto.class, Arrays.asList(filter))) {
					Page<DeviceType> deviceTypes = masterdataSearchHelper.searchMasterdata(DeviceType.class,
							new SearchDto(Arrays.asList(filter), Collections.emptyList(), new Pagination(), null),
							null);
					List<SearchFilter> deviceCodeFilter = buildDeviceTypeSearchFilter(deviceTypes.getContent());
					if (deviceCodeFilter.isEmpty()) {
						throw new DataNotFoundException(
								DeviceErrorCode.DEVICE_ID_NOT_FOUND_FOR_NAME_EXCEPTION.getErrorCode(),
								String.format(DeviceErrorCode.DEVICE_ID_NOT_FOUND_FOR_NAME_EXCEPTION.getErrorMessage(),
										filter.getValue()));
					}
					Page<DeviceSpecification> devspecs = masterdataSearchHelper.searchMasterdata(
							DeviceSpecification.class,
							new SearchDto(deviceCodeFilter, Collections.emptyList(), new Pagination(), null), null);
					removeList.add(filter);
					addList.addAll(buildDeviceSpecificationSearchFilter(devspecs.getContent()));
					if (addList.isEmpty()) {
						throw new DataNotFoundException(
								DeviceErrorCode.DEVICE_SPECIFICATION_ID_NOT_FOUND_FOR_NAME_EXCEPTION.getErrorCode(),
								String.format(DeviceErrorCode.DEVICE_SPECIFICATION_ID_NOT_FOUND_FOR_NAME_EXCEPTION
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
				throw new MasterDataServiceException(DeviceErrorCode.DEVICE_NOT_TAGGED_TO_ZONE.getErrorCode(),
						DeviceErrorCode.DEVICE_NOT_TAGGED_TO_ZONE.getErrorMessage());
		}
		dto.getFilters().removeAll(removeList);

		if (filterValidator.validate(DeviceSearchDto.class, dto.getFilters())) {
			OptionalFilter optionalFilter = new OptionalFilter(addList);
			OptionalFilter zoneOptionalFilter = new OptionalFilter(zoneFilter);
			Page<Device> page = masterdataSearchHelper.searchMasterdata(Device.class, dto,
					new OptionalFilter[] { optionalFilter, zoneOptionalFilter });
			if (page.getContent() != null && !page.getContent().isEmpty()) {
				pageDto = PageUtils.pageResponse(page);
				devices = MapperUtils.mapAll(page.getContent(), DeviceSearchDto.class);
				setDeviceMetadata(devices, zones);
				pageDto.setData(devices);
			}

		}
		return pageDto;
	}

	/**
	 * Method to set each device zone meta data.
	 * 
	 * @param list
	 *            list of {@link DeviceSearchDto}.
	 * @param zones
	 *            the list of zones.
	 */
	public void setDeviceMetadata(List<DeviceSearchDto> list, List<Zone> zones) {
		list.forEach(i -> setZoneMetadata(i, zones));
	}

	/**
	 * Method to set Zone metadata
	 * 
	 * @param devices
	 *            metadata to be added
	 * @param zones
	 *            list of zones
	 * 
	 */
	private void setZoneMetadata(DeviceSearchDto devices, List<Zone> zones) {
		Optional<Zone> zone = zones.stream()
				.filter(i -> i.getCode().equals(devices.getZoneCode()) && i.getLangCode().equals(devices.getLangCode()))
				.findFirst();
		if (zone.isPresent()) {
			devices.setZone(zone.get().getName());
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
			throw new MasterDataServiceException(DeviceErrorCode.ZONE_NOT_EXIST.getErrorCode(),
					String.format(DeviceErrorCode.ZONE_NOT_EXIST.getErrorMessage(), filter.getValue()));
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
	 * This method return Device Id list filters.
	 * 
	 * @param deviceIdList
	 *            the Device Id list.
	 * @return the list of {@link SearchFilter}.
	 */
	private List<SearchFilter> buildRegistrationCenterDeviceTypeSearchFilter(List<String> deviceIdList) {
		if (deviceIdList != null && !deviceIdList.isEmpty())
			return deviceIdList.stream().filter(Objects::nonNull).map(this::buildRegistrationCenterDeviceType)
					.collect(Collectors.toList());
		return Collections.emptyList();
	}

	/**
	 * This method return Device Types list filters.
	 * 
	 * @param deviceTypes
	 *            the list of Device Type.
	 * @return the list of {@link SearchFilter}.
	 */
	private List<SearchFilter> buildDeviceTypeSearchFilter(List<DeviceType> deviceTypes) {
		if (deviceTypes != null && !deviceTypes.isEmpty())
			return deviceTypes.stream().filter(Objects::nonNull).map(this::buildDeviceType)
					.collect(Collectors.toList());
		return Collections.emptyList();
	}

	/**
	 * This method return Device Specification list filters.
	 * 
	 * @param deviceSpecs
	 *            the list of Device Specification.
	 * @return the list of {@link SearchFilter}.
	 */
	private List<SearchFilter> buildDeviceSpecificationSearchFilter(List<DeviceSpecification> deviceSpecs) {
		if (deviceSpecs != null && !deviceSpecs.isEmpty())
			return deviceSpecs.stream().filter(Objects::nonNull).map(this::buildDeviceSpecification)
					.collect(Collectors.toList());
		return Collections.emptyList();
	}

	/**
	 * This method provide search filter for provided device id.
	 * 
	 * @param deviceId
	 *            the device id.
	 * @return the {@link SearchFilter}.
	 */
	private SearchFilter buildRegistrationCenterDeviceType(String deviceId) {
		SearchFilter filter = new SearchFilter();
		filter.setColumnName("id");
		filter.setType(FilterTypeEnum.EQUALS.name());
		filter.setValue(deviceId);
		return filter;
	}

	/**
	 * This method provide search filter for provided Device specification.
	 * 
	 * @param deviceSpecification
	 *            the device specification.
	 * @return the {@link SearchFilter}.
	 */
	private SearchFilter buildDeviceSpecification(DeviceSpecification deviceSpecification) {
		SearchFilter filter = new SearchFilter();
		filter.setColumnName("deviceSpecId");
		filter.setType(FilterTypeEnum.EQUALS.name());
		filter.setValue(deviceSpecification.getId());
		return filter;
	}

	/**
	 * This method provide search filter for provided Device Type.
	 * 
	 * @param deviceType
	 *            the device type.
	 * @return the {@link SearchFilter}.
	 */
	private SearchFilter buildDeviceType(DeviceType deviceType) {
		SearchFilter filter = new SearchFilter();
		filter.setColumnName("deviceTypeCode");
		filter.setType(FilterTypeEnum.EQUALS.name());
		filter.setValue(deviceType.getCode());
		return filter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DeviceService#deviceFilterValues(io.mosip.
	 * kernel.masterdata.dto.request.FilterValueDto)
	 */
	@Override
	public FilterResponseDto deviceFilterValues(FilterValueDto filterValueDto) {
		FilterResponseDto filterResponseDto = new FilterResponseDto();
		List<ColumnValue> columnValueList = new ArrayList<>();
		if (filterColumnValidator.validate(FilterDto.class, filterValueDto.getFilters(), Device.class)) {
			for (FilterDto filterDto : filterValueDto.getFilters()) {
				masterDataFilterHelper.filterValues(Device.class, filterDto, filterValueDto).forEach(filterValue -> {
					if (filterValue != null) {
						ColumnValue columnValue = new ColumnValue();
						columnValue.setFieldID(filterDto.getColumnName());
						columnValue.setFieldValue(filterValue.toString());
						columnValueList.add(columnValue);
					}
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
	 * io.mosip.kernel.masterdata.service.DeviceService#decommissionDevice(java.lang
	 * .String)
	 */
	@Override
	@Transactional
	public IdResponseDto decommissionDevice(String deviceId) {
		IdResponseDto deviceCodeId = new IdResponseDto();
		// MapperUtils.mapFieldValues(deviceId, deviceCodeId);
		try {
			int updatedRows = deviceRepository.decommissionDevice(deviceId);
			if (updatedRows < 1) {
				throw new RequestException(MachineErrorCode.MAPPED_MACHINE_ID_NOT_FOUND_EXCEPTION.getErrorCode(),
						MachineErrorCode.MAPPED_MACHINE_ID_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(MachineErrorCode.MACHINE_DECOMMISSION_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_DECOMMISSION_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		deviceCodeId.setId(deviceId);
		return deviceCodeId;
	}

}
