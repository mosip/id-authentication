package io.mosip.authentication.core.indauth.dto;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 
 * @author Dinesh Karuppiah.T
 */
public enum NotificationType {

	/**
	 * Enum for Email
	 */
	EMAIL("email", "EMAIL"),
	
	/**
	 * Enum for SMS
	 */
	SMS("sms", "PHONE"),

	/**
	 * Enum for None
	 */
	NONE("none", "none");

	/**
	 * Variable to hold name
	 */
	private String name;
	private String channel;

	NotificationType(String name, String channel) {
		this.name = name;
		this.channel = channel;
	}

	public String getName() {
		return name;
	}
	
	public String getChannel() {
		return channel;
	}

	public static Optional<NotificationType> getNotificationTypeForChannel(String channel) {
		return Stream.of(values()).filter(nt -> nt.getChannel().equalsIgnoreCase(channel)).findAny();
	}

}
