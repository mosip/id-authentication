package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.HolidayDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.HolidayResponseDto;
import io.mosip.kernel.masterdata.entity.id.HolidayID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * Service class for Holiday Data
 * 
 * @author Abhishek Kumar
 * @author Sidhant Agarwal
 * @version 1.0.0
 * @since 25-10-2018
 */
public interface HolidayService {

	/**
	 * to fetch all the holidays
	 * 
	 * @return {@linkplain HolidayResponseDto}
	 * @throws MasterDataServiceException
	 *             when data not fetched from DB
	 * @throws DataNotFoundException
	 *             when data not found
	 */
	HolidayResponseDto getAllHolidays();

	/**
	 * to fetch specific holiday using holiday id
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
	 * to fetch specific holiday using holiday id and language code
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
	 * to add a new holiday data
	 * 
	 * @param holidayDto
	 *            input values for holiday
	 * @return primary key of entered row of holiday data
	 * @throws MasterDataServiceException
	 *             when entered data not created
	 */
	public HolidayID saveHoliday(RequestDto<HolidayDto> holidayDto);
}
