package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.UserDetailsHistoryErrorCode;
import io.mosip.kernel.masterdata.dto.UserDetailsDto;
import io.mosip.kernel.masterdata.dto.postresponse.UserDetailsHistoryResponseDto;
import io.mosip.kernel.masterdata.entity.UserDetailsHistory;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.UserDetailsHistoryRepository;
import io.mosip.kernel.masterdata.service.UserDetailsHistoryService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Service
public class UserDetailsHistoryServiceImpl implements UserDetailsHistoryService {

	@Autowired
	UserDetailsHistoryRepository userDetailsHistoryRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.UserDetailsService#getByUserIdAndTimestamp
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public UserDetailsHistoryResponseDto getByUserIdAndTimestamp(String userId, String effDTimes) {
		List<UserDetailsHistory> userDetails = null;
		UserDetailsHistoryResponseDto userResponseDto = new UserDetailsHistoryResponseDto();

		LocalDateTime localDateTime = null;
		try {
			localDateTime = MapperUtils.parseToLocalDateTime(effDTimes);
		} catch (DateTimeParseException e) {
			throw new RequestException(
					UserDetailsHistoryErrorCode.INVALID_EFFECTIVE_DATE_TIME_FORMATE_EXCEPTION.getErrorCode(),
					UserDetailsHistoryErrorCode.INVALID_EFFECTIVE_DATE_TIME_FORMATE_EXCEPTION.getErrorMessage());
		}
		try {
			userDetails = userDetailsHistoryRepository.getByUserIdAndTimestamp(userId, localDateTime);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(UserDetailsHistoryErrorCode.USER_HISTORY_FETCH_EXCEPTION.getErrorCode(),
					UserDetailsHistoryErrorCode.USER_HISTORY_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		if (userDetails == null || userDetails.isEmpty()) {
			throw new DataNotFoundException(UserDetailsHistoryErrorCode.USER_HISTORY_NOT_FOUND_EXCEPTION.getErrorCode(),
					UserDetailsHistoryErrorCode.USER_HISTORY_NOT_FOUND_EXCEPTION.getErrorMessage());
		} else {
			userResponseDto.setUserResponseDto(MapperUtils.mapAll(userDetails, UserDetailsDto.class));
		}
		return userResponseDto;
	}

}
