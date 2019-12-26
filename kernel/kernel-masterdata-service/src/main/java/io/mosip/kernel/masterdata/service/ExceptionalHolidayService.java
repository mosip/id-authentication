package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.getresponse.ExceptionalHolidayResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.HolidayResponseDto;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * @author Kishan Rathore
 *
 */
public interface ExceptionalHolidayService {
	
	/**
	 * To fetch all the holidays
	 * 
	 * @return {@linkplain HolidayResponseDto}
	 * @throws MasterDataServiceException
	 *             when data not fetched from DB
	 * @throws DataNotFoundException
	 *             when data not found
	 */
	ExceptionalHolidayResponseDto getAllExceptionalHolidays(String regCenterId , String langCode );

}
