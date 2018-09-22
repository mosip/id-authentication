package org.mosip.kernel.packetuploader.constants;

/**
 * @author Urvil Joshi
 * @since 1.0.0
 */
public enum PacketUploaderConstants {
	/**
	 * 
	 */
	STR_STRICT_HOST_KEY_CHECKING("StrictHostKeyChecking", "no"),
	/**
	 * 
	 */
	STR_SFTP("sftp"),
	/**
	 * 
	 */
	AUTHENTICATIONS("PreferredAuthentications", "publickey,keyboard-interactive,password"),
	/**
	 * 
	 */
	PACKET_SIZE("5242880"),
	/**
	 * 
	 */
	PORT_MIN("0"),
	/**
	 * 
	 */
	PORT_MAX("65535");

	/**
	 * @param value
	 */
	private PacketUploaderConstants(String value) {
		this.setValue(value);
	}

	/**
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
