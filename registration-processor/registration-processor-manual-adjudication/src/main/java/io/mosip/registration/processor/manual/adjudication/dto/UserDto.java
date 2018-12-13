package io.mosip.registration.processor.manual.adjudication.dto;

import java.io.Serializable;

/**
 * The {@link UserDto} class
 * 
 * @author Shuchita
 * @since 0.0.1
 *
 */
public class UserDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String userId;

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
}
