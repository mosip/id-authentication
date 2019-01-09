package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.DeviceHistoryErrorCode;
import io.mosip.kernel.masterdata.constant.MachineHistoryErrorCode;
import io.mosip.kernel.masterdata.dto.DeviceHistoryDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceHistoryResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.DeviceHistory;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.DeviceHistoryRepository;
import io.mosip.kernel.masterdata.service.DeviceHistoryService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;

@Service
public class DeviceHistoryServiceImpl implements DeviceHistoryService {

	@Autowired
	DeviceHistoryRepository deviceHistoryRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DeviceHistoryService#
	 * getDeviceHistroyIdLangEffDTime(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public DeviceHistoryResponseDto getDeviceHistroyIdLangEffDTime(String id, String langCode, String effDtime) {
		LocalDateTime lDateAndTime = null;
		try {
			lDateAndTime = MapperUtils.parseToLocalDateTime(effDtime);
		} catch (DateTimeParseException e) {
			throw new RequestException(
					MachineHistoryErrorCode.INVALIDE_EFFECTIVE_DATE_TIME_FORMATE_EXCEPTION.getErrorCode(),
					MachineHistoryErrorCode.INVALIDE_EFFECTIVE_DATE_TIME_FORMATE_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

		List<DeviceHistory> devHistoryList = null;
		List<DeviceHistoryDto> deviceHistoryDtoList = null;
		DeviceHistoryResponseDto deviceHistoryResponseDto = new DeviceHistoryResponseDto();
		try {
			devHistoryList = deviceHistoryRepository
					.findByFirstByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(id,
							langCode, lDateAndTime);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DeviceHistoryErrorCode.DEVICE_HISTORY_FETCH_EXCEPTION.getErrorCode(),
					DeviceHistoryErrorCode.DEVICE_HISTORY_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		if (devHistoryList != null && !devHistoryList.isEmpty()) {

			deviceHistoryDtoList = MapperUtils.mapAll(devHistoryList, DeviceHistoryDto.class);
		} else {
			throw new DataNotFoundException(DeviceHistoryErrorCode.DEVICE_HISTORY_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceHistoryErrorCode.DEVICE_HISTORY_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		deviceHistoryResponseDto.setDeviceHistoryDetails(deviceHistoryDtoList);
		return deviceHistoryResponseDto;
	}
	
	
	/* (non-Javadoc)
	 * @see io.mosip.kernel.masterdata.service.DeviceHistoryService#createDeviceHistory(io.mosip.kernel.masterdata.entity.DeviceHistory)
	 */
	@Override
	@Transactional(propagation=Propagation.MANDATORY)
	public IdResponseDto createDeviceHistory(DeviceHistory entityHistory) {
		DeviceHistory createdHistory;
			createdHistory = deviceHistoryRepository.create(entityHistory);

		IdResponseDto idResponseDto = new IdResponseDto();
		MapperUtils.map(createdHistory, idResponseDto);
		return idResponseDto;
	}
}
