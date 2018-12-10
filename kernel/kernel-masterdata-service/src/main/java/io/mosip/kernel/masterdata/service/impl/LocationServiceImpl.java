package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.LocationErrorCode;
import io.mosip.kernel.masterdata.dto.LocationCodeDto;
import io.mosip.kernel.masterdata.dto.LocationDto;
import io.mosip.kernel.masterdata.dto.LocationHierarchyDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationHierarchyResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationResponseDto;
import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.LocationRepository;
import io.mosip.kernel.masterdata.service.LocationService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * Class will fetch Location details based on various parameters this class is
 * implemented from {@link LocationService}}
 * 
 * @author Srinivasan
 *
 */
@Service
public class LocationServiceImpl implements LocationService {

	/**
	 * creates an instance of repository class {@link LocationRepository}}
	 */
	@Autowired
	LocationRepository locationRepository;

	private List<Location> childHierarchyList = null;
	private List<Location> parentHierarchyList = null;

	/**
	 * This method will all location details from the Database. Refers to
	 * {@link LocationRepository} for fetching location hierarchy
	 */
	@Override
	public LocationHierarchyResponseDto getLocationDetails(String langCode) {
		List<LocationHierarchyDto> responseList = null;
		LocationHierarchyResponseDto locationHierarchyResponseDto = new LocationHierarchyResponseDto();
		List<Object[]> locations = null;
		try {

			locations = locationRepository.findDistinctLocationHierarchyByIsDeletedFalse(langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}
		if (!locations.isEmpty()) {

			responseList = MapperUtils.objectToDtoConverter(locations);
		} else {
			throw new DataNotFoundException(LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		locationHierarchyResponseDto.setLocations(responseList);
		return locationHierarchyResponseDto;
	}

	/**
	 * This method will fetch location hierarchy based on location code and language
	 * code Refers to {@link LocationRepository} for fetching location hierarchy
	 * 
	 * @param locCode
	 * @param langcode
	 * @return LocationHierarchyResponseDto-List<LocationHierachy>
	 */
	@Override
	public LocationResponseDto getLocationHierarchyByLangCode(String locCode, String langCode) {
		List<Location> childList = null;
		List<Location> parentList = null;
		childHierarchyList = new ArrayList<>();
		parentHierarchyList = new ArrayList<>();
		LocationResponseDto locationHierarchyResponseDto = new LocationResponseDto();
		try {

			List<Location> locHierList = getLocationHierarchyList(locCode, langCode);
			if (locHierList != null && !locHierList.isEmpty()) {
				for (Location locationHierarchy : locHierList) {
					String currentParentLocCode = locationHierarchy.getParentLocCode();
					childList = getChildList(locCode, langCode);
					parentList = getParentList(currentParentLocCode, langCode);

				}
				locHierList.addAll(childList);
				locHierList.addAll(parentList);
				// List<LocationDto> locationHierarchies = MapperUtils.mapAll(locHierList,
				// LocationDto.class);
				List<LocationDto> locationHierarchies = MapperUtils.mapAll(locHierList, LocationDto.class);
				locationHierarchyResponseDto.setLocations(locationHierarchies);

			} else {
				throw new DataNotFoundException(LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorCode(),
						LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		}

		catch (DataAccessException e) {

			throw new MasterDataServiceException(LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));

		}
		return locationHierarchyResponseDto;
	}

	/**
	 * fetches location hierarchy details from database based on location code and
	 * language code
	 * 
	 * @param locCode
	 * @param langCode
	 * @return List<LocationHierarchy>
	 */
	private List<Location> getLocationHierarchyList(String locCode, String langCode) {

		return locationRepository.findLocationHierarchyByCodeAndLanguageCode(locCode, langCode);
	}

	/**
	 * fetches location hierarchy details from database based on parent location
	 * code and language code
	 * 
	 * @param locCode
	 * @param langCode
	 * @return List<LocationHierarchy>
	 */
	private List<Location> getLocationChildHierarchyList(String locCode, String langCode) {

		return locationRepository.findLocationHierarchyByParentLocCodeAndLanguageCode(locCode, langCode);

	}

	/**
	 * This method fetches child hierachy details of the location based on location
	 * code
	 * 
	 * @param locCode
	 * @param langCode
	 * @return
	 */
	private List<Location> getChildList(String locCode, String langCode) {

		if (locCode != null && !locCode.isEmpty()) {
			List<Location> childLocHierList = getLocationChildHierarchyList(locCode, langCode);
			childHierarchyList.addAll(childLocHierList);
			childLocHierList.parallelStream().filter(entity -> entity.getCode() != null && !entity.getCode().isEmpty())
					.map(entity -> getChildList(entity.getCode(), langCode)).collect(Collectors.toList());
		}

		return childHierarchyList;
	}

	/**
	 * This method fetches parent hierachy details of the location based on parent
	 * Location code
	 * 
	 * @param locCode
	 * @param langCode
	 * @return List<LocationHierarcy>
	 */
	private List<Location> getParentList(String locCode, String langCode) {

		if (locCode != null && !locCode.isEmpty()) {
			List<Location> parentLocHierList = getLocationHierarchyList(locCode, langCode);
			parentHierarchyList.addAll(parentLocHierList);

			parentLocHierList.parallelStream()
					.filter(entity -> entity.getParentLocCode() != null && !entity.getParentLocCode().isEmpty())
					.map(entity -> getParentList(entity.getParentLocCode(), langCode)).collect(Collectors.toList());
		}

		return parentHierarchyList;
	}

	/**
	 * Method creates location hierarchy data into the table based on the request
	 * parameter sent {@inheritDoc}
	 */
	@Override
	public LocationCodeDto createLocationHierarchy(RequestDto<LocationDto> locationRequestDto) {

		Location location = null;
		Location locationResultantEntity = null;
		LocationCodeDto locationCodeDto = null;

		location = MetaDataUtils.setCreateMetaData(locationRequestDto.getRequest(), Location.class);
		try {
			locationResultantEntity = locationRepository.create(location);
		} catch (DataAccessLayerException | DataAccessException ex) {
			throw new MasterDataServiceException(LocationErrorCode.LOCATION_INSERT_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_INSERT_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(ex));
		}

		// locationCodeDto = MapperUtils.map(locationResultantEntity,
		// LocationCodeDto.class);
		locationCodeDto = MapperUtils.map(locationResultantEntity, LocationCodeDto.class);
		return locationCodeDto;
	}

}
