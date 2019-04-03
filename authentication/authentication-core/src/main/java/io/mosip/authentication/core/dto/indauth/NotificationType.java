package io.mosip.authentication.core.dto.indauth;

/**
 * 
 * @author Dinesh Karuppiah.T
 */
public enum NotificationType {

	/**
	 * Enum for Email
	 */
	EMAIL("email"),
	/**
	 * Enum for SMS
	 */
	SMS("sms"),

	/**
	 * Enum for None
	 */
	NONE("none");

	/**
	 * Variable to hold name
	 */
	private String name;

	NotificationType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
