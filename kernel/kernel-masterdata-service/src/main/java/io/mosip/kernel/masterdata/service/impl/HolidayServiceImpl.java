package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.masterdata.constant.HolidayErrorCode;
import io.mosip.kernel.masterdata.constant.LocationErrorCode;
import io.mosip.kernel.masterdata.dto.HolidayDto;
import io.mosip.kernel.masterdata.dto.HolidayIDDto;
import io.mosip.kernel.masterdata.dto.HolidayIdDeleteDto;
import io.mosip.kernel.masterdata.dto.HolidayUpdateDto;
import io.mosip.kernel.masterdata.dto.LocationDto;
import io.mosip.kernel.masterdata.dto.getresponse.HolidayResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.HolidayExtnDto;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.dto.request.SearchSort;
import io.mosip.kernel.masterdata.dto.response.ColumnValue;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.HolidaySearchDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.Holiday;
import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.HolidayRepository;
import io.mosip.kernel.masterdata.repository.LocationRepository;
import io.mosip.kernel.masterdata.service.HolidayService;
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
 * Service Impl class for Holiday Data
 * 
 * @author Sidhant Agarwal
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@Service
public class HolidayServiceImpl implements HolidayService {
	@Autowired
	private HolidayRepository holidayRepository;
	@Autowired
	private MasterdataSearchHelper masterdataSearchHelper;
	@Autowired
	private FilterTypeValidator filterValidator;
	@Autowired
	private LocationRepository locationRepository;
	@Autowired
	private FilterColumnValidator filterColumnValidator;
	@Autowired
	private MasterDataFilterHelper masterDataFilterHelper;
	@Autowired
	private PageUtils pageUtils;

	private static final String UPDATE_HOLIDAY_QUERY = "UPDATE Holiday h SET h.isActive = :isActive ,h.updatedBy = :updatedBy , h.updatedDateTime = :updatedDateTime, h.holidayDesc = :holidayDesc,h.holidayId.holidayDate=:newHolidayDate,h.holidayId.holidayName = :newHolidayName   WHERE h.holidayId.locationCode = :locationCode and h.holidayId.holidayName = :holidayName and h.holidayId.holidayDate = :holidayDate and h.holidayId.langCode = :langCode and (h.isDeleted is null or h.isDeleted = false)";

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.HolidayService#getAllHolidays()
	 */
	@Override
	public HolidayResponseDto getAllHolidays() {
		HolidayResponseDto holidayResponseDto = null;
		List<HolidayDto> holidayDto = null;
		List<Holiday> holidays = null;
		try {
			holidays = holidayRepository.findAllNonDeletedHoliday();
		} catch (DataAccessException | DataAccessLayerException dataAccessException) {
			throw new MasterDataServiceException(HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorCode(),
					HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorMessage());
		}

		if (holidays != null && !holidays.isEmpty()) {
			holidayDto = MapperUtils.mapHolidays(holidays);
		} else {
			throw new DataNotFoundException(HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorCode(),
					HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorMessage());
		}

		holidayResponseDto = new HolidayResponseDto();
		holidayResponseDto.setHolidays(holidayDto);
		return holidayResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.HolidayService#getHolidayById(int)
	 */
	@Override
	public HolidayResponseDto getHolidayById(int id) {

		HolidayResponseDto holidayResponseDto = null;
		List<HolidayDto> holidayDto = null;
		List<Holiday> holidays = null;
		try {
			holidays = holidayRepository.findAllById(id);
		} catch (DataAccessException | DataAccessLayerException dataAccessException) {
			throw new MasterDataServiceException(HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorCode(),
					HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorMessage());
		}

		if (holidays != null && !holidays.isEmpty()) {
			holidayDto = MapperUtils.mapHolidays(holidays);
		} else {
			throw new DataNotFoundException(HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorCode(),
					HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorMessage());
		}

		holidayResponseDto = new HolidayResponseDto();
		holidayResponseDto.setHolidays(holidayDto);
		return holidayResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.HolidayService#
	 * getHolidayByIdAndLanguageCode(int, java.lang.String)
	 */
	@Override
	public HolidayResponseDto getHolidayByIdAndLanguageCode(int id, String langCode) {
		HolidayResponseDto holidayResponseDto = null;
		List<HolidayDto> holidayList = null;
		List<Holiday> holidays = null;
		try {
			holidays = holidayRepository.findHolidayByIdAndHolidayIdLangCode(id, langCode);
		} catch (DataAccessException | DataAccessLayerException dataAccessException) {
			throw new MasterDataServiceException(HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorCode(),
					HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorMessage());
		}

		if (holidays != null && !holidays.isEmpty()) {
			holidayList = MapperUtils.mapHolidays(holidays);
		} else {
			throw new DataNotFoundException(HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorCode(),
					HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorMessage());
		}
		holidayResponseDto = new HolidayResponseDto();
		holidayResponseDto.setHolidays(holidayList);
		return holidayResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.HolidayService#saveHoliday(io.mosip.kernel
	 * .masterdata.dto.RequestDto)
	 */
	@Override
	public HolidayIDDto saveHoliday(HolidayDto holidayDto) {
		Holiday entity = MetaDataUtils.setCreateMetaData(holidayDto, Holiday.class);
		entity.setHolidayId(holidayDto.getId());
		Holiday holiday;
		try {
			holiday = holidayRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(HolidayErrorCode.HOLIDAY_INSERT_EXCEPTION.getErrorCode(),
					ExceptionUtils.parseException(e));
		}
		HolidayIDDto holidayId = new HolidayIDDto();
		MapperUtils.map(holiday, holidayId);
		return holidayId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.HolidayService#updateHoliday(io.mosip.
	 * kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public HolidayIDDto updateHoliday(HolidayUpdateDto holidayDto) {
		HolidayIDDto idDto = null;
		Map<String, Object> params = bindDtoToMap(holidayDto);
		try {
			int noOfRowAffected = holidayRepository.createQueryUpdateOrDelete(UPDATE_HOLIDAY_QUERY, params);
			if (noOfRowAffected != 0)
				idDto = mapToHolidayIdDto(holidayDto);
			else
				throw new RequestException(HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorCode(),
						HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorMessage());

		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(HolidayErrorCode.HOLIDAY_UPDATE_EXCEPTION.getErrorCode(),
					HolidayErrorCode.HOLIDAY_UPDATE_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}
		return idDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.HolidayService#deleteHoliday(io.mosip.
	 * kernel.masterdata.entity.id.HolidayID)
	 */
	@Override
	public HolidayIdDeleteDto deleteHoliday(RequestWrapper<HolidayIdDeleteDto> request) {
		HolidayIdDeleteDto idDto = request.getRequest();
		try {
			int affectedRows = holidayRepository.deleteHolidays(LocalDateTime.now(ZoneId.of("UTC")),
					idDto.getHolidayName(), idDto.getHolidayDate(), idDto.getLocationCode());
			if (affectedRows == 0)
				throw new RequestException(HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorCode(),
						HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorMessage());

		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(HolidayErrorCode.HOLIDAY_DELETE_EXCEPTION.getErrorCode(),
					HolidayErrorCode.HOLIDAY_DELETE_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}
		return idDto;
	}

	/**
	 * Bind {@link HolidayUpdateDto} dto to {@link Map}
	 * 
	 * @param dto
	 *            input {@link HolidayUpdateDto}
	 * @return {@link Map} with the named parameter and value
	 */
	private Map<String, Object> bindDtoToMap(HolidayUpdateDto dto) {
		Map<String, Object> params = new HashMap<>();
		if (dto.getNewHolidayName() != null && !dto.getNewHolidayName().isEmpty())
			params.put("newHolidayName", dto.getNewHolidayName());
		else
			params.put("newHolidayName", dto.getHolidayName());
		if (dto.getNewHolidayDate() != null)
			params.put("newHolidayDate", dto.getNewHolidayDate());
		else
			params.put("newHolidayDate", dto.getHolidayDate());
		if (dto.getNewHolidayDesc() != null && !dto.getNewHolidayDesc().isEmpty())
			params.put("holidayDesc", dto.getNewHolidayDesc());
		else
			params.put("holidayDesc", dto.getHolidayDesc());

		params.put("isActive", dto.getIsActive());
		params.put("holidayDate", dto.getHolidayDate());
		params.put("holidayName", dto.getHolidayName());
		params.put("updatedBy", MetaDataUtils.getContextUser());
		params.put("updatedDateTime", LocalDateTime.now(ZoneId.of("UTC")));
		params.put("locationCode", dto.getLocationCode());
		params.put("langCode", dto.getLangCode());
		return params;
	}

	/**
	 * Bind the {@link HolidayUpdateDto} to {@link HolidayIDDto}
	 * 
	 * @param dto
	 *            input {@link HolidayUpdateDto} to be bind
	 * @return {@link HolidayIDDto} holiday id
	 */
	private HolidayIDDto mapToHolidayIdDto(HolidayUpdateDto dto) {
		HolidayIDDto idDto;
		idDto = new HolidayIDDto();
		if (dto.getNewHolidayName() != null)
			idDto.setHolidayName(dto.getNewHolidayName());
		else
			idDto.setHolidayName(dto.getHolidayName());
		if (dto.getNewHolidayDate() != null)
			idDto.setHolidayDate(dto.getNewHolidayDate());
		else
			idDto.setHolidayDate(dto.getHolidayDate());
		idDto.setLocationCode(dto.getLocationCode());
		idDto.setLangCode(dto.getLangCode());
		return idDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.HolidayService#getHolidays(int, int,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public PageDto<HolidayExtnDto> getHolidays(int pageNumber, int pageSize, String sortBy, String orderBy) {
		List<HolidayExtnDto> holidays = null;
		PageDto<HolidayExtnDto> pageDto = null;
		try {
			Page<Holiday> pageData = holidayRepository
					.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Direction.fromString(orderBy), sortBy)));
			if (pageData != null && pageData.getContent() != null && !pageData.getContent().isEmpty()) {
				holidays = MapperUtils.mapAll(pageData.getContent(), HolidayExtnDto.class);
				pageDto = new PageDto<>(pageData.getNumber(), pageData.getTotalPages(), pageData.getTotalElements(),
						holidays);
			} else {
				throw new DataNotFoundException(HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorCode(),
						HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorMessage());
			}
		} catch (DataAccessException | DataAccessLayerException dataAccessException) {
			throw new MasterDataServiceException(HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorCode(),
					HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorMessage());
		}
		return pageDto;
	}

	@Override
	public PageResponseDto<HolidaySearchDto> searchHolidays(SearchDto dto) {
		PageResponseDto<HolidaySearchDto> pageDto = new PageResponseDto<>();
		List<HolidayExtnDto> holidayDtos = null;
		List<SearchFilter> addList = new ArrayList<>();
		List<SearchFilter> removeList = new ArrayList<>();
		List<Location> locationList = null;
		try {
			locationList = locationRepository.findByLangCode(dto.getLanguageCode());
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}
		if (!locationList.isEmpty()) {
			for (SearchFilter filter : dto.getFilters()) {
				String column = filter.getColumnName();
				if (column.equalsIgnoreCase("name")) {
					if (filterValidator.validate(LocationDto.class, Arrays.asList(filter))) {
						Page<Location> locations = masterdataSearchHelper.searchMasterdata(Location.class,
								new SearchDto(Arrays.asList(filter), Collections.emptyList(), new Pagination(), null),
								null);
						List<SearchFilter> locationCodeFilter = buildLocationSearchFilter(locations.getContent());
						if (locationCodeFilter.isEmpty()) {
							throw new DataNotFoundException(
									LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorCode(),
									LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorMessage());
						}
						addList.addAll(locationCodeFilter);
						removeList.add(filter);
					}
				}
			}
			dto.getFilters().removeAll(removeList);
			Pagination pagination = dto.getPagination();
			List<SearchSort> sort = dto.getSort();
			dto.setPagination(new Pagination(0, Integer.MAX_VALUE));
			dto.setSort(Collections.emptyList());
			List<HolidaySearchDto> resultDto = new ArrayList<>();
			if (filterValidator.validate(HolidaySearchDto.class, dto.getFilters())) {
				OptionalFilter optionalFilter = new OptionalFilter(addList);
				Page<Holiday> page = masterdataSearchHelper.searchMasterdata(Holiday.class, dto,
						new OptionalFilter[] { optionalFilter });
				if (page.getContent() != null && !page.getContent().isEmpty()) {
					holidayDtos = MapperUtils.mapAll(page.getContent(), HolidayExtnDto.class);
					Map<Integer, List<HolidayExtnDto>> holidayPerHolidayType = holidayDtos.stream()
							.collect(Collectors.groupingBy(HolidayExtnDto::getHolidayId));
					for (Map.Entry<Integer, List<HolidayExtnDto>> entry : holidayPerHolidayType.entrySet()) {
						HolidaySearchDto holidaySearchDto = new HolidaySearchDto();
						setMetaData(entry.getValue(), locationList, holidaySearchDto);
						MapperUtils.map(entry.getValue().get(0), holidaySearchDto);
						resultDto.add(holidaySearchDto);
					}
				}
				pageDto = pageUtils.sortPage(resultDto, sort, pagination);
			}
		} else {
			throw new DataNotFoundException(LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		return pageDto;
	}

	@Override
	public FilterResponseDto holidaysFilterValues(FilterValueDto filterValueDto) {
		FilterResponseDto filterResponseDto = new FilterResponseDto();
		List<ColumnValue> columnValueList = new ArrayList<>();
		if (filterColumnValidator.validate(FilterDto.class, filterValueDto.getFilters(), Machine.class)) {
			for (FilterDto filterDto : filterValueDto.getFilters()) {
				List<?> filterValues = masterDataFilterHelper.filterValues(Holiday.class, filterDto, filterValueDto);
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

	/**
	 * This method return Machine Types list filters.
	 * 
	 * @param machineTypes
	 *            the list of Machine Type.
	 * @return the list of {@link SearchFilter}.
	 */
	private List<SearchFilter> buildLocationSearchFilter(List<Location> Locations) {
		if (Locations != null && !Locations.isEmpty())
			return Locations.stream().filter(Objects::nonNull).map(this::buildLocations).collect(Collectors.toList());
		return Collections.emptyList();
	}

	/**
	 * This method provide search filter for provided Machine Type.
	 * 
	 * @param machineType
	 *            the machine type.
	 * @return the {@link SearchFilter}.
	 */
	private SearchFilter buildLocations(Location location) {
		SearchFilter filter = new SearchFilter();
		filter.setColumnName("locationCode");
		filter.setType(FilterTypeEnum.EQUALS.name());
		filter.setValue(location.getCode());
		return filter;
	}

	private static void setMetaData(List<HolidayExtnDto> holidays, List<Location> locations,
			HolidaySearchDto searchDto) {
		Set<String> holidayLocations = holidays.stream().map(HolidayExtnDto::getLocationCode)
				.collect(Collectors.toSet());
		if (!holidayLocations.isEmpty()) {
			String locationNames = locations.stream().filter(i -> holidayLocations.contains(i.getCode()))
					.map(Location::getName).collect(Collectors.joining(","));

			searchDto.setName(locationNames);
		}
	}

}
