package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.postresponse.UserDetailsResponseDto;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public interface UserDetailsService {
	/**
	 * @param userId
	 *            input from user
	 * @param effDTimes
	 *            input from user
	 * @return user detail DTO for the particular input data
	 */
	UserDetailsResponseDto getByUserIdAndTimestamp(String userId, String effDTimes);

}
