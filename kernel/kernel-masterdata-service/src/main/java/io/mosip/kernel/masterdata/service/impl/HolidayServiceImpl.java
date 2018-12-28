package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.HolidayErrorCode;
import io.mosip.kernel.masterdata.dto.HolidayDto;
import io.mosip.kernel.masterdata.dto.HolidayIDDto;
import io.mosip.kernel.masterdata.dto.HolidayIdDeleteDto;
import io.mosip.kernel.masterdata.dto.HolidayUpdateDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.HolidayResponseDto;
import io.mosip.kernel.masterdata.entity.Holiday;
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
	private static final String UPDATE_HOLIDAY_QUERY = "UPDATE Holiday h SET h.isActive = :isActive ,h.updatedBy = :updatedBy , h.updatedDateTime = :updatedDateTime, h.holidayDesc = :holidayDesc,h.holidayId.holidayDate=:newHolidayDate,h.holidayId.holidayName = :newHolidayName   WHERE h.holidayId.locationCode = :locationCode and h.holidayId.holidayName = :holidayName and h.holidayId.holidayDate = :holidayDate and h.holidayId.langCode = :langCode and (h.isDeleted is null or h.isDeleted = false)";

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
			holidays = holidayRepository.findAllNonDeletedHoliday();
		} catch (DataAccessException | DataAccessLayerException dataAccessException) {
			throw new MasterDataServiceException(HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorCode(),
					HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorMessage());
		}

		if (holidays != null && !holidays.isEmpty()) {
			holidayDto = MapperUtils.mapHolidays(holidays);
		} else {
			throw new DataNotFoundException(HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorCode(),
					HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorMessage());
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
		} catch (DataAccessException | DataAccessLayerException dataAccessException) {
			throw new MasterDataServiceException(HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorCode(),
					HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorMessage());
		}

		if (holidays != null && !holidays.isEmpty()) {
			holidayDto = MapperUtils.mapHolidays(holidays);
		} else {
			throw new DataNotFoundException(HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorCode(),
					HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorMessage());
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
		} catch (DataAccessException | DataAccessLayerException dataAccessException) {
			throw new MasterDataServiceException(HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorCode(),
					HolidayErrorCode.HOLIDAY_FETCH_EXCEPTION.getErrorMessage());
		}

		if (holidays != null && !holidays.isEmpty()) {
			holidayList = MapperUtils.mapHolidays(holidays);
		} else {
			throw new DataNotFoundException(HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorCode(),
					HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorMessage());
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
	public HolidayIDDto saveHoliday(RequestDto<HolidayDto> holidayDto) {
		Holiday entity = MetaDataUtils.setCreateMetaData(holidayDto.getRequest(), Holiday.class);
		Holiday holiday;
		try {
			holiday = holidayRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(HolidayErrorCode.HOLIDAY_INSERT_EXCEPTION.getErrorCode(),
					ExceptionUtils.parseException(e));
		}
		HolidayIDDto holidayId = new HolidayIDDto();
		MapperUtils.map(holiday, holidayId);
		return holidayId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.HolidayService#updateHoliday(io.mosip.
	 * kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public HolidayIDDto updateHoliday(RequestDto<HolidayUpdateDto> holidayDto) {
		HolidayIDDto idDto = null;
		HolidayUpdateDto dto = holidayDto.getRequest();
		Map<String, Object> params = bindDtoToMap(dto);
		try {
			int noOfRowAffected = holidayRepository.createQueryUpdateOrDelete(UPDATE_HOLIDAY_QUERY, params);
			if (noOfRowAffected != 0)
				idDto = mapToHolidayIdDto(dto);
			else
				throw new DataNotFoundException(HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorCode(),
						HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorMessage());

		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(HolidayErrorCode.HOLIDAY_UPDATE_EXCEPTION.getErrorCode(),
					HolidayErrorCode.HOLIDAY_UPDATE_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}
		return idDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.HolidayService#deleteHoliday(io.mosip.
	 * kernel.masterdata.entity.id.HolidayID)
	 */
	@Override
	public HolidayIdDeleteDto deleteHoliday(RequestDto<HolidayIdDeleteDto> request) {
		HolidayIdDeleteDto idDto = request.getRequest();
		try {
			int affectedRows = holidayRepository.deleteHolidays(LocalDateTime.now(ZoneId.of("UTC")),
					idDto.getHolidayName(), idDto.getHolidayDate(), idDto.getLocationCode());
			if (affectedRows == 0)
				throw new DataNotFoundException(HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorCode(),
						HolidayErrorCode.HOLIDAY_NOTFOUND.getErrorMessage());

		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(HolidayErrorCode.HOLIDAY_DELETE_EXCEPTION.getErrorCode(),
					HolidayErrorCode.HOLIDAY_DELETE_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}
		return idDto;
	}

	/**
	 * Bind {@link HolidayUpdateDto} dto to {@link Map}
	 * 
	 * @param dto
	 *            input {@link HolidayUpdateDto}
	 * @return {@link Map} with the named parameter and value
	 */
	private Map<String, Object> bindDtoToMap(HolidayUpdateDto dto) {
		Map<String, Object> params = new HashMap<>();
		if (dto.getNewHolidayName() != null && !dto.getNewHolidayName().isEmpty())
			params.put("newHolidayName", dto.getNewHolidayName());
		else
			params.put("newHolidayName", dto.getHolidayName());
		if (dto.getNewHolidayDate() != null)
			params.put("newHolidayDate", dto.getNewHolidayDate());
		else
			params.put("newHolidayDate", dto.getHolidayDate());
		if (dto.getNewHolidayDesc() != null && !dto.getNewHolidayDesc().isEmpty())
			params.put("holidayDesc", dto.getNewHolidayDesc());
		else
			params.put("holidayDesc", dto.getHolidayDesc());

		params.put("isActive", dto.getIsActive());
		params.put("holidayDate", dto.getHolidayDate());
		params.put("holidayName", dto.getHolidayName());
		params.put("updatedBy", SecurityContextHolder.getContext().getAuthentication().getName());
		params.put("updatedDateTime", LocalDateTime.now(ZoneId.of("UTC")));
		params.put("locationCode", dto.getLocationCode());
		params.put("langCode", dto.getLangCode());
		return params;
	}

	/**
	 * Bind the {@link HolidayUpdateDto} to {@link HolidayIDDto}
	 * 
	 * @param dto
	 *            input {@link HolidayUpdateDto} to be bind
	 * @return {@link HolidayIDDto} holiday id
	 */
	private HolidayIDDto mapToHolidayIdDto(HolidayUpdateDto dto) {
		HolidayIDDto idDto;
		idDto = new HolidayIDDto();
		if (dto.getNewHolidayName() != null)
			idDto.setHolidayName(dto.getNewHolidayName());
		else
			idDto.setHolidayName(dto.getHolidayName());
		if (dto.getNewHolidayDate() != null)
			idDto.setHolidayDate(dto.getNewHolidayDate());
		else
			idDto.setHolidayDate(dto.getHolidayDate());
		idDto.setLocationCode(dto.getLocationCode());
		idDto.setLangCode(dto.getLangCode());
		return idDto;
	}
}
