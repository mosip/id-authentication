package org.mosip.kernel.packetuploader.constants;

/**
 * Packet Uploader Exception Constants
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public enum PacketUploaderExceptionConstants {

	/**
	 * {@link #MOSIP_ILLEGAL_CONFIGURATION_EXCEPTION} exception constant
	 */
	MOSIP_ILLEGAL_CONFIGURATION_EXCEPTION("merrorcode", "illegal configuration provided"),
	/**
	 * {@link #MOSIP_CONNECTION_EXCEPTION} exception constant
	 */
	MOSIP_CONNECTION_EXCEPTION("derrorcode", "cannot connect to server"),
	/**
	 * {@link #MOSIP_ILLEGAL_IDENTITY_EXCEPTION} exception constant
	 */
	MOSIP_ILLEGAL_IDENTITY_EXCEPTION("cderrorxdcode", "key file not valid"),
	/**
	 * {@link #MOSIP_SFTP_EXCEPTION} exception constant
	 */
	MOSIP_SFTP_EXCEPTION("berrorcode", "file transfer inturrupted"),
	/**
	 * {@link #MOSIP_NO_SESSION_FOUND_EXCEPTION} exception constant
	 */
	MOSIP_NO_SESSION_FOUND_EXCEPTION("aerrorcode", "session not found"),

	/**
	 * {@link #MOSIP_EMPTY_PATH_EXCEPTION} exception constant
	 */
	MOSIP_EMPTY_PATH_EXCEPTION("cerroxdcfrcode", "filepath is empty"),
	/**
	 * {@link #MOSIP_NULL_PATH_EXCEPTION} exception constant
	 */
	MOSIP_NULL_PATH_EXCEPTION("aaasrrorcode", "filepath is null"),
	/**
	 * {@link #MOSIP_EMPTY_HOST_EXCEPTION} exception constant
	 */
	MOSIP_EMPTY_HOST_EXCEPTION("cerrdsffffforcode", "host is empty"),
	/**
	 * {@link #MOSIP_NULL_HOST_EXCEPTION} exception constant
	 */
	MOSIP_NULL_HOST_EXCEPTION("aaaaaaerrorcode", "host is null"),
	/**
	 * {@link #MOSIP_EMPTY_USER_EXCEPTION} exception constant
	 */
	MOSIP_EMPTY_USER_EXCEPTION("cerrorcoddse", "user is empty"),
	/**
	 * {@link #MOSIP_NULL_USER_EXCEPTION} exception constant
	 */
	MOSIP_NULL_USER_EXCEPTION("aaaerrorcsaode", "user is null"),
	/**
	 * {@link #MOSIP_EMPTY_REMOTE_DIRECTORY_EXCEPTION} exception constant
	 */
	MOSIP_EMPTY_REMOTE_DIRECTORY_EXCEPTION("cerrorcadsasdadasode", "remote directory is empty"),
	/**
	 * {@link #MOSIP_NULL_REMOTE_DIRECTORY_EXCEPTION} exception constant
	 */
	MOSIP_NULL_REMOTE_DIRECTORY_EXCEPTION("adasasdas", "remote directory is null"),
	/**
	 * {@link #MOSIP_INVALID_KEY_EXCEPTION} exception constant
	 */
	MOSIP_INVALID_KEY_EXCEPTION("cerradasdasdasorcode", "one of password and key should be provided"),
	/**
	 * {@link #MOSIP_INVALID_PORT_EXCEPTION} exception constant
	 */
	MOSIP_INVALID_PORT_EXCEPTION("adasdasdas", "invalid port range is 0 to 65535"),
	/**
	 * {@link #MOSIP_NULL_CONFIGURATION_EXCEPTION} exception constant
	 */
	MOSIP_NULL_CONFIGURATION_EXCEPTION("adasdassssdas", "configuration is null"),
	/**
	 * {@link #MOSIP_PACKET_SIZE_EXCEPTION} exception constant
	 */
	MOSIP_PACKET_SIZE_EXCEPTION("adadadasdasdas", "packet size should be less than 5 MB and greater than 0 Byte");

	/**
	 * 
	 */
	private PacketUploaderExceptionConstants() {
	}

	/**
	 * Constructor for this {@link Enum}
	 * 
	 * @param errorCode
	 * @param errorMessage
	 */
	PacketUploaderExceptionConstants(String errorCode, String errorMessage) {
		this.setErrorCode(errorCode);
		this.setErrorMessage(errorMessage);
	}

	/**
	 * getter for {@link #errorCode}
	 * 
	 * @return
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * setter for {@link #errorCode}
	 * 
	 * @param errorCode
	 */
	private void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * getter for {@link #errorMessage}
	 * 
	 * @return
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * setter for {@link #errorMessage}
	 * 
	 * @param errorMessage
	 */
	private void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * 
	 */
	String errorCode;
	/**
	 * 
	 */
	String errorMessage;
}
