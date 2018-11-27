package io.mosip.authentication.service.integration;

/**
 * 
 * @author Dinesh Karuppiah.T
 */
public enum NotificationType {

	EMAIL("email"), SMS("sms");

	private String name;

	NotificationType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
