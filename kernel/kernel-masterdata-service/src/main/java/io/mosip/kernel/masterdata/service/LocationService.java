package io.mosip.kernel.masterdata.service;


import io.mosip.kernel.masterdata.dto.LocationCodeResponseDto;
import io.mosip.kernel.masterdata.dto.LocationHierarchyResponseDto;
import io.mosip.kernel.masterdata.dto.LocationRequestDto;
import io.mosip.kernel.masterdata.dto.LocationResponseDto;

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
	public LocationCodeResponseDto saveLocationHierarchy(LocationRequestDto locationRequestDto);
}
