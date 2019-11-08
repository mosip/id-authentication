package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.DeviceTypeErrorCode;
import io.mosip.kernel.masterdata.constant.MachineTypeErrorCode;
import io.mosip.kernel.masterdata.dto.DeviceTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DeviceTypeExtnDto;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.ColumnValue;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DeviceTypeRepository;
import io.mosip.kernel.masterdata.service.DeviceTypeService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MasterDataFilterHelper;
import io.mosip.kernel.masterdata.utils.MasterdataSearchHelper;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.PageUtils;
import io.mosip.kernel.masterdata.validator.FilterColumnValidator;
import io.mosip.kernel.masterdata.validator.FilterTypeValidator;

/**
 * Service class have methods to save a DeviceType Details to the Database table
 * 
 * @author Megha Tanga
 * @author Ayush Saxena
 * @since 1.0.0
 *
 */
@Service
public class DeviceTypeServiceImpl implements DeviceTypeService {

	/**
	 * Reference to DeviceTypeRepository.
	 */
	@Autowired
	DeviceTypeRepository deviceTypeRepository;

	@Autowired
	private FilterTypeValidator filterValidator;

	@Autowired
	private MasterdataSearchHelper masterdataSearchHelper;

	@Autowired
	private MasterDataFilterHelper masterDataFilterHelper;

	@Autowired
	private FilterColumnValidator filterColumnValidator;

	@Autowired
	private PageUtils pageUtils;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DeviceTypeService#createDeviceTypes(io.
	 * mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public CodeAndLanguageCodeID createDeviceType(DeviceTypeDto deviceType) {
		DeviceType renDeviceType = null;

		DeviceType entity = MetaDataUtils.setCreateMetaData(deviceType, DeviceType.class);

		try {
			renDeviceType = deviceTypeRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(MachineTypeErrorCode.MACHINE_TYPE_INSERT_EXCEPTION.getErrorCode(),
					MachineTypeErrorCode.MACHINE_TYPE_INSERT_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

		CodeAndLanguageCodeID codeLangCodeId = new CodeAndLanguageCodeID();
		MapperUtils.map(renDeviceType, codeLangCodeId);
		return codeLangCodeId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DeviceTypeService#getAllDeviceTypes(int,
	 * int, java.lang.String, java.lang.String)
	 */
	@Override
	public PageDto<DeviceTypeExtnDto> getAllDeviceTypes(int pageNumber, int pageSize, String sortBy, String orderBy) {
		List<DeviceTypeExtnDto> deviceTypes = null;
		PageDto<DeviceTypeExtnDto> deviceTypesPages = null;
		try {
			Page<DeviceType> pageData = deviceTypeRepository
					.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Direction.fromString(orderBy), sortBy)));
			if (pageData != null && pageData.getContent() != null && !pageData.getContent().isEmpty()) {
				deviceTypes = MapperUtils.mapAll(pageData.getContent(), DeviceTypeExtnDto.class);
				deviceTypesPages = new PageDto<>(pageData.getNumber(), pageData.getTotalPages(),
						pageData.getTotalElements(), deviceTypes);
			} else {
				throw new DataNotFoundException(DeviceTypeErrorCode.DEVICE_TYPE_NOT_FOUND_EXCEPTION.getErrorCode(),
						DeviceTypeErrorCode.DEVICE_TYPE_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(DeviceTypeErrorCode.DEVICE_TYPE_FETCH_EXCEPTION.getErrorCode(),
					DeviceTypeErrorCode.DEVICE_TYPE_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		return deviceTypesPages;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DeviceTypeService#deviceTypeSearch(io.
	 * mosip.kernel.masterdata.dto.request.SearchDto)
	 */
	@Override
	public PageResponseDto<DeviceTypeExtnDto> deviceTypeSearch(SearchDto searchRequestDto) {
		PageResponseDto<DeviceTypeExtnDto> pageDto = new PageResponseDto<>();

		List<DeviceTypeExtnDto> deviceTypeList = null;
		if (filterValidator.validate(DeviceTypeExtnDto.class, searchRequestDto.getFilters())) {
			pageUtils.validateSortField(DeviceType.class, searchRequestDto.getSort());
			Page<DeviceType> page = masterdataSearchHelper.searchMasterdata(DeviceType.class, searchRequestDto, null);
			if (page.getContent() != null && !page.getContent().isEmpty()) {
				pageDto = PageUtils.pageResponse(page);
				deviceTypeList = MapperUtils.mapAll(page.getContent(), DeviceTypeExtnDto.class);
				pageDto.setData(deviceTypeList);
			}
		}
		return pageDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DeviceTypeService#deviceTypeFilterValues(
	 * io.mosip.kernel.masterdata.dto.request.FilterValueDto)
	 */
	@Override
	public FilterResponseDto deviceTypeFilterValues(FilterValueDto filterValueDto) {
		FilterResponseDto filterResponseDto = new FilterResponseDto();
		List<ColumnValue> columnValueList = new ArrayList<>();
		if (filterColumnValidator.validate(FilterDto.class, filterValueDto.getFilters(), DeviceType.class)) {
			for (FilterDto filterDto : filterValueDto.getFilters()) {
				masterDataFilterHelper.filterValues(DeviceType.class, filterDto, filterValueDto)
						.forEach(filterValue -> {
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

}
