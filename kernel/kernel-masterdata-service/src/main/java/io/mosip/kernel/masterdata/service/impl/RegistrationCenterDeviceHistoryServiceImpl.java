package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.RegistrationCenterDeviceHistoryErrorCode;
import io.mosip.kernel.masterdata.dto.RegistrationCenterDeviceHistoryDto;
import io.mosip.kernel.masterdata.dto.getresponse.RegistrationCenterDeviceHistoryResponseDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDeviceHistory;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.RegistrationCenterDeviceHistoryRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterDeviceHistoryService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 */
@Service
public class RegistrationCenterDeviceHistoryServiceImpl implements RegistrationCenterDeviceHistoryService {

	@Autowired
	RegistrationCenterDeviceHistoryRepository repository;

	
	/* (non-Javadoc)
	 * @see io.mosip.kernel.masterdata.service.RegistrationCenterDeviceHistoryService#getRegCenterDeviceHisByregCenterIdDevIdEffDTime(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistrationCenterDeviceHistoryResponseDto getRegCenterDeviceHisByregCenterIdDevIdEffDTime(
			String regCenterId, String deviceId, String effDateTime) {
		LocalDateTime lDateAndTime = null;
		try {
			lDateAndTime = MapperUtils.parseToLocalDateTime(effDateTime);
		} catch (DateTimeParseException e) {
			throw new RequestException(
					RegistrationCenterDeviceHistoryErrorCode.INVALIDE_EFFECTIVE_DATE_TIME_FORMATE_EXCEPTION.getErrorCode(),
					RegistrationCenterDeviceHistoryErrorCode.INVALIDE_EFFECTIVE_DATE_TIME_FORMATE_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}

		RegistrationCenterDeviceHistory registrationCenterDeviceHistory;
		RegistrationCenterDeviceHistoryDto registrationCenterDeviceHistoryDto;
		RegistrationCenterDeviceHistoryResponseDto registrationCenterDeviceHistoryResponseDto = new RegistrationCenterDeviceHistoryResponseDto();
		try {
			registrationCenterDeviceHistory = repository
					.findByFirstByRegCenterIdAndDeviceIdAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(
							regCenterId, deviceId, lDateAndTime);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(RegistrationCenterDeviceHistoryErrorCode.REGISTRATION_CENTER_DEVICE_HISTORY_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterDeviceHistoryErrorCode.REGISTRATION_CENTER_DEVICE_HISTORY_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		if (registrationCenterDeviceHistory != null) {

			registrationCenterDeviceHistoryDto = MapperUtils.map(registrationCenterDeviceHistory,
					RegistrationCenterDeviceHistoryDto.class);
		} else {
			throw new DataNotFoundException(RegistrationCenterDeviceHistoryErrorCode.REGISTRATION_CENTER_DEVICE_HISTORY_NOT_FOUND_EXCEPTION.getErrorCode(),
					RegistrationCenterDeviceHistoryErrorCode.REGISTRATION_CENTER_DEVICE_HISTORY_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		registrationCenterDeviceHistoryResponseDto
				.setRegistrationCenterDeviceHistoryDetails(registrationCenterDeviceHistoryDto);
		return registrationCenterDeviceHistoryResponseDto;
	}
}
