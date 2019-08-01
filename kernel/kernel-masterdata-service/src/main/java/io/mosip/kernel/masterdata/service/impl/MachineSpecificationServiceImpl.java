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
import io.mosip.kernel.masterdata.constant.MachineSpecificationErrorCode;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.dto.MachineSpecificationDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.MachineSpecificationExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.dto.response.ColumnValue;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.entity.MachineSpecification;
import io.mosip.kernel.masterdata.entity.MachineType;
import io.mosip.kernel.masterdata.entity.id.IdAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.MachineRepository;
import io.mosip.kernel.masterdata.repository.MachineSpecificationRepository;
import io.mosip.kernel.masterdata.repository.MachineTypeRepository;
import io.mosip.kernel.masterdata.service.MachineSpecificationService;
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
 * This class have methods to save a Machine Specification Details
 * 
 * @author Megha Tanga
 * @author Ayush Saxena
 * @since 1.0.0
 *
 */
@Service
public class MachineSpecificationServiceImpl implements MachineSpecificationService {

	/**
	 * Field to hold Machine Repository object
	 */
	@Autowired
	MachineSpecificationRepository machineSpecificationRepository;

	@Autowired
	MachineTypeRepository machineTypeRepository;

	@Autowired
	MachineRepository machineRepository;
	
	@Autowired
	private MasterdataSearchHelper masterdataSearchHelper;
	
	@Autowired
	private FilterTypeValidator filterValidator;
	
	@Autowired
	private MasterDataFilterHelper masterDataFilterHelper;
	
	@Autowired
	private FilterColumnValidator filterColumnValidator;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.MachineSpecificationService#
	 * createMachineSpecification(io.mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public IdAndLanguageCodeID createMachineSpecification(MachineSpecificationDto machineSpecification) {

		MachineSpecification renMachineSpecification = new MachineSpecification();

		MachineSpecification entity = MetaDataUtils.setCreateMetaData(machineSpecification, MachineSpecification.class);
		try {
			renMachineSpecification = machineSpecificationRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_INSERT_EXCEPTION.getErrorCode(),
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_INSERT_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

		IdAndLanguageCodeID idAndLanguageCodeID = new IdAndLanguageCodeID();
		MapperUtils.map(renMachineSpecification, idAndLanguageCodeID);

		return idAndLanguageCodeID;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.MachineSpecificationService#
	 * updateMachineSpecification(io.mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public IdAndLanguageCodeID updateMachineSpecification(MachineSpecificationDto machineSpecification) {
		MachineSpecification updMachineSpecification = null;

		try {
			MachineSpecification renMachineSpecification = machineSpecificationRepository
					.findByIdAndLangCodeIsDeletedFalseorIsDeletedIsNull(machineSpecification.getId(),
							machineSpecification.getLangCode());
			if (renMachineSpecification != null) {

				MetaDataUtils.setUpdateMetaData(machineSpecification, renMachineSpecification, false);
				updMachineSpecification = machineSpecificationRepository.update(renMachineSpecification);
			} else {
				throw new RequestException(
						MachineSpecificationErrorCode.MACHINE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
						MachineSpecificationErrorCode.MACHINE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_UPDATE_EXCEPTION.getErrorCode(),
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_UPDATE_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

		IdAndLanguageCodeID idAndLanguageCodeID = new IdAndLanguageCodeID();
		MapperUtils.map(updMachineSpecification, idAndLanguageCodeID);

		return idAndLanguageCodeID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.MachineSpecificationService#
	 * deleteMachineSpecification(java.lang.String)
	 */
	@Override
	public IdResponseDto deleteMachineSpecification(String id) {
		MachineSpecification delMachineSpecification = null;
		try {
			List<MachineSpecification> renMachineSpecifications = machineSpecificationRepository
					.findByIdAndIsDeletedFalseorIsDeletedIsNull(id);
			if (!renMachineSpecifications.isEmpty()) {
				for (MachineSpecification renMachineSpecification : renMachineSpecifications) {
					List<Machine> renmachineList = machineRepository
							.findMachineBymachineSpecIdAndIsDeletedFalseorIsDeletedIsNull(
									renMachineSpecification.getId());
					if (renmachineList.isEmpty()) {
						MetaDataUtils.setDeleteMetaData(renMachineSpecification);
						delMachineSpecification = machineSpecificationRepository.update(renMachineSpecification);
					} else {
						throw new MasterDataServiceException(
								MachineSpecificationErrorCode.MACHINE_DELETE_DEPENDENCY_EXCEPTION.getErrorCode(),
								MachineSpecificationErrorCode.MACHINE_DELETE_DEPENDENCY_EXCEPTION.getErrorMessage());
					}
				}
			} else {
				throw new RequestException(
						MachineSpecificationErrorCode.MACHINE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
						MachineSpecificationErrorCode.MACHINE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_DELETE_EXCEPTION.getErrorCode(),
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_DELETE_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

		IdResponseDto idResponseDto = new IdResponseDto();
		MapperUtils.map(delMachineSpecification, idResponseDto);
		return idResponseDto;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.MachineSpecificationService#
	 * getAllMachineSpecfication(int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public PageDto<MachineSpecificationExtnDto> getAllMachineSpecfication(int pageNumber, int pageSize, String sortBy,
			String orderBy) {
		List<MachineSpecificationExtnDto> machineSpecs = null;
		PageDto<MachineSpecificationExtnDto> machineSpecificationPages = null;
		try {
			Page<MachineSpecification> pageData = machineSpecificationRepository
					.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Direction.fromString(orderBy), sortBy)));
			if (pageData != null && pageData.getContent() != null && !pageData.getContent().isEmpty()) {
				machineSpecs = MapperUtils.mapAll(pageData.getContent(), MachineSpecificationExtnDto.class);
				machineSpecificationPages = new PageDto<>(pageData.getNumber(), pageData.getTotalPages(),
						pageData.getTotalElements(), machineSpecs);
			} else {
				throw new DataNotFoundException(
						MachineSpecificationErrorCode.MACHINE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorCode(),
						MachineSpecificationErrorCode.MACHINE_SPECIFICATION_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_FETCH_EXCEPTION.getErrorCode(),
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		return machineSpecificationPages;
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.kernel.masterdata.service.MachineSpecificationService#searchMachineSpecification(io.mosip.kernel.masterdata.dto.request.SearchDto)
	 */
	@Override
	public PageResponseDto<MachineSpecificationExtnDto> searchMachineSpecification(SearchDto searchRequestDto) {
		PageResponseDto<MachineSpecificationExtnDto> pageDto = new PageResponseDto<>();
		
		List<MachineSpecificationExtnDto> machineSpecifications = null;
		List<SearchFilter> addList = new ArrayList<>();
		List<SearchFilter> removeList = new ArrayList<>();
		
		for (SearchFilter filter : searchRequestDto.getFilters()) {
			String column = filter.getColumnName();
			
			if (column.equalsIgnoreCase("machineTypeName")) {
				filter.setColumnName(MasterDataConstant.NAME);
				Page<MachineType> machineTypes = masterdataSearchHelper.searchMasterdata(
						MachineType.class,
						new SearchDto(Arrays.asList(filter), Collections.emptyList(), new Pagination(), null),
						null);
				removeList.add(filter);
				addList.addAll(buildMachineTypeSearchFilter(machineTypes.getContent()));
				if (addList.isEmpty()) {
					throw new DataNotFoundException(
							MachineSpecificationErrorCode.MACHINE_ID_NOT_FOUND_FOR_NAME_EXCEPTION.getErrorCode(),
							String.format(MachineSpecificationErrorCode.MACHINE_ID_NOT_FOUND_FOR_NAME_EXCEPTION.getErrorMessage(),
									filter.getValue()));
				}
			}
		}
		searchRequestDto.getFilters().removeAll(removeList);
		
		if (filterValidator.validate(MachineSpecificationExtnDto.class, searchRequestDto.getFilters())) {
			OptionalFilter optionalFilter = new OptionalFilter(addList);
		Page<MachineSpecification> page = masterdataSearchHelper.searchMasterdata(MachineSpecification.class, searchRequestDto, new OptionalFilter[] { optionalFilter });
		if (page.getContent() != null && !page.getContent().isEmpty()) {
			pageDto = PageUtils.pageResponse(page);
			machineSpecifications = MapperUtils.mapAll(page.getContent(), MachineSpecificationExtnDto.class);
			pageDto.setData(machineSpecifications);
		}
		}
		return pageDto;
	}
	
	private List<SearchFilter> buildMachineTypeSearchFilter(List<MachineType> machineTypes) {
		if (machineTypes != null && !machineTypes.isEmpty())
			return machineTypes.stream().filter(Objects::nonNull)
					.map(this::buildMachineType)
					.collect(Collectors.toList());
		return Collections.emptyList();
	}
	
	private SearchFilter buildMachineType(MachineType machineType) {
		SearchFilter filter = new SearchFilter();
		filter.setColumnName("machineTypeCode");
		filter.setType(FilterTypeEnum.EQUALS.name());
		filter.setValue(machineType.getCode());
		return filter;
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.kernel.masterdata.service.MachineSpecificationService#machineSpecificationFilterValues(io.mosip.kernel.masterdata.dto.request.FilterValueDto)
	 */
	@Override
	public FilterResponseDto machineSpecificationFilterValues(FilterValueDto filterValueDto) {
		FilterResponseDto filterResponseDto = new FilterResponseDto();
		List<ColumnValue> columnValueList = new ArrayList<>();
		if (filterColumnValidator.validate(FilterDto.class, filterValueDto.getFilters(), MachineSpecification.class)) {
			for (FilterDto filterDto : filterValueDto.getFilters()) {
				masterDataFilterHelper.filterValues(MachineSpecification.class, filterDto, filterValueDto)
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
