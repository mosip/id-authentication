package io.mosip.registration.dto;

import java.util.List;

/**
 * Service class for user detail response dto
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public class UserDetailResponseDto {
	List<UserDetailDto> userDetails;

	/**
	 * @return the userDetails
	 */
	public List<UserDetailDto> getUserDetails() {
		return userDetails;
	}

	/**
	 * @param userDetails the userDetails to set
	 */
	public void setUserDetails(List<UserDetailDto> userDetails) {
		this.userDetails = userDetails;
	}

}
