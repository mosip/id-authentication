package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.masterdata.dto.HolidayDto;
import io.mosip.kernel.masterdata.dto.HolidayIDDto;
import io.mosip.kernel.masterdata.dto.HolidayIdDeleteDto;
import io.mosip.kernel.masterdata.dto.HolidayUpdateDto;
import io.mosip.kernel.masterdata.dto.getresponse.HolidayResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.HolidayExtnDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.HolidaySearchDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * Service class for Holiday Data
 * 
 * @author Abhishek Kumar
 * @author Sidhant Agarwal
 * @author Uday Kumar
 * @since 1.0.0
 */
public interface HolidayService {

	/**
	 * To fetch all the holidays
	 * 
	 * @return {@linkplain HolidayResponseDto}
	 * @throws MasterDataServiceException
	 *             when data not fetched from DB
	 * @throws DataNotFoundException
	 *             when data not found
	 */
	HolidayResponseDto getAllHolidays();

	/**
	 * To fetch specific holiday using holiday id
	 * 
	 * @param holidayId
	 *            input from user
	 * @return {@linkplain HolidayResponseDto}
	 * @throws MasterDataServiceException
	 *             when data not fetched from DB
	 * @throws DataNotFoundException
	 *             when data not found
	 */
	HolidayResponseDto getHolidayById(int holidayId);

	/**
	 * To fetch specific holiday using holiday id and language code
	 * 
	 * @param holidayId
	 *            input from user
	 * @param langCode
	 *            input from user
	 * @return {@linkplain HolidayResponseDto}
	 * @throws MasterDataServiceException
	 *             when data not fetched from DB
	 * @throws DataNotFoundException
	 *             when data not found
	 */
	HolidayResponseDto getHolidayByIdAndLanguageCode(int holidayId, String langCode);

	/**
	 * To add a new holiday data
	 * 
	 * @param holidayDto
	 *            input values for holiday
	 * @return primary key of entered row of holiday data
	 * @throws MasterDataServiceException
	 *             when entered data not created
	 */
	public HolidayIDDto saveHoliday(HolidayDto holidayDto);

	/**
	 * Method to update a holiday data
	 * 
	 * @param holidayDto
	 *            input values for holidays
	 * @return primary key of entered row of holiday data
	 * @throws MasterDataServiceException
	 *             when entered data not updated
	 */
	public HolidayIDDto updateHoliday(HolidayUpdateDto holidayDto);

	/**
	 * Method to delete the holidays
	 * 
	 * @param holidayId
	 *            input holiday id
	 * @return id of the holiday which been deleted
	 */
	public HolidayIdDeleteDto deleteHoliday(RequestWrapper<HolidayIdDeleteDto> holidayId);

	/**
	 * This method provides with all holidays.
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
	 * @return the response i.e. pages containing the holidays
	 */
	public PageDto<HolidayExtnDto> getHolidays(int pageNumber, int pageSize, String sortBy, String orderBy);
	
	/**
	 * Method to search holidays based on filters provided.
	 * 
	 * @param dto
	 *            the search DTO.
	 * @return the {@link PageResponseDto}.
	 */
	public PageResponseDto<HolidaySearchDto> searchHolidays(SearchDto dto);

	/**
	 * Method to filter holidays based on column and type provided.
	 * 
	 * @param filterValueDto
	 *            the filter DTO.
	 * @return the {@link FilterResponseDto}.
	 */
	public FilterResponseDto holidaysFilterValues(FilterValueDto filterValueDto);
}
