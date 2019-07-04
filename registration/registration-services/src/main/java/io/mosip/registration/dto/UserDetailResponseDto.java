package io.mosip.registration.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The DTO Class UserDetailResponseDto.
 *
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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
