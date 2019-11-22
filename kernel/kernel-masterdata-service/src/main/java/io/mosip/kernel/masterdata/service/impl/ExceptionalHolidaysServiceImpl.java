package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.ExceptionalHolidayErrorCode;
import io.mosip.kernel.masterdata.dto.ExceptionalHolidayDto;
import io.mosip.kernel.masterdata.dto.getresponse.ExceptionalHolidayResponseDto;
import io.mosip.kernel.masterdata.entity.ExceptionalHoliday;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.ExceptionalHolidayRepository;
import io.mosip.kernel.masterdata.service.ExceptionalHolidayService;
import io.mosip.kernel.masterdata.utils.MapperUtils;

/**
 * @author Kishan Rathore
 *
 */
@Service
public class ExceptionalHolidaysServiceImpl implements ExceptionalHolidayService {

	@Autowired
	private ExceptionalHolidayRepository repository;

	@Override
	public ExceptionalHolidayResponseDto getAllExceptionalHolidays(String regCenterId, String langCode) {

		ExceptionalHolidayResponseDto excepHolidayResponseDto = null;
		List<ExceptionalHolidayDto> excepHolidays = null;
		List<ExceptionalHoliday> exeptionalHolidayList = null;
		try {
			exeptionalHolidayList = repository.findAllNonDeletedExceptionalHoliday(regCenterId, langCode);

		} catch (DataAccessException | DataAccessLayerException dataAccessException) {
			dataAccessException.printStackTrace();
			throw new MasterDataServiceException(
					ExceptionalHolidayErrorCode.EXCEPTIONAL_HOLIDAY_FETCH_EXCEPTION.getErrorCode(),
					ExceptionalHolidayErrorCode.EXCEPTIONAL_HOLIDAY_FETCH_EXCEPTION.getErrorMessage());
		}
		if (exeptionalHolidayList != null && !exeptionalHolidayList.isEmpty()) {
			excepHolidays = MapperUtils.mapExceptionalHolidays(exeptionalHolidayList);
		} else {
			throw new DataNotFoundException(ExceptionalHolidayErrorCode.EXCEPTIONAL_HOLIDAY_NOTFOUND.getErrorCode(),
					ExceptionalHolidayErrorCode.EXCEPTIONAL_HOLIDAY_NOTFOUND.getErrorMessage());
		}
		excepHolidayResponseDto = new ExceptionalHolidayResponseDto();
		excepHolidayResponseDto.setExceptionalHolidayList(excepHolidays);
		return excepHolidayResponseDto;
	}

}
