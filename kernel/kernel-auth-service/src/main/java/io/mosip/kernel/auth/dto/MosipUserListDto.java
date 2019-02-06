package io.mosip.kernel.auth.dto;

import java.util.List;

/**
 *  @author Sabbu Uday Kumar
 *  @since 1.0.0
 */
public class MosipUserListDto {
	
	private List<MosipUserDto> userDetails;

	public List<MosipUserDto> getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(List<MosipUserDto> userDetails) {
		this.userDetails = userDetails;
	}

}
