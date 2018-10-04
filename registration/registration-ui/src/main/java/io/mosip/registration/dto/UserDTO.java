package io.mosip.registration.dto;

public class UserDTO {
	
	private String userId;
	private String username;
	private String password;
	private String loginMode;
	private boolean multifactAuth;
	private String sequencevalue;
	private String centerId;
	private String centerLocation;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLoginMode() {
		return loginMode;
	}
	public void setLoginMode(String loginMode) {
		this.loginMode = loginMode;
	}
	public boolean isMultifactAuth() {
		return multifactAuth;
	}
	public void setMultifactAuth(boolean multifactAuth) {
		this.multifactAuth = multifactAuth;
	}
	public String getSequencevalue() {
		return sequencevalue;
	}
	public void setSequencevalue(String sequencevalue) {
		this.sequencevalue = sequencevalue;
	}
	public String getCenterId() {
		return centerId;
	}
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	public String getCenterLocation() {
		return centerLocation;
	}
	public void setCenterLocation(String centerLocation) {
		this.centerLocation = centerLocation;
	} 
	
	
			
}
