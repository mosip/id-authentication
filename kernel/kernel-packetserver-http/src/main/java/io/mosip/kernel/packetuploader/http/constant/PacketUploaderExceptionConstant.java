package io.mosip.kernel.packetuploader.http.constant;

/**
 * Exception constants for this Application
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public enum PacketUploaderExceptionConstant {

	/**
	 * {@link #MOSIP_DIRECTORY_NOT_EMPTY_FILE_LOCATION_EXCEPTION} exception constant
	 */
	MOSIP_DIRECTORY_NOT_EMPTY_FILE_LOCATION_EXCEPTION("KER-FTM-FTP-004", "file name is a non empty directory"),
	/**
	 * {@link #MOSIP_SECURITY_FILE_LOCATION_EXCEPTION} exception constant
	 */
	MOSIP_SECURITY_FILE_LOCATION_EXCEPTION("KER-FTM-FTP-005",
			"file location does not exist and cannot be created permission denied"),
	/**
	 * {@link #MOSIP_IO_FILE_EXCEPTION} exception constant
	 */
	MOSIP_IO_FILE_EXCEPTION("KER-FTM-FTP-006", "exception occure while reading and writing file"),
	/**
	 * {@link #MOSIP_INVALID_FILE_NAME_EXCEPTION} exception constant
	 */
	MOSIP_INVALID_FILE_NAME_EXCEPTION("KER-FTM-FTP-007", "invalid filename contains .."),
	/**
	 * {@link #MOSIP_PACKET_SIZE_EXCEPTION} exception constant
	 */
	MOSIP_PACKET_SIZE_EXCEPTION("KER-FTM-FTP-008", "packet size should be less than 5 MB and greater than 0");

	/**
	 * Error Message for Exception
	 */
	private String errorMessage;

	/**
	 * Error Code for Exception
	 */
	private String errorCode;
	
	/**
	 * Setter for {@link #errorCode}
	 * 
	 * @param errorCode {@link #errorCode}
	 */
	private void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Constructor for this {@link Enum}
	 */
	private PacketUploaderExceptionConstant() {
	}

	/**
	 * Constructor for this {@link Enum}
	 * 
	 * @param errorCode    errorCode for exception
	 * @param errorMessage errorMessage for exception
	 */
	PacketUploaderExceptionConstant(String errorCode, String errorMessage) {
		this.setErrorCode(errorCode);
		this.setErrorMessage(errorMessage);
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