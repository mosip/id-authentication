package io.mosip.kernel.masterdata.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import io.mosip.kernel.masterdata.constant.LocationErrorCode;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.constant.RegistrationCenterErrorCode;
import io.mosip.kernel.masterdata.dto.getresponse.extn.LocationExtnDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.RegistrationCenterTypeExtnDto;
import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.dto.response.RegistrationCenterSearchDto;
import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.RegistrationCenterType;
import io.mosip.kernel.masterdata.entity.Zone;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.LocationRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterDeviceRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterTypeRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterUserRepository;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;
import io.mosip.kernel.masterdata.validator.FilterTypeValidator;

/**
 * Regsitration Center service helper
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@Component
public class RegistrationCenterServiceHelper {

	@Autowired
	private FilterTypeValidator filterTypeValidator;

	@Autowired
	private MasterdataSearchHelper masterdataSearchHelper;

	@Autowired
	private RegistrationCenterTypeRepository registrationCenterTypeRepository;

	@Autowired
	private RegistrationCenterDeviceRepository registrationCenterDeviceRepository;

	@Autowired
	private RegistrationCenterMachineRepository registrationCenterMachineRepository;

	@Autowired
	private RegistrationCenterUserRepository registrationCenterUserRepository;

	@Autowired
	private LocationUtils locationUtils;

	@Autowired
	private ZoneUtils zoneUtils;

	@Autowired
	private LocationRepository locationRepository;

	/**
	 * Method to search center
	 * 
	 * @param dto
	 *            search inputs
	 * @param locationFilter
	 *            filter to be applied for location
	 * @param zoneFilter
	 *            filter to be applied for zone
	 * @param zones
	 *            list of zones
	 * @param locations
	 *            list of locations
	 * @return list of {@link RegistrationCenterSearchDto} with page Metadata
	 */
	public PageResponseDto<RegistrationCenterSearchDto> searchCenter(SearchDto dto, List<SearchFilter> locationFilter,
			List<SearchFilter> zoneFilter, List<Zone> zones, List<Location> locations) {
		PageResponseDto<RegistrationCenterSearchDto> pageDto = null;
		List<RegistrationCenterSearchDto> registrationCenters = null;
		OptionalFilter optionalFilter = new OptionalFilter(locationFilter);
		OptionalFilter zoneOptionalFilter = new OptionalFilter(zoneFilter);
		Page<RegistrationCenter> page = masterdataSearchHelper.searchMasterdata(RegistrationCenter.class, dto,
				new OptionalFilter[] { optionalFilter, zoneOptionalFilter });
		if (page.getContent() != null && !page.getContent().isEmpty()) {
			pageDto = PageUtils.pageResponse(page);
			registrationCenters = MapperUtils.mapAll(page.getContent(), RegistrationCenterSearchDto.class);
			setCenterMetadata(registrationCenters, locations, zones);
			pageDto.setData(registrationCenters);
		}
		return pageDto;
	}

	/**
	 * Method to fetch entire locations
	 * 
	 * @return list of {@link Location}
	 */
	public List<Location> fetchLocations() {
		List<Location> locations = null;
		try {
			locations = locationRepository.findAllNonDeleted();
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorMessage());
		}
		return locations;
	}

	/**
	 * Method to fetch logged in user zones
	 * 
	 * @param zoneFilter
	 *            zone search inputs
	 * @return list of {@link Zone}
	 */
	public List<Zone> fetchUserZone(List<SearchFilter> zoneFilter) {
		List<Zone> zones;
		zones = zoneUtils.getUserZones();
		if (zones != null && !zones.isEmpty())
			zoneFilter.addAll(buildZoneFilter(zones));
		else
			throw new MasterDataServiceException(RegistrationCenterErrorCode.USER_ZONE_NOT_FOUND.getErrorCode(),
					RegistrationCenterErrorCode.USER_ZONE_NOT_FOUND.getErrorMessage());
		return zones;
	}

	/**
	 * Method to fetch registration center type
	 * 
	 * @param addList
	 *            filter to be added for further operation
	 * @param removeList
	 *            filter to be removed from further operations
	 * @param filter
	 *            list of request search filters
	 */
	public void centerTypeSearch(List<SearchFilter> addList, List<SearchFilter> removeList, SearchFilter filter) {
		filter.setColumnName(MasterDataConstant.NAME);
		if (filterTypeValidator.validate(RegistrationCenterTypeExtnDto.class, Arrays.asList(filter))) {
			Page<RegistrationCenterType> regtypes = masterdataSearchHelper.searchMasterdata(
					RegistrationCenterType.class,
					new SearchDto(Arrays.asList(filter), Collections.emptyList(), new Pagination(), null), null);
			if (regtypes.hasContent()) {
				removeList.add(filter);
				addList.addAll(buildRegistrationCenterTypeSearchFilter(regtypes.getContent()));
			} else {
				throw new MasterDataServiceException(RegistrationCenterErrorCode.NO_CENTERTYPE_AVAILABLE.getErrorCode(),
						String.format(RegistrationCenterErrorCode.NO_CENTERTYPE_AVAILABLE.getErrorMessage(),
								filter.getValue()));
			}
		}
	}

	/**
	 * Method to search the location based the filter.
	 * 
	 * @param filter
	 *            input for search
	 * @return {@link Location}
	 */
	public Location locationSearch(SearchFilter filter) {
		SearchFilter filter2 = new SearchFilter();
		filter2.setColumnName(MasterDataConstant.HIERARCHY_LEVEL);
		filter2.setType(FilterTypeEnum.EQUALS.name());
		filter2.setValue(getHierarchyLevel(filter.getColumnName()));
		filter.setColumnName(MasterDataConstant.NAME);
		if (filterTypeValidator.validate(LocationExtnDto.class, Arrays.asList(filter, filter2))) {
			Page<Location> locations = masterdataSearchHelper.searchMasterdata(Location.class,
					new SearchDto(Arrays.asList(filter, filter2), Collections.emptyList(), new Pagination(), null),
					null);
			if (locations.hasContent()) {
				return locations.getContent().get(0);
			} else {
				throw new MasterDataServiceException(
						RegistrationCenterErrorCode.NO_LOCATION_DATA_AVAILABLE.getErrorCode(),
						String.format(RegistrationCenterErrorCode.NO_LOCATION_DATA_AVAILABLE.getErrorMessage(),
								filter.getValue()));
			}
		}
		return null;
	}

	/**
	 * Method to find out the hierrachy level from the column name
	 * 
	 * @param columnName
	 *            input column name
	 * @return hierarchy level
	 */
	public String getHierarchyLevel(String columnName) {
		if (columnName != null) {
			switch (columnName.toLowerCase()) {
			case MasterDataConstant.POSTAL_CODE:
				return "5";
			case MasterDataConstant.LAA:
				return "4";
			case MasterDataConstant.CITY:
				return "3";
			case MasterDataConstant.PROVINCE:
				return "2";
			case MasterDataConstant.REGION:
				return "1";
			default:
				return "0";
			}
		}
		return "0";
	}

	/**
	 * Method to prepare search filters based on the registration center type code
	 * list passed
	 * 
	 * @param regCenterTypes
	 *            list of registration center types
	 * @return list of search filters
	 */
	private List<SearchFilter> buildRegistrationCenterTypeSearchFilter(List<RegistrationCenterType> regCenterTypes) {
		if (regCenterTypes != null && !regCenterTypes.isEmpty())
			return regCenterTypes.stream().filter(Objects::nonNull).map(this::buildRegCenterType)
					.collect(Collectors.toList());
		return Collections.emptyList();
	}

	/**
	 * Method to prepare search filters based on the location code list passed.
	 * 
	 * @param location
	 *            list of location codes
	 * @return list of search filter
	 */
	public List<SearchFilter> buildLocationSearchFilter(List<Location> location) {
		if (location != null && !location.isEmpty())
			return location.stream().filter(Objects::nonNull).map(Location::getCode).map(this::buildLocationFilter)
					.collect(Collectors.toList());
		return Collections.emptyList();
	}

	/**
	 * Method to build search filter by the registration center type id to fetch the
	 * exact registration center
	 * 
	 * @param centerType
	 *            request registration center
	 * @return search filter
	 */
	private SearchFilter buildRegCenterType(RegistrationCenterType centerType) {
		SearchFilter filter = new SearchFilter();
		filter.setColumnName(MasterDataConstant.CENTERTYPECODE);
		filter.setType(FilterTypeEnum.EQUALS.name());
		filter.setValue(centerType.getCode());
		return filter;
	}

	/**
	 * Method to build location search filter
	 * 
	 * @param location
	 *            search filter
	 * @return search filter
	 */
	private SearchFilter buildLocationFilter(String location) {
		SearchFilter filter = new SearchFilter();
		filter.setColumnName(MasterDataConstant.CENTERLOCCODE);
		filter.setType(FilterTypeEnum.EQUALS.name());
		filter.setValue(location);
		return filter;
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
	 * Method to check whether columnName is belong to location
	 * 
	 * @param filter
	 *            to search the column name
	 * @return true if column is location type false otherwise
	 */
	public boolean isLocationSearch(String filter) {
		switch (filter.toLowerCase()) {
		case MasterDataConstant.CITY:
			return true;
		case MasterDataConstant.PROVINCE:
			return true;
		case MasterDataConstant.REGION:
			return true;
		case MasterDataConstant.LAA:
			return true;
		case MasterDataConstant.POSTAL_CODE:
			return true;
		default:
			return false;
		}

	}

	/**
	 * Method to fetch no. of machines for the registration center and set the
	 * response to registration center response dto
	 * 
	 * @param dto
	 *            response to be mapped
	 * @return true if successful otherwise exception
	 */

	public void setCenterMetadata(List<RegistrationCenterSearchDto> list, List<Location> locations, List<Zone> zones) {
		list.parallelStream().filter(this::setDevices).filter(this::setMachines).filter(this::setRegistrationCenterType)
				.filter(this::setUsers).filter(i -> setHolidayMetadata(i, locations))
				.filter(i -> setLocationMetadata(i, locations)).forEach(i -> setZoneMetadata(i, zones));
	}

	/**
	 * Method to set Zone metadata
	 * 
	 * @param centers
	 *            metadata to be added
	 * @param zones
	 *            list of zones
	 * 
	 */
	private void setZoneMetadata(RegistrationCenterSearchDto centers, List<Zone> zones) {
		Optional<Zone> zone = zones.stream()
				.filter(i -> i.getCode().equals(centers.getZoneCode()) && i.getLangCode().equals(centers.getLangCode()))
				.findFirst();
		if (zone.isPresent()) {
			centers.setZone(zone.get().getName());
		}
	}

	/**
	 * Method to fetch no. of devices for the registration center and set the
	 * response to registration center response dto
	 * 
	 * @param dto
	 *            response to be mapped
	 * @return true if successful otherwise exception
	 */
	private boolean setDevices(RegistrationCenterSearchDto centerDto) {
		try {
			long devices = registrationCenterDeviceRepository.countCenterDevices(centerDto.getId());
			centerDto.setDevices(devices);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterErrorCode.REGISTRATION_CENTER_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterErrorCode.REGISTRATION_CENTER_FETCH_EXCEPTION.getErrorMessage(), e);
		}
		return true;
	}

	/**
	 * Method to fetch no. of machines for the registration center and set the
	 * response to registration center response dto
	 * 
	 * @param dto
	 *            response to be mapped
	 * @return true if successful otherwise exception
	 */
	private boolean setMachines(RegistrationCenterSearchDto centerDto) {
		try {
			long machines = registrationCenterMachineRepository.countCenterMachines(centerDto.getId());
			centerDto.setMachines(machines);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterErrorCode.REGISTRATION_CENTER_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterErrorCode.REGISTRATION_CENTER_FETCH_EXCEPTION.getErrorMessage(), e);
		}
		return true;
	}

	/**
	 * Method to fetch no. of machines for the registration center and set the
	 * response to registration center response dto
	 * 
	 * @param dto
	 *            response to be mapped
	 * @return true if successful otherwise exception
	 */
	private boolean setUsers(RegistrationCenterSearchDto centerDto) {
		try {
			long users = registrationCenterUserRepository.countCenterUsers(centerDto.getId());
			centerDto.setUsers(users);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterErrorCode.REGISTRATION_CENTER_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterErrorCode.REGISTRATION_CENTER_FETCH_EXCEPTION.getErrorMessage(), e);
		}
		return true;
	}

	/**
	 * Setting Location metadata for the received center
	 * 
	 * @param center
	 *            input for location metadata to be set
	 * @param locations
	 *            contains the location information
	 * @return true if successful
	 */
	private boolean setLocationMetadata(RegistrationCenterSearchDto center, List<Location> locations) {
		Optional<Location> optional = locations.parallelStream().filter(
				i -> i.getCode().equals(center.getLocationCode()) && i.getLangCode().equals(center.getLangCode()))
				.findFirst();
		if (optional.isPresent()) {
			Location location = optional.get();
			List<Location> list = locationUtils.getAncestors(locations, location);
			if (list != null && !list.isEmpty()) {
				for (Location l : list) {
					short level = l.getHierarchyLevel();
					switch (level) {
					case 3:
						center.setCity(l.getName());
						break;
					case 2:
						center.setProvince(l.getName());
						break;
					case 1:
						center.setRegion(l.getName());
						break;
					case 5:
						center.setPostalCode(l.getName());
						break;
					case 4:
						center.setLocalAdminAuthority(l.getName());
						break;
					default:
						break;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Method to fetch registration center type and set the response to registration
	 * center response dto
	 * 
	 * @param dto
	 *            response to be mapped
	 * @return true if successful otherwise exception
	 */
	private boolean setRegistrationCenterType(RegistrationCenterSearchDto dto) {
		try {
			RegistrationCenterType centerType = registrationCenterTypeRepository
					.findByCodeAndLangCode(dto.getCenterTypeCode(), dto.getLangCode());
			dto.setCenterTypeName(centerType.getName());
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterErrorCode.REGISTRATION_CENTER_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterErrorCode.REGISTRATION_CENTER_FETCH_EXCEPTION.getErrorMessage(), e);
		}
		return true;
	}

	/**
	 * Search the zone in the based on the received inpu filter
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
			throw new MasterDataServiceException(RegistrationCenterErrorCode.NO_ZONE_AVAILABLE.getErrorCode(),
					String.format(RegistrationCenterErrorCode.NO_ZONE_AVAILABLE.getErrorMessage(), filter.getValue()));
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
	 * Setting location holiday metadata
	 * 
	 * @param center
	 *            holiday information to be added
	 * @param locations
	 *            fetch the name of the holiday location name
	 * @return true if successful
	 */
	private boolean setHolidayMetadata(RegistrationCenterSearchDto center, List<Location> locations) {
		if (locations != null && !locations.isEmpty()) {
			Optional<Location> location = locations.stream()
					.filter(i -> center.getHolidayLocationCode().equals(i.getCode())
							&& center.getLangCode().equals(i.getLangCode()))
					.findFirst();
			if (location.isPresent()) {
				center.setHolidayLocation(location.get().getName());
			}
		}
		return true;
	}

}
