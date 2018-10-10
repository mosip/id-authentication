package io.mosip.registration.dto;

public class UserDto {
	private String userName;
	

	public UserDto(String userName) {
		super();
		this.userName = userName;
	}

	public UserDto() {
		// TODO Auto-generated constructor stub
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	

}
