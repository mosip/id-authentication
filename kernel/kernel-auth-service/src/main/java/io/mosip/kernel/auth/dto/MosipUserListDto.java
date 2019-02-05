package io.mosip.kernel.auth.dto;

import java.util.List;

public class MosipUserListDto {
	
	private List<MosipUserDto> userDetails;

	public List<MosipUserDto> getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(List<MosipUserDto> userDetails) {
		this.userDetails = userDetails;
	}

}
