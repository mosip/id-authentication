package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.LocationDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationHierarchyResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.PostLocationCodeResponseDto;

/**
 * Interface class from which various implementation can be performed
 * 
 * @author Srinivasan
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
	 * @param locationRequestDto - location request object
	 * @return {@link PostLocationCodeResponseDto}
	 */
	public PostLocationCodeResponseDto createLocationHierarchy(RequestDto<LocationDto> locationRequestDto);

	/**
	 * @author M1043226
	 * 
	 * @param hierarchyName
	 *            - hierarchyName
	 * @return location response dto
	 */
	public LocationResponseDto getLocationDataByHierarchyName(String hierarchyName);
	
	/**
	 * 
	 * @param locationRequestDto - location request DTO
	 * @return {@link PostLocationCodeResponseDto}
	 */
	public PostLocationCodeResponseDto updateLocationDetails(RequestDto<LocationDto> locationRequestDto);
	
	/**
	 * 
	 * @param locationCode - location code
	 * @return {@link CodeResponseDto}
	 */
	public CodeResponseDto deleteLocationDetials(String locationCode);
	
}
