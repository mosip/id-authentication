package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.constant.HolidayErrorCode;
import io.mosip.kernel.masterdata.dto.HolidayDto;
import io.mosip.kernel.masterdata.dto.HolidayResponseDto;
import io.mosip.kernel.masterdata.entity.Holiday;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.HolidayRepository;
import io.mosip.kernel.masterdata.service.HolidayService;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

@Service
public class HolidayServiceImpl implements HolidayService {
	@Autowired
	private HolidayRepository holidayRepository;
	@Autowired
	private ObjectMapperUtil mapperUtil;

	@Override
	public HolidayResponseDto getAllHolidays() {
		HolidayResponseDto holidayResponseDto = null;
		List<HolidayDto> holidayDto = null;
		List<Holiday> holidays = null;
		try {
			holidays = holidayRepository.findAll(Holiday.class);
		} catch (DataAccessException dataAccessException) {
			throw new MasterDataServiceException(HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorCode(),
					HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorMessage());
		}

		if (holidays != null && !holidays.isEmpty()) {
			holidayDto = mapperUtil.mapHolidays(holidays);
		} else {
			throw new DataNotFoundException(HolidayErrorCode.ID_OR_LANGCODE_HOLIDAY_NOTFOUND_EXCEPTION.getErrorCode(),
					HolidayErrorCode.ID_OR_LANGCODE_HOLIDAY_NOTFOUND_EXCEPTION.getErrorMessage());
		}

		holidayResponseDto = new HolidayResponseDto();
		holidayResponseDto.setHolidays(holidayDto);
		return holidayResponseDto;
	}

	@Override
	public HolidayResponseDto getHolidayById(int id) {

		HolidayResponseDto holidayResponseDto = null;
		List<HolidayDto> holidayDto = null;
		List<Holiday> holidays = null;
		try {
			holidays = holidayRepository.findAllByHolidayIdId(id);
		} catch (DataAccessException dataAccessException) {
			throw new MasterDataServiceException(HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorCode(),
					HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorMessage());
		}

		if (holidays != null && !holidays.isEmpty()) {
			holidayDto = mapperUtil.mapHolidays(holidays);
		} else {
			throw new DataNotFoundException(HolidayErrorCode.ID_OR_LANGCODE_HOLIDAY_NOTFOUND_EXCEPTION.getErrorCode(),
					HolidayErrorCode.ID_OR_LANGCODE_HOLIDAY_NOTFOUND_EXCEPTION.getErrorMessage());
		}

		holidayResponseDto = new HolidayResponseDto();
		holidayResponseDto.setHolidays(holidayDto);
		return holidayResponseDto;
	}

	@Override
	public HolidayResponseDto getHolidayByIdAndLanguageCode(int id, String langCode) {
		HolidayResponseDto holidayResponseDto = null;
		List<HolidayDto> holidayList = null;
		List<Holiday> holidays = null;
		try {
			holidays = holidayRepository.findHolidayByHolidayIdIdAndHolidayIdLangCodeAndIsDeletedFalse(id, langCode);
		} catch (DataAccessException dataAccessException) {
			throw new MasterDataServiceException(HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorCode(),
					HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorMessage());
		}

		if (holidays != null && !holidays.isEmpty()) {
			holidayList = mapperUtil.mapHolidays(holidays);
		} else {
			throw new DataNotFoundException(HolidayErrorCode.ID_OR_LANGCODE_HOLIDAY_NOTFOUND_EXCEPTION.getErrorCode(),
					HolidayErrorCode.ID_OR_LANGCODE_HOLIDAY_NOTFOUND_EXCEPTION.getErrorMessage());
		}
		holidayResponseDto = new HolidayResponseDto();
		holidayResponseDto.setHolidays(holidayList);
		return holidayResponseDto;
	}
}
