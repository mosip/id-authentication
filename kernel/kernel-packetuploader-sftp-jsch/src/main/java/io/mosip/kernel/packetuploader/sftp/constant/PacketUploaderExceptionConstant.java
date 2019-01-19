package io.mosip.kernel.packetuploader.sftp.constant;

/**
 * Packet Uploader Exception Constants
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public enum PacketUploaderExceptionConstant {

	/**
	 * {@link #MOSIP_ILLEGAL_CONFIGURATION_EXCEPTION} exception constant
	 */
	MOSIP_ILLEGAL_CONFIGURATION_EXCEPTION("KER-FTM-FTP-009", "illegal configuration provided"),
	/**
	 * {@link #MOSIP_CONNECTION_EXCEPTION} exception constant
	 */
	MOSIP_CONNECTION_EXCEPTION("KER-FTM-FTP-010", "cannot connect to server"),
	/**
	 * {@link #MOSIP_ILLEGAL_IDENTITY_EXCEPTION} exception constant
	 */
	MOSIP_ILLEGAL_IDENTITY_EXCEPTION("KER-FTM-FTP-011", "key file not valid"),
	/**
	 * {@link #MOSIP_SFTP_EXCEPTION} exception constant
	 */
	MOSIP_SFTP_EXCEPTION("KER-FTM-FTP-012", "file transfer inturrupted"),
	/**
	 * {@link #MOSIP_NO_SESSION_FOUND_EXCEPTION} exception constant
	 */
	MOSIP_NO_SESSION_FOUND_EXCEPTION("KER-FTM-FTP-013", "session not found"),

	/**
	 * {@link #MOSIP_EMPTY_PATH_EXCEPTION} exception constant
	 */
	MOSIP_EMPTY_PATH_EXCEPTION("KER-FTM-FTP-014", "filepath is empty"),
	/**
	 * {@link #MOSIP_NULL_PATH_EXCEPTION} exception constant
	 */
	MOSIP_NULL_PATH_EXCEPTION("KER-FTM-FTP-015", "filepath is null"),
	/**
	 * {@link #MOSIP_EMPTY_HOST_EXCEPTION} exception constant
	 */
	MOSIP_EMPTY_HOST_EXCEPTION("KER-FTM-FTP-016", "host is empty"),
	/**
	 * {@link #MOSIP_NULL_HOST_EXCEPTION} exception constant
	 */
	MOSIP_NULL_HOST_EXCEPTION("KER-FTM-FTP-017", "host is null"),
	/**
	 * {@link #MOSIP_EMPTY_USER_EXCEPTION} exception constant
	 */
	MOSIP_EMPTY_USER_EXCEPTION("KER-FTM-FTP-018", "user is empty"),
	/**
	 * {@link #MOSIP_NULL_USER_EXCEPTION} exception constant
	 */
	MOSIP_NULL_USER_EXCEPTION("KER-FTM-FTP-019", "user is null"),
	/**
	 * {@link #MOSIP_EMPTY_REMOTE_DIRECTORY_EXCEPTION} exception constant
	 */
	MOSIP_EMPTY_REMOTE_DIRECTORY_EXCEPTION("KER-FTM-FTP-025", "remote directory is empty"),
	/**
	 * {@link #MOSIP_NULL_REMOTE_DIRECTORY_EXCEPTION} exception constant
	 */
	MOSIP_NULL_REMOTE_DIRECTORY_EXCEPTION("KER-FTM-FTP-026", "remote directory is null"),
	/**
	 * {@link #MOSIP_INVALID_KEY_EXCEPTION} exception constant
	 */
	MOSIP_INVALID_KEY_EXCEPTION("KER-FTM-FTP-020", "one of password and key should be provided"),
	/**
	 * {@link #MOSIP_INVALID_PORT_EXCEPTION} exception constant
	 */
	MOSIP_INVALID_PORT_EXCEPTION("KER-FTM-FTP-022", "invalid port range is 0 to 65535"),
	/**
	 * {@link #MOSIP_NULL_CONFIGURATION_EXCEPTION} exception constant
	 */
	MOSIP_NULL_CONFIGURATION_EXCEPTION("KER-FTM-FTP-023", "configuration is null"),
	/**
	 * {@link #MOSIP_PACKET_SIZE_EXCEPTION} exception constant
	 */
	MOSIP_PACKET_SIZE_EXCEPTION("KER-FTM-FTP-024", "packet size should be less than 5 MB and greater than 0 Byte");

	/**
	 * ErrorCode
	 */
	private String errorCode;
	/**
	 * ErrorMessage
	 */
	private String errorMessage;

	/**
	 * Constructor for this enum
	 */
	private PacketUploaderExceptionConstant() {
	}

	/**
	 * Getter for {@link #errorCode}
	 * 
	 * @return {@link #errorCode}
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Constructor for this {@link Enum}
	 * 
	 * @param errorCode    {@link #errorCode}Code
	 * @param errorMessage {@link #errorMessage}
	 */
	PacketUploaderExceptionConstant(String errorCode, String errorMessage) {
		this.setErrorCode(errorCode);
		this.setErrorMessage(errorMessage);
	}

	/**
	 * Setter for {@link #errorCode}
	 * 
	 * @param errorCode
	 */
	private void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Getter for {@link #errorMessage}
	 * 
	 * @return {@link #errorMessage}
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Setter for {@link #errorMessage}
	 * 
	 * @param errorMessage {@link #errorMessage}
	 */
	private void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}