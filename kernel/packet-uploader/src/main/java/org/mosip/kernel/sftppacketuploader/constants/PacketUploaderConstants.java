package org.mosip.kernel.packetuploader.constants;

/**
 * Constants for Packet Uploader
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public enum PacketUploaderConstants {
	/**
	 * check strictly for host or not
	 */
	STR_STRICT_HOST_KEY_CHECKING("StrictHostKeyChecking", "no"),
	/**
	 * channel type
	 */
	STR_SFTP("sftp"),
	/**
	 * PreferredAuthentications for this client
	 */
	AUTHENTICATIONS("PreferredAuthentications", "publickey,keyboard-interactive,password"),
	/**
	 * Max packet size allowed
	 */
	PACKET_SIZE_MAX("5242880"),
	/**
	 * Min packet size allowed
	 */
	PACKET_SIZE_MIN("0"),
	/**
	 * Exception Breaker
	 */
	EXCEPTTION_BREAKER(" : "),
	/**
	 * Minimum port allowed
	 */
	PORT_MIN("0"),
	/**
	 * Maximum Port allowed
	 */
	PORT_MAX("65535");

	/**
	 * getter for {@link #value}
	 * 
	 * @param value
	 */
	private PacketUploaderConstants(String value) {
		this.setValue(value);
	}

	/**
	 * constructor for this {@link Enum}
	 * 
	 * @param key
	 * @param value
	 */
	private PacketUploaderConstants(String key, String value) {
		this.setKey(key);
		this.setValue(value);
	}

	/**
	 * 
	 */
	private String value;
	/**
	 * 
	 */
	private String key;

	/**
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 */
	private void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 */
	private void setValue(String value) {
		this.value = value;
	}
}
