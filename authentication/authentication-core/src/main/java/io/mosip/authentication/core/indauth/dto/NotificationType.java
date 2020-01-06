package io.mosip.authentication.core.indauth.dto;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * General-purpose of {@code NotificationType} class used to provide
 * Notification type
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
	SMS("sms", "PHONE", "MOBILE"),

	/**
	 * Enum for None
	 */
	NONE("none", "none");

	/**
	 * Variable to hold name
	 */
	private String name;
	private String channel;
	private String apiChannel;

	NotificationType(String name, String channel) {
		this.name = name;
		this.channel = channel;
		apiChannel = channel;
	}

	NotificationType(String name, String channel, String apiChannel) {
		this.name = name;
		this.channel = channel;
		this.apiChannel = apiChannel;

	}

	public String getName() {
		return name;
	}

	public String getChannel() {
		return channel;
	}

	public String getApiChannel() {
		return apiChannel;
	}

	public static Optional<NotificationType> getNotificationTypeForChannel(String channel) {
		return Stream.of(values()).filter(nt -> nt.getChannel().equalsIgnoreCase(channel)).findAny();
	}

}
