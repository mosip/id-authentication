package org.mosip.kernel.httppacketuploader.constants;

/**
 * Exception constants for this Application
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public enum PacketUploaderExceptionConstants {

	/**
	 * {@link #MOSIP_DIRECTORY_NOT_EMPTY_FILE_LOCATION_EXCEPTION} exception constant
	 */
	MOSIP_DIRECTORY_NOT_EMPTY_FILE_LOCATION_EXCEPTION("maerrorcode", "file name is a non empty directory"),
	/**
	 * {@link #MOSIP_SECURITY_FILE_LOCATION_EXCEPTION} exception constant
	 */
	MOSIP_SECURITY_FILE_LOCATION_EXCEPTION("mberrorcode",
			"file location does not exist and cannot be created permission denied"),
	/**
	 * {@link #MOSIP_IO_FILE_EXCEPTION} exception constant
	 */
	MOSIP_IO_FILE_EXCEPTION("mcerrorcode", "exception occure while reading and writing file"),
	/**
	 * {@link #MOSIP_INVALID_FILE_NAME_EXCEPTION} exception constant
	 */
	MOSIP_INVALID_FILE_NAME_EXCEPTION("mcderrorcode", "invalid filename contains .."),
	/**
	 * {@link #MOSIP_PACKET_SIZE_EXCEPTION} exception constant
	 */
	MOSIP_PACKET_SIZE_EXCEPTION("adadadasdasdas", "packet size should be less than 5 MB and greater than 0");

	/**
	 * Constructor for this {@link Enum}
	 */
	private PacketUploaderExceptionConstants() {
	}

	/**
	 * Constructor for this {@link Enum}
	 * 
	 * @param errorCode
	 *            errorCode for exception
	 * @param errorMessage
	 *            errorMessage for exception
	 */
	PacketUploaderExceptionConstants(String errorCode, String errorMessage) {
		this.setErrorCode(errorCode);
		this.setErrorMessage(errorMessage);
	}

	/**
	 * getter for {@link #errorCode}
	 * 
	 * @return {@link #errorCode}
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * setter for {@link #errorCode}
	 * 
	 * @param errorCode
	 *            {@link #errorCode}
	 */
	private void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * getter for {@link #errorMessage}
	 * 
	 * @return {@link #errorMessage}
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * setter for {@link #errorMessage}
	 * 
	 * @param errorMessage
	 *            {@link #errorMessage}
	 */
	private void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * error Code for Exception
	 */
	String errorCode;
	/**
	 * error Message for Exception
	 */
	String errorMessage;
}
