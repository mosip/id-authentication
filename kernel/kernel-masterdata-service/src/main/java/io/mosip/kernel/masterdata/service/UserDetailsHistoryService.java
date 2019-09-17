package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.postresponse.UserDetailsHistoryResponseDto;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public interface UserDetailsHistoryService {
	/**
	 * @param userId
	 *            input from user
	 * @param effDTimes
	 *            input from user
	 * @return user detail DTO for the particular input data
	 */
	UserDetailsHistoryResponseDto getByUserIdAndTimestamp(String userId, String effDTimes);

}
