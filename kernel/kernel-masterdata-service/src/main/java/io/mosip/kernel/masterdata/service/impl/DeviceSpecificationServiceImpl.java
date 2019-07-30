package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.masterdata.constant.DeviceSpecificationErrorCode;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DeviceSpecificationExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.dto.response.ColumnValue;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.Device;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.entity.id.IdAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.DeviceRepository;
import io.mosip.kernel.masterdata.repository.DeviceSpecificationRepository;
import io.mosip.kernel.masterdata.service.DeviceSpecificationService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MasterDataFilterHelper;
import io.mosip.kernel.masterdata.utils.MasterdataSearchHelper;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.OptionalFilter;
import io.mosip.kernel.masterdata.utils.PageUtils;
import io.mosip.kernel.masterdata.validator.FilterColumnValidator;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;
import io.mosip.kernel.masterdata.validator.FilterTypeValidator;

/**
 * Service class has methods to save and fetch DeviceSpecification Details
 * 
 * @author Megha Tanga
 * @author Uday
 * @since 1.0.0
 *
 */
/**
 * @author M1046571
 *
 */
@Service
public class DeviceSpecificationServiceImpl implements DeviceSpecificationService {

	@Autowired
	DeviceSpecificationRepository deviceSpecificationRepository;

	@Autowired
	DeviceRepository deviceRepository;

	@Autowired
	MasterdataSearchHelper masterDataSearchHelper;

	@Autowired
	MasterDataFilterHelper masterDataFilterHelper;

	@Autowired
	FilterColumnValidator filterColumnValidator;

	@Autowired
	FilterTypeValidator filterValidator;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DeviceSpecificationService#
	 * findDeviceSpecificationByLangugeCode(java.lang.String)
	 */
	@Override
	public List<DeviceSpecificationDto> findDeviceSpecificationByLangugeCode(String languageCode) {
		List<DeviceSpecification> deviceSpecificationList = null;
		List<DeviceSpecificationDto> deviceSpecificationDtoList = null;
		try {
			deviceSpecificationList = deviceSpecificationRepository
					.findByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(languageCode);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_DATA_FETCH_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_DATA_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		if (deviceSpecificationList != null && !deviceSpecificationList.isEmpty()) {
			deviceSpecificationDtoList = MapperUtils.mapAll(deviceSpecificationList, DeviceSpecificationDto.class);
			return deviceSpecificationDtoList;
		} else {
			throw new DataNotFoundException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DeviceSpecificationService#
	 * findDeviceSpecByLangCodeAndDevTypeCode(java.lang.String, java.lang.String)
	 */
	@Override
	public List<DeviceSpecificationDto> findDeviceSpecByLangCodeAndDevTypeCode(String languageCode,
			String deviceTypeCode) {
		List<DeviceSpecification> deviceSpecificationList = null;
		List<DeviceSpecificationDto> deviceSpecificationDtoList = null;
		try {
			deviceSpecificationList = deviceSpecificationRepository
					.findByLangCodeAndDeviceTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(languageCode, deviceTypeCode);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_DATA_FETCH_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_DATA_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		if (deviceSpecificationList != null && !deviceSpecificationList.isEmpty()) {
			deviceSpecificationDtoList = MapperUtils.mapAll(deviceSpecificationList, DeviceSpecificationDto.class);
			return deviceSpecificationDtoList;
		} else {
			throw new DataNotFoundException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DeviceSpecificationService#
	 * createDeviceSpecification(io.mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public IdAndLanguageCodeID createDeviceSpecification(DeviceSpecificationDto deviceSpecifications) {
		DeviceSpecification renDeviceSpecification = new DeviceSpecification();

		DeviceSpecification entity = MetaDataUtils.setCreateMetaData(deviceSpecifications, DeviceSpecification.class);
		try {
			renDeviceSpecification = deviceSpecificationRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_INSERT_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_INSERT_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

		IdAndLanguageCodeID idAndLanguageCodeID = new IdAndLanguageCodeID();
		MapperUtils.map(renDeviceSpecification, idAndLanguageCodeID);

		return idAndLanguageCodeID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DeviceSpecificationService#
	 * updateDeviceSpecification(io.mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public IdAndLanguageCodeID updateDeviceSpecification(DeviceSpecificationDto deviceSpecification) {
		IdAndLanguageCodeID idAndLanguageCodeID = new IdAndLanguageCodeID();
		try {
			DeviceSpecification entity = deviceSpecificationRepository
					.findByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(deviceSpecification.getId(),
							deviceSpecification.getLangCode());
			if (!EmptyCheckUtils.isNullEmpty(entity)) {
				MetaDataUtils.setUpdateMetaData(deviceSpecification, entity, false);
				deviceSpecificationRepository.update(entity);
				idAndLanguageCodeID.setId(entity.getId());
				idAndLanguageCodeID.setLangCode(entity.getLangCode());
			} else {
				throw new RequestException(
						DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
						DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_UPDATE_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_UPDATE_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		return idAndLanguageCodeID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DeviceSpecificationService#
	 * deleteDeviceSpecification(java.lang.String)
	 */
	@Override
	public IdResponseDto deleteDeviceSpecification(String id) {
		IdResponseDto idResponseDto = new IdResponseDto();
		try {
			List<DeviceSpecification> deviceSpecifications = deviceSpecificationRepository
					.findByIdAndIsDeletedFalseorIsDeletedIsNull(id);

			if (!deviceSpecifications.isEmpty()) {
				for (DeviceSpecification deviceSpecification : deviceSpecifications) {
					List<Device> renDeviceList = deviceRepository
							.findDeviceByDeviceSpecIdAndIsDeletedFalseorIsDeletedIsNull(deviceSpecification.getId());
					if (renDeviceList.isEmpty()) {
						MetaDataUtils.setDeleteMetaData(deviceSpecification);
						deviceSpecificationRepository.update(deviceSpecification);
						idResponseDto.setId(deviceSpecification.getId());
					} else {
						throw new MasterDataServiceException(
								DeviceSpecificationErrorCode.DEVICE_DELETE_DEPENDENCY_EXCEPTION.getErrorCode(),
								DeviceSpecificationErrorCode.DEVICE_DELETE_DEPENDENCY_EXCEPTION.getErrorMessage());
					}
				}

			} else {
				throw new RequestException(
						DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
						DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_DELETE_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_DELETE_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

		return idResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DeviceSpecificationService#
	 * getAllDeviceSpecifications()
	 */
	@Override
	public PageDto<DeviceSpecificationExtnDto> getAllDeviceSpecifications(int pageNumber, int pageSize, String sortBy,
			String orderBy) {
		List<DeviceSpecificationExtnDto> deviceSpecs = null;
		PageDto<DeviceSpecificationExtnDto> pageDto = null;
		try {
			Page<DeviceSpecification> pageEntity = deviceSpecificationRepository
					.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Direction.fromString(orderBy), sortBy)));
			if (pageEntity != null && pageEntity.getContent() != null && !pageEntity.getContent().isEmpty()) {
				deviceSpecs = MapperUtils.mapAll(pageEntity.getContent(), DeviceSpecificationExtnDto.class);
				pageDto = new PageDto<>(pageEntity.getNumber(), pageEntity.getTotalPages(),
						pageEntity.getTotalElements(), deviceSpecs);
			} else {
				throw new DataNotFoundException(
						DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
						DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_DATA_FETCH_EXCEPTION.getErrorCode(),
					DeviceSpecificationErrorCode.DEVICE_SPECIFICATION_DATA_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		return pageDto;
	}

	@Override
	public FilterResponseDto deviceSpecFilterValues(FilterValueDto filterValueDto) {
		FilterResponseDto filterResponseDto = new FilterResponseDto();
		List<ColumnValue> columnValueList = new ArrayList<>();
		if (filterColumnValidator.validate(FilterDto.class, filterValueDto.getFilters(), DeviceSpecification.class)) {
			for (FilterDto filterDto : filterValueDto.getFilters()) {
				List<?> filterValues = masterDataFilterHelper.filterValues(DeviceSpecification.class, filterDto,
						filterValueDto);
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

	@Override
	public PageResponseDto<DeviceSpecificationExtnDto> searchDeviceSpec(SearchDto dto) {
		PageResponseDto<DeviceSpecificationExtnDto> pageDto = new PageResponseDto<>();
		List<SearchFilter> addList = new ArrayList<>();
		List<SearchFilter> removeList = new ArrayList<>();
		List<DeviceSpecificationExtnDto> devices = null;
		List<SearchFilter> deviceCodeFilter = null;
		for (SearchFilter filter : dto.getFilters()) {
			String column = filter.getColumnName();
			if (column.equalsIgnoreCase("deviceTypeName")) {
				filter.setColumnName("name");
				if (filterValidator.validate(DeviceTypeDto.class, Arrays.asList(filter))) {
					Page<DeviceType> deviceTypes = masterDataSearchHelper.searchMasterdata(DeviceType.class,
							new SearchDto(Arrays.asList(filter), Collections.emptyList(), new Pagination(), null),
							null);
					removeList.add(filter);
					deviceCodeFilter = buildDeviceTypeSearchFilter(deviceTypes.getContent());
					if (deviceCodeFilter.isEmpty()) {
						throw new DataNotFoundException(
								DeviceSpecificationErrorCode.PAGE_DATA_NOT_FOUND_EXCEPTION.getErrorCode(),
								DeviceSpecificationErrorCode.PAGE_DATA_NOT_FOUND_EXCEPTION.getErrorMessage());
					}

				}
			}

		}
		dto.getFilters().removeAll(removeList);
		if (filterValidator.validate(DeviceSpecificationExtnDto.class, dto.getFilters())) {
			OptionalFilter optionalFilter = new OptionalFilter(addList);
			OptionalFilter optionalFilterForDeviceTypeName = new OptionalFilter(deviceCodeFilter);
			Page<DeviceSpecification> page = masterDataSearchHelper.searchMasterdata(DeviceSpecification.class, dto,
					new OptionalFilter[] { optionalFilter, optionalFilterForDeviceTypeName });
			if (page.getContent() != null && !page.getContent().isEmpty()) {
				pageDto = PageUtils.pageResponse(page);
				devices = MapperUtils.mapAll(page.getContent(), DeviceSpecificationExtnDto.class);
				pageDto.setData(devices);
			}

		}

		return pageDto;
	}

	private List<SearchFilter> buildDeviceTypeSearchFilter(List<DeviceType> deviceTypes) {
		if (deviceTypes != null && !deviceTypes.isEmpty())
			return deviceTypes.stream().filter(Objects::nonNull).map(this::buildDeviceType)
					.collect(Collectors.toList());
		return Collections.emptyList();
	}

	private SearchFilter buildDeviceType(DeviceType deviceType) {
		SearchFilter filter = new SearchFilter();
		filter.setColumnName("deviceTypeCode");
		filter.setType(FilterTypeEnum.EQUALS.name());
		filter.setValue(deviceType.getCode());
		return filter;
	}

}
