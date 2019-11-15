package io.mosip.kernel.masterdata.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.WorkingNonWorkingDayErrorCode;
import io.mosip.kernel.masterdata.dto.DayNameAndSeqListDto;
import io.mosip.kernel.masterdata.dto.WeekDaysResponseDto;
import io.mosip.kernel.masterdata.dto.WorkingDaysResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.WeekDaysDto;
import io.mosip.kernel.masterdata.dto.getresponse.WorkingDaysDto;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DaysOfWeekListRepo;
import io.mosip.kernel.masterdata.repository.RegWorkingNonWorkingRepo;
import io.mosip.kernel.masterdata.service.RegWorkingNonWorkingService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;

@Service
public class RegWorkingNonWorkingServiceImpl implements RegWorkingNonWorkingService {

	@Autowired
	@Qualifier("workingDaysRepo")
	private RegWorkingNonWorkingRepo workingDaysRepo;

	@Autowired
	@Qualifier("daysOfWeekRepo")
	private DaysOfWeekListRepo daysOfWeekRepo;

	@Override
	public WeekDaysResponseDto getWeekDaysList(String regCenterId,String langCode) {

		List<WeekDaysDto> weekdayList = null;
		List<DayNameAndSeqListDto> nameSeqList = null;
		WeekDaysResponseDto weekdays = new WeekDaysResponseDto();
		
		Objects.requireNonNull(regCenterId);
		Objects.requireNonNull(langCode);

		try {
			nameSeqList = workingDaysRepo.findByregistrationCenterIdAndlanguagecode(
					regCenterId,langCode);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(
					WorkingNonWorkingDayErrorCode.WORKING_DAY_TABLE_NOT_ACCESSIBLE.getErrorCode(),
					WorkingNonWorkingDayErrorCode.WORKING_DAY_TABLE_NOT_ACCESSIBLE.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

		if (nameSeqList != null && !nameSeqList.isEmpty()) {

			nameSeqList.sort((d1, d2) -> d1.getDaySeq() - d2.getDaySeq());
			weekdayList = nameSeqList.stream().map(nameSeq -> {
				WeekDaysDto dto = new WeekDaysDto();
				dto.setLanguageCode(langCode);
				dto.setName(nameSeq.getName());
				dto.setOrder(nameSeq.getDaySeq());
				return dto;
			}).collect(Collectors.toList());
			weekdays.setWeekdays(weekdayList);

		} else {
			throw new DataNotFoundException(
					WorkingNonWorkingDayErrorCode.WEEK_DAY_DATA_FOUND_EXCEPTION.getErrorCode(),
					WorkingNonWorkingDayErrorCode.WEEK_DAY_DATA_FOUND_EXCEPTION.getErrorMessage());
		}

		return weekdays;
	}

	@Override
	public WorkingDaysResponseDto getWorkingDays(String regCenterId,String dayCode) {
		
		List<WorkingDaysDto> workingDayList=null;
		WorkingDaysResponseDto responseDto=new WorkingDaysResponseDto();
		Objects.requireNonNull(regCenterId);
		Objects.requireNonNull(dayCode);
		try {
			workingDayList=workingDaysRepo.findByregistrationCenterIdAnddayCode(regCenterId, dayCode);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(
					WorkingNonWorkingDayErrorCode.WORKING_DAY_TABLE_NOT_ACCESSIBLE.getErrorCode(),
					WorkingNonWorkingDayErrorCode.WORKING_DAY_TABLE_NOT_ACCESSIBLE.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		
		if(workingDayList!=null && !workingDayList.isEmpty()) {
			responseDto.setWorkingdays(workingDayList);
		}
		else {
			throw new DataNotFoundException(
					WorkingNonWorkingDayErrorCode.WORKING_DAY_DATA_FOUND_EXCEPTION.getErrorCode(),
					WorkingNonWorkingDayErrorCode.WORKING_DAY_DATA_FOUND_EXCEPTION.getErrorMessage());
		}
		return responseDto;
	}

}
