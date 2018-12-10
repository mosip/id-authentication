package io.mosip.kernel.packetuploader.sftp.constant;

/**
 * Constants for Packet Uploader
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public enum PacketUploaderConstant {
	/**
	 * Check strictly for host or not
	 */
	STR_STRICT_HOST_KEY_CHECKING("StrictHostKeyChecking", "no"),
	/**
	 * Channel type
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
	EXCEPTION_BREAKER(" : "),
	/**
	 * Minimum port allowed
	 */
	PORT_MIN("0"),
	/**
	 * Maximum Port allowed
	 */
	PORT_MAX("65535");

	/**
	 * Getter for {@link #value}
	 * 
	 * @param value
	 */
	private PacketUploaderConstant(String value) {
		this.setValue(value);
	}

	/**
	 * Constructor for this {@link Enum}
	 * 
	 * @param key
	 *            value of {@link #key}
	 * @param value
	 *            {@link #value}
	 */
	private PacketUploaderConstant(String key, String value) {
		this.setKey(key);
		this.setValue(value);
	}

	/**
	 * Value of constants
	 */
	private String value;
	/**
	 * Key of constants
	 */
	private String key;

	/**
	 * Getter for {@link #key}
	 * 
	 * @return {@link #key}
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Setter for {@link #key}
	 * 
	 * @param key
	 *            {@link #key}
	 */
	private void setKey(String key) {
		this.key = key;
	}

	/**
	 * Getter for {@link #value}
	 * 
	 * @return {@link #value}
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Setter for {@link #value}
	 * 
	 * @param value
	 *            {@link #value}
	 */
	private void setValue(String value) {
		this.value = value;
	}
}
