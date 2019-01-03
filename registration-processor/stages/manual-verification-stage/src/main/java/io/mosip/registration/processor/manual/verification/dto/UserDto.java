package io.mosip.registration.processor.manual.verification.dto;

import java.io.Serializable;
	
/**
 * The {@link UserDto} class.
 *
 * @author Shuchita
 * @since 0.0.1
 */
public class UserDto implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The user id. */
	private String userId;

	/**
	 * Gets the user id.
	 *
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Sets the user id.
	 *
	 * @param userId            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
}
