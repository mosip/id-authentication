package io.mosip.kernel.packetuploader.http.constant;

/**
 * packet uploader Http Constants
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public enum PacketUploaderConstant {

	/**
	 * {@link #INVALID_FILE} constant
	 */
	INVALID_FILE(".."),
	/**
	 * {@link #PACKET_MIN_SIZE} constant
	 */
	PACKET_MIN_SIZE("0");

	/**
	 * Constructor for this {@link Enum}
	 * 
	 * @param value
	 *            {@link #value}
	 */
	private PacketUploaderConstant(String value) {
		this.setValue(value);
	}

	/**
	 * Value of constants
	 */
	private String value;

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
