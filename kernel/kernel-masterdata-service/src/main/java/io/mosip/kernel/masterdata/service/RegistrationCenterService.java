package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.PageDto;
import io.mosip.kernel.masterdata.dto.RegCenterPostReqDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterHolidayDto;
import io.mosip.kernel.masterdata.dto.RegCenterPutReqDto;
import io.mosip.kernel.masterdata.dto.getresponse.RegistrationCenterResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.ResgistrationCenterStatusResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.RegistrationCenterExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.RegistrationCenterPostResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.RegistrationCenterPutResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.dto.response.RegistrationCenterSearchDto;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * This interface contains methods that provides registration centers details
 * based on user provided data.
 * 
 * @author Dharmesh Khandelwal
 * @author Abhishek Kumar
 * @author Urvil Joshi
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @author Sidhant Agarwal
 * @author Srinivasan
 * @author Uday Kumar
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public interface RegistrationCenterService {

	/**
	 * Function to fetch specific registration center holidays by registration
	 * center id , year and language code
	 * 
	 * @param registrationCenterId
	 *            centerId of required center
	 * @param year
	 *            the year provided by user.
	 * @param langCode
	 *            languageCode of required center.
	 * @return {@link RegistrationCenterHolidayDto}
	 */
	RegistrationCenterHolidayDto getRegistrationCenterHolidays(String registrationCenterId, int year, String langCode);

	/**
	 * Function to fetch nearby registration centers using coordinates.
	 * 
	 * @param longitude
	 *            the longitude provided by user.
	 * @param latitude
	 *            the latitude provided by user.
	 * @param proximityDistance
	 *            the proximity distance provided by user.
	 * @param langCode
	 *            languageCode of required centers.
	 * @return {@link RegistrationCenterResponseDto}
	 */
	RegistrationCenterResponseDto getRegistrationCentersByCoordinates(double longitude, double latitude,
			int proximityDistance, String langCode);

	/**
	 * Function to fetch registration center using centerId and language code.
	 * 
	 * @param registrationCenterId
	 *            centerId of required center.
	 * @param langCode
	 *            languageCode of required center.
	 * @return {@link RegistrationCenterResponseDto}
	 */
	RegistrationCenterResponseDto getRegistrationCentersByIDAndLangCode(String registrationCenterId, String langCode);

	/**
	 * Function to fetch registration centers list using location code and language
	 * code.
	 * 
	 * @param locationCode
	 *            location code for which the registration center needs to be
	 *            searched.
	 * @param langCode
	 *            language code for which the registration center needs to be
	 *            searched.
	 * @return the list of registration centers.
	 */
	RegistrationCenterResponseDto getRegistrationCentersByLocationCodeAndLanguageCode(String locationCode,
			String langCode);

	/**
	 * Function to fetch all registration centers list.
	 * 
	 * @return the list of all registration centers.
	 */
	public RegistrationCenterResponseDto getAllRegistrationCenters();

	/**
	 * Function to fetch list of registration centers based on hierarchy level,text
	 * input and language code
	 * 
	 * @param hierarchyLevel
	 *            input from user
	 * @param languageCode
	 *            input from user
	 * @param name
	 *            input from user
	 *
	 * @return list of registration centers
	 * @throws MasterDataServiceException
	 *             when data not fetched from DB
	 * @throws DataNotFoundException
	 *             when data not found
	 */
	public RegistrationCenterResponseDto findRegistrationCenterByHierarchyLevelandTextAndLanguageCode(
			String languageCode, Short hierarchyLevel, String name);

	/**
	 * This service method can be used to create registration center.
	 * 
	 * @param registrationCenterDto
	 *            the input registration center dto.
	 * @return the id response dto.
	 */
	// public IdResponseDto createRegistrationCenter(RegistrationCenterDto
	// registrationCenterDto);

	/**
	 * This method would validate timestamp and id whether the given date in
	 * timestamp is a holiday. Also,checks time in the timestamp whether it is
	 * between working hours.
	 * 
	 * @param id
	 *            - registration id
	 * @param langCode
	 *            language code
	 * @param timeStamp
	 *            - Time stamp based on the format YYYY-MM-ddTHH:mm:ss.SSSZ
	 * @return ResgistrationCenterStatusResponseDto
	 */
	public ResgistrationCenterStatusResponseDto validateTimeStampWithRegistrationCenter(String id, String langCode,
			String timeStamp);

	/**
	 * This method deletes the registration center.
	 * 
	 * @param registrationCenterId
	 *            - the id of the registration center to be deleted.
	 * @return - the id response DTO.
	 */
	IdResponseDto deleteRegistrationCenter(String registrationCenterId);

	/**
	 * Function to fetch list of registration centers based on hierarchy level,text
	 * input and language code
	 * 
	 * @param hierarchyLevel
	 *            input from user
	 * @param names
	 *            input from user
	 * @param languageCode
	 *            input from user
	 * @return list of registration centers
	 * @throws MasterDataServiceException
	 *             when data not fetched from DB
	 * @throws DataNotFoundException
	 *             when data not found
	 */
	public RegistrationCenterResponseDto findRegistrationCenterByHierarchyLevelAndListTextAndlangCode(
			String languageCode, Short hierarchyLevel, List<String> names);

	/**
	 * Function to fetch all registration centers list.
	 * 
	 * @return the list of all registration centers.
	 */
	public PageDto<RegistrationCenterExtnDto> getAllExistingRegistrationCenters(int pageNo, int pageSize, String sortBy,
			String orderBy);

	/**
	 * Method to perform search based on the input
	 * 
	 * @param searchDto
	 *            search criteria for the registration center
	 * @return list of registration centers
	 */
	public PageResponseDto<RegistrationCenterSearchDto> searchRegistrationCenter(SearchDto searchDto);

	/**
	 * Method to filter registration center based on column and type provided.
	 * 
	 * @param filterValueDto
	 *            the filter DTO.
	 * @return the {@link FilterResponseDto}.
	 */
	public FilterResponseDto registrationCenterFilterValues(FilterValueDto filterValueDto);

	/**
	 * This service method can be used to create registration center by admin,
	 * without id in request DTO will create Registration center for primary
	 * language with ID in request DTO will create Registration center for secondary
	 * language if for the given ID registration center is there in DB
	 * 
	 * @param RegistrationCenterReqAdmSecDto
	 *            -pass the List of registration center DTO to create.
	 * @return RegistrationCenterPostResponseDto - return created registration
	 *         centers complete DTO
	 */
	public RegistrationCenterPostResponseDto createRegistrationCenter(
			List<RegCenterPostReqDto> reqRegistrationCenterDto);

    /**
    * This method updates the registration center by admin.
    * 
     * @param RegCenterPutReqDto
    *            - pass the List of registration center DTO to update.
    * 
     * @return RegistrationCenterPutResponseDto - return updated registration
    *         centers complete DTO
    */

	public RegistrationCenterPutResponseDto updateRegistrationCenter(
			List<RegCenterPutReqDto> registrationCenterPutReqAdmDto);

	/**
	 * Service method to decommission registration center.
	 * 
	 * @param regCenterID
	 *            the center ID of the reg-center which needs to be decommissioned.
	 * @return {@link IdResponseDto}.
	 */
	IdResponseDto decommissionRegCenter(String regCenterID);

}
