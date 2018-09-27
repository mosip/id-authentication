package org.mosip.kernel.sftppacketuploader.constants;

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
	 *            value of {@link #key}
	 * @param value
	 *            {@link #value}
	 */
	private PacketUploaderConstants(String key, String value) {
		this.setKey(key);
		this.setValue(value);
	}

	/**
	 * value
	 */
	private String value;
	/**
	 * key
	 */
	private String key;

	/**
	 * getter for {@link #key}
	 * 
	 * @return {@link #key}
	 */
	public String getKey() {
		return key;
	}

	/**
	 * setter for {@link #key}
	 * 
	 * @param key
	 *            {@link #key}
	 */
	private void setKey(String key) {
		this.key = key;
	}

	/**
	 * getter for {@link #value}
	 * 
	 * @return {@link #value}
	 */
	public String getValue() {
		return value;
	}

	/**
	 * setter for {@link #value}
	 * 
	 * @param value
	 *            {@link #value}
	 */
	private void setValue(String value) {
		this.value = value;
	}
}
