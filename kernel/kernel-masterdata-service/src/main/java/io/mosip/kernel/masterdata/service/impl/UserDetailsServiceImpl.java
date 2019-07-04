package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.UserDetailsErrorCode;
import io.mosip.kernel.masterdata.dto.UserDetailsDto;
import io.mosip.kernel.masterdata.dto.postresponse.UserDetailsResponseDto;
import io.mosip.kernel.masterdata.entity.UserDetailsHistory;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.UserDetailsRepository;
import io.mosip.kernel.masterdata.service.UserDetailsService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserDetailsRepository userDetailsRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.UserDetailsService#getByUserIdAndTimestamp
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public UserDetailsResponseDto getByUserIdAndTimestamp(String userId, String effDTimes) {
		List<UserDetailsHistory> userDetails = null;
		UserDetailsResponseDto userResponseDto = new UserDetailsResponseDto();

		LocalDateTime localDateTime = null;
		try {
			localDateTime = MapperUtils.parseToLocalDateTime(effDTimes);
		} catch (DateTimeParseException e) {
			throw new RequestException(
					UserDetailsErrorCode.INVALID_EFFECTIVE_DATE_TIME_FORMATE_EXCEPTION.getErrorCode(),
					UserDetailsErrorCode.INVALID_EFFECTIVE_DATE_TIME_FORMATE_EXCEPTION.getErrorMessage());
		}
		try {
			userDetails = userDetailsRepository.getByUserIdAndTimestamp(userId, localDateTime);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(UserDetailsErrorCode.USER_HISTORY_FETCH_EXCEPTION.getErrorCode(),
					UserDetailsErrorCode.USER_HISTORY_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		if (userDetails == null || userDetails.isEmpty()) {
			throw new DataNotFoundException(UserDetailsErrorCode.USER_HISTORY_NOT_FOUND_EXCEPTION.getErrorCode(),
					UserDetailsErrorCode.USER_HISTORY_NOT_FOUND_EXCEPTION.getErrorMessage());
		} else {
			userResponseDto.setUserResponseDto(MapperUtils.mapAll(userDetails, UserDetailsDto.class));
		}
		return userResponseDto;
	}

}
