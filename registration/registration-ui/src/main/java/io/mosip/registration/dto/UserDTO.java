package io.mosip.registration.dto;

/**
 * DTO class for User details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public class UserDTO {

	private String userId;
	private String username;
	private String password;
	private String loginMode;
	private boolean multifactAuth;
	private String sequencevalue;
	private String centerId;
	private String centerLocation;

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the loginMode
	 */
	public String getLoginMode() {
		return loginMode;
	}

	/**
	 * @param loginMode
	 *            the loginMode to set
	 */
	public void setLoginMode(String loginMode) {
		this.loginMode = loginMode;
	}

	/**
	 * @return the multifactAuth
	 */
	public boolean isMultifactAuth() {
		return multifactAuth;
	}

	/**
	 * @param multifactAuth
	 *            the multifactAuth to set
	 */
	public void setMultifactAuth(boolean multifactAuth) {
		this.multifactAuth = multifactAuth;
	}

	/**
	 * @return the sequencevalue
	 */
	public String getSequencevalue() {
		return sequencevalue;
	}

	/**
	 * @param sequencevalue
	 *            the sequencevalue to set
	 */
	public void setSequencevalue(String sequencevalue) {
		this.sequencevalue = sequencevalue;
	}

	/**
	 * @return the centerId
	 */
	public String getCenterId() {
		return centerId;
	}

	/**
	 * @param centerId
	 *            the centerId to set
	 */
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	/**
	 * @return the centerLocation
	 */
	public String getCenterLocation() {
		return centerLocation;
	}

	/**
	 * @param centerLocation
	 *            the centerLocation to set
	 */
	public void setCenterLocation(String centerLocation) {
		this.centerLocation = centerLocation;
	}

}
