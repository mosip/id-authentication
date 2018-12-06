package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.HolidayErrorCode;
import io.mosip.kernel.masterdata.dto.HolidayDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.HolidayResponseDto;
import io.mosip.kernel.masterdata.entity.Holiday;
import io.mosip.kernel.masterdata.entity.id.HolidayID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.HolidayRepository;
import io.mosip.kernel.masterdata.service.HolidayService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * Service Impl class for Holiday Data
 * 
 * @author Sidhant Agarwal
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@Service
public class HolidayServiceImpl implements HolidayService {
	@Autowired
	private HolidayRepository holidayRepository;
	@Autowired
	private MapperUtils mapperUtil;

	@Autowired
	private MetaDataUtils metaDataUtils;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.HolidayService#getAllHolidays()
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.HolidayService#getHolidayById(int)
	 */
	@Override
	public HolidayResponseDto getHolidayById(int id) {

		HolidayResponseDto holidayResponseDto = null;
		List<HolidayDto> holidayDto = null;
		List<Holiday> holidays = null;
		try {
			holidays = holidayRepository.findAllById(id);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.HolidayService#
	 * getHolidayByIdAndLanguageCode(int, java.lang.String)
	 */
	@Override
	public HolidayResponseDto getHolidayByIdAndLanguageCode(int id, String langCode) {
		HolidayResponseDto holidayResponseDto = null;
		List<HolidayDto> holidayList = null;
		List<Holiday> holidays = null;
		try {
			holidays = holidayRepository.findHolidayByIdAndHolidayIdLangCode(id, langCode);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.HolidayService#saveHoliday(io.mosip.kernel
	 * .masterdata.dto.RequestDto)
	 */
	@Override
	public HolidayID saveHoliday(RequestDto<HolidayDto> holidayDto) {
		Holiday entity = metaDataUtils.setCreateMetaData(holidayDto.getRequest(), Holiday.class);
		Holiday holiday;
		try {
			holiday = holidayRepository.create(entity);
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(HolidayErrorCode.HOLIDAY_INSERT_EXCEPTION.getErrorCode(),
					ExceptionUtils.parseException(e));
		}
		HolidayID holidayId = new HolidayID();
		mapperUtil.mapNew(holiday, holidayId);
		return holidayId;

	}
}
