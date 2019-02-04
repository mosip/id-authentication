package io.mosip.preregistration.auth.dto;

public class UserDto {
	private String userName;
	

	public UserDto(String userName) {
		super();
		this.userName = userName;
	}

	public UserDto() {
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	

}
