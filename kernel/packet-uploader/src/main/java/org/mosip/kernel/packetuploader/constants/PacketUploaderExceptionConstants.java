package org.mosip.kernel.packetuploader.constants;

/**
 * @author Urvil Joshi
 * @since 1.0.0
 */
public enum PacketUploaderExceptionConstants {

	/**
	 * 
	 */
	MOSIP_ILLEGAL_CONFIGURATION_EXCEPTION("merrorcode", "illegal configuration provided"),
	/**
	 * 
	 */
	MOSIP_CONNECTION_EXCEPTION("derrorcode", "cannot connect to server"),
	/**
	 * 
	 */
	MOSIP_ILLEGAL_IDENTITY_EXCEPTION("cderrorxdcode", "key file not valid"),
	/**
	 * 
	 */
	MOSIP_SFTP_EXCEPTION("berrorcode", "file transfer inturrupted"),
	/**
	 * 
	 */
	MOSIP_NO_SESSION_FOUND_EXCEPTION("aerrorcode", "session not found"),

	/**
	 * 
	 */
	MOSIP_EMPTY_PATH_EXCEPTION("cerroxdcfrcode", "filepath is empty"),
	/**
	 * 
	 */
	MOSIP_NULL_PATH_EXCEPTION("aaasrrorcode", "filepath is null"),
	/**
	 * 
	 */
	MOSIP_EMPTY_HOST_EXCEPTION("cerrdsffffforcode", "host is empty"),
	/**
	 * 
	 */
	MOSIP_NULL_HOST_EXCEPTION("aaaaaaerrorcode", "host is null"),
	/**
	 * 
	 */
	MOSIP_EMPTY_USER_EXCEPTION("cerrorcoddse", "user is empty"),
	/**
	 * 
	 */
	MOSIP_NULL_USER_EXCEPTION("aaaerrorcsaode", "user is null"),
	/**
	 * 
	 */
	MOSIP_EMPTY_REMOTE_DIRECTORY_EXCEPTION("cerrorcadsasdadasode", "remote directory is empty"),
	/**
	 * 
	 */
	MOSIP_NULL_REMOTE_DIRECTORY_EXCEPTION("adasasdas", "remote directory is null"),
	/**
	 * 
	 */
	MOSIP_INVALID_KEY_EXCEPTION("cerradasdasdasorcode", "one of password and key should be provided"),
	/**
	 * 
	 */
	MOSIP_INVALID_PORT_EXCEPTION("adasdasdas", "invalid port range is 0 to 65535"),
	/**
	 * 
	 */
	MOSIP_NULL_CONFIGURATION_EXCEPTION("adasdassssdas", "configuration is null"),
	/**
	 * 
	 */
	MOSIP_PACKET_SIZE_EXCEPTION("adadadasdasdas", "packet size should be less than 5 MB");

	/**
	 * 
	 */
	private PacketUploaderExceptionConstants() {
	}

	/**
	 * @param errorCode
	 * @param errorMessage
	 */
	PacketUploaderExceptionConstants(String errorCode, String errorMessage) {
		this.setErrorCode(errorCode);
		this.setErrorMessage(errorMessage);
	}

	/**
	 * @return
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode
	 */
	private void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
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
