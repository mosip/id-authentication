package io.mosip.kernel.masterdata.service;

import java.util.List;
import java.util.Map;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.LocationDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationHierarchyResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.StatusResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.LocationExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.PostLocationCodeResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.LocationSearchDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.Location;

/**
 * Interface class from which various implementation can be performed
 * 
 * @author Srinivasan
 * @author Tapaswini
 *
 */
public interface LocationService {

	/**
	 * this method will fetch LocationHierarchyDetails
	 * 
	 * @param langCode
	 *            - language code
	 * @return LocationHierarchyResponseDto -location response
	 */
	public LocationHierarchyResponseDto getLocationDetails(String langCode);

	/**
	 * 
	 * @param locCode
	 *            - location code
	 * @param langCode
	 *            - language code
	 * @return location response dto
	 */
	public LocationResponseDto getLocationHierarchyByLangCode(String locCode, String langCode);

	/**
	 * 
	 * @param locationDto
	 *            - location request object
	 * @return {@link PostLocationCodeResponseDto}
	 */
	public ResponseWrapper<PostLocationCodeResponseDto> createLocation(
			LocationDto locationDto);


	/**
	 * 
	 * @param hierarchyName
	 *            - hierarchyName
	 * @return location response dto
	 */
	public LocationResponseDto getLocationDataByHierarchyName(String hierarchyName);

	/**
	 * 
	 * @param locationRequestDto
	 *            - location request DTO
	 * @return {@link PostLocationCodeResponseDto}
	 */
	public PostLocationCodeResponseDto updateLocationDetails(LocationDto locationRequestDto);

	/**
	 * 
	 * @param locationCode
	 *            - location code
	 * @return {@link CodeResponseDto}
	 */
	public CodeResponseDto deleteLocationDetials(String locationCode);

	/**
	 * 
	 * @param locCode
	 *            - location code
	 * @param langCode
	 *            - language code
	 * @return {@link LocationResponseDto}
	 */
	public LocationResponseDto getImmediateChildrenByLocCodeAndLangCode(String locCode, String langCode);

	/**
	 * 
	 * @param langCode
	 *            - language code
	 * @param hierarchyLevel
	 *            - hierarchyLevel
	 * @return map contain key as parentCode and value as List of Location
	 * 
	 */
	public Map<Short, List<Location>> getLocationByLangCodeAndHierarchyLevel(String langCode, Short hierarchyLevel);

	/**
	 * checks whether the given location name is valid or not
	 * 
	 * @param locationName
	 *            -location name
	 * @return {@link StatusResponseDto}
	 */
	public StatusResponseDto validateLocationName(String locationName);

	/**
	 * Method to fetch all the locations
	 * 
	 * @param pageNumber
	 *            the page number
	 * @param pageSize
	 *            the size of each page
	 * @param sortBy
	 *            the attributes by which it should be ordered
	 * @param orderBy
	 *            the order to be used
	 * 
	 * @return the response i.e. pages containing the locations
	 */
	public PageDto<LocationExtnDto> getLocations(int pageNumber, int pageSize, String sortBy, String orderBy);

	/**
	 * This method fetches child hierarchy details of the location based on location
	 * code, here child isActive can true or false
	 * 
	 * @param locCode
	 *            - location code
	 * @return List<Location>
	 */
	public List<String> getChildList(String locCode);

	/**
	 * Service method to search location
	 * 
	 * @param dto
	 *            input from user
	 * @return response dto containing location values
	 */
	public PageResponseDto<LocationSearchDto> searchLocation(SearchDto dto);

	/**
	 * Service method to filter location values
	 * 
	 * @param filterValueDto
	 * @return names corresponding to the eneted filter dto
	 */
	public FilterResponseDto locationFilterValues(FilterValueDto filterValueDto);
}
