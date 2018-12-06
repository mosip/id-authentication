package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.HolidayDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.HolidayResponseDto;
import io.mosip.kernel.masterdata.entity.id.HolidayID;

/**
 * @author Abhishek Kumar
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
	 * @return {@linkplain HolidayResponseDto}
	 */
	HolidayResponseDto getHolidayById(int holidayId);

	/**
	 * to fetch specific holiday using holiday id and language code
	 * 
	 * @param holidayId
	 * @param langCode
	 * @return {@linkplain HolidayResponseDto}
	 */
	HolidayResponseDto getHolidayByIdAndLanguageCode(int holidayId, String langCode);
	
	public HolidayID saveHoliday(RequestDto<HolidayDto> holidayDto);
}
