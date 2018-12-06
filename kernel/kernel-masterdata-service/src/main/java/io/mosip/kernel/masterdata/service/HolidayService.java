package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.HolidayDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.HolidayResponseDto;
import io.mosip.kernel.masterdata.entity.id.HolidayID;

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
	 */
	HolidayResponseDto getAllHolidays();

	/**
	 * to fetch specific holiday using holiday id
	 * 
	 * @param holidayId
	 *            input from user
	 * @return {@linkplain HolidayResponseDto}
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
	 */
	HolidayResponseDto getHolidayByIdAndLanguageCode(int holidayId, String langCode);

	/**
	 * to add a new holiday data
	 * 
	 * @param holidayDto
	 *            input values for holiday
	 * @return primary key of entered row of holiday data
	 */
	public HolidayID saveHoliday(RequestDto<HolidayDto> holidayDto);
}
