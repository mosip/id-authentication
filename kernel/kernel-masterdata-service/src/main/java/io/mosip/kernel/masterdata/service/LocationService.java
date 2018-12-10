package io.mosip.kernel.masterdata.service;


import io.mosip.kernel.masterdata.dto.LocationDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationHierarchyResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.PostLocationCodeResponseDto;

/**
 * Interface class from which various implementation can be performed
 * @author Srinivasan
 *
 */
public interface LocationService {

	/**
	 * this method will fetch LocationHierarchyDetails
	 * 
	 * @param locationHierarchyDTO
	 * @return
	 */
	public LocationHierarchyResponseDto getLocationDetails(String langCode);
	
	/**
	 * 
	 * @param locationHierarchyDto
	 * @return
	 */
	public LocationResponseDto getLocationHierarchyByLangCode(String locCode,String langCode);
	
	/**
	 * 
	 * @return 
	 */
	public PostLocationCodeResponseDto createLocationHierarchy(RequestDto<LocationDto> locationRequestDto);
}
