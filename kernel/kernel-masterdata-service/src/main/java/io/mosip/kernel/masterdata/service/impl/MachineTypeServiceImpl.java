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
import io.mosip.kernel.masterdata.constant.MachineTypeErrorCode;
import io.mosip.kernel.masterdata.dto.MachineTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.MachineExtnDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.MachineTypeExtnDto;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.dto.response.ColumnValue;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.MachineType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.MachineTypeRepository;
import io.mosip.kernel.masterdata.service.MachineTypeService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MasterDataFilterHelper;
import io.mosip.kernel.masterdata.utils.MasterdataSearchHelper;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.OptionalFilter;
import io.mosip.kernel.masterdata.utils.PageUtils;
import io.mosip.kernel.masterdata.validator.FilterColumnValidator;
import io.mosip.kernel.masterdata.validator.FilterTypeValidator;

/**
 * This class have methods to save a Machine Type Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Service
public class MachineTypeServiceImpl implements MachineTypeService {

	/**
	 * Field to hold Machine Repository object
	 */
	@Autowired
	MachineTypeRepository machineTypeRepository;

	/**
	 * Reference to {@link FilterTypeValidator}.
	 */
	@Autowired
	private FilterTypeValidator filterValidator;

	/**
	 * Referencr to {@link MasterdataSearchHelper}.
	 */
	@Autowired
	private MasterdataSearchHelper masterdataSearchHelper;

	/**
	 * Reference to {@link MasterDataFilterHelper}.
	 */
	@Autowired
	private MasterDataFilterHelper masterDataFilterHelper;

	/**
	 * Refernece to {@link FilterColumnValidator}.
	 */
	@Autowired
	private FilterColumnValidator filterColumnValidator;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.MachineTypeService#createMachineType(io.
	 * mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public CodeAndLanguageCodeID createMachineType(MachineTypeDto machineType) {
		MachineType renMachineType = null;

		MachineType entity = MetaDataUtils.setCreateMetaData(machineType, MachineType.class);

		try {
			renMachineType = machineTypeRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(MachineTypeErrorCode.MACHINE_TYPE_INSERT_EXCEPTION.getErrorCode(),
					MachineTypeErrorCode.MACHINE_TYPE_INSERT_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

		CodeAndLanguageCodeID codeLangCodeId = new CodeAndLanguageCodeID();
		MapperUtils.map(renMachineType, codeLangCodeId);
		return codeLangCodeId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.MachineTypeService#getAllMachineTypes(int,
	 * int, java.lang.String, java.lang.String)
	 */
	@Override
	public PageDto<MachineTypeExtnDto> getAllMachineTypes(int pageNumber, int pageSize, String sortBy, String orderBy) {
		List<MachineTypeExtnDto> machineTypes = null;
		PageDto<MachineTypeExtnDto> machineTypesPages = null;
		try {
			Page<MachineType> pageData = machineTypeRepository
					.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Direction.fromString(orderBy), sortBy)));
			if (pageData != null && pageData.getContent() != null && !pageData.getContent().isEmpty()) {
				machineTypes = MapperUtils.mapAll(pageData.getContent(), MachineTypeExtnDto.class);
				machineTypesPages = new PageDto<>(pageData.getNumber(), pageData.getTotalPages(),
						pageData.getTotalElements(), machineTypes);
			} else {
				throw new DataNotFoundException(MachineTypeErrorCode.MACHINE_TYPE_NOT_FOUND.getErrorCode(),
						MachineTypeErrorCode.MACHINE_TYPE_NOT_FOUND.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(MachineTypeErrorCode.MACHINE_TYPE_FETCH_EXCEPTION.getErrorCode(),
					MachineTypeErrorCode.MACHINE_TYPE_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		return machineTypesPages;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.MachineTypeService#searchMachineType(io.
	 * mosip.kernel.masterdata.dto.request.SearchDto)
	 */
	@Override
	public PageResponseDto<MachineTypeExtnDto> searchMachineType(SearchDto dto) {
		PageResponseDto<MachineTypeExtnDto> pageDto = new PageResponseDto<>();
		List<MachineTypeExtnDto> machineTypes = null;
		List<SearchFilter> addList = new ArrayList<>();
		if (filterValidator.validate(MachineExtnDto.class, dto.getFilters())) {
			OptionalFilter optionalFilter = new OptionalFilter(addList);
			Page<MachineType> page = masterdataSearchHelper.searchMasterdata(MachineType.class, dto,
					new OptionalFilter[] { optionalFilter });
			if (page.getContent() != null && !page.getContent().isEmpty()) {
				pageDto = PageUtils.pageResponse(page);
				machineTypes = MapperUtils.mapAll(page.getContent(), MachineTypeExtnDto.class);
				pageDto.setData(machineTypes);
			}

		}
		return pageDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.MachineTypeService#
	 * machineTypesFilterValues(io.mosip.kernel.masterdata.dto.request.
	 * FilterValueDto)
	 */
	@Override
	public FilterResponseDto machineTypesFilterValues(FilterValueDto filterValueDto) {
		FilterResponseDto filterResponseDto = new FilterResponseDto();
		List<ColumnValue> columnValueList = new ArrayList<>();
		if (filterColumnValidator.validate(FilterDto.class, filterValueDto.getFilters())) {
			for (FilterDto filterDto : filterValueDto.getFilters()) {
				List<?> filterValues = masterDataFilterHelper.filterValues(MachineType.class, filterDto,
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

}
