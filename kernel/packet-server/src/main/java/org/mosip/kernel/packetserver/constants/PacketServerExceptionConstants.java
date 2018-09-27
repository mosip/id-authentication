package org.mosip.kernel.packetserver.constants;

/**
 * Exception constants for this Application
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public enum PacketServerExceptionConstants {

	/**
	 * {@link #MOSIP_PUBLIC_KEY_EXCEPTION} exception constant
	 */
	MOSIP_PUBLIC_KEY_EXCEPTION("cerrorcode", "cannot read public key"),
	/**
	 * {@link #MOSIP_INVALID_SPEC_EXCEPTION} exception constant
	 */
	MOSIP_INVALID_SPEC_EXCEPTION("eerrorcode", "public key is does not have valid spec"),
	/**
	 * {@link #MOSIP_ILLEGAL_STATE_EXCEPTION} exception constant
	 */
	MOSIP_ILLEGAL_STATE_EXCEPTION("ferrorcode", "server went into illegal state");

	/**
	 * Constructor for this {@link Enum}
	 */
	private PacketServerExceptionConstants() {
	}

	/**
	 * Constructor for this {@link Enum}
	 * 
	 * @param errorCode
	 *            errorCode for exception
	 * @param errorMessage
	 *            errorMessage for exception
	 */
	PacketServerExceptionConstants(String errorCode, String errorMessage) {
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
	 * @param errorCode {@link #errorCode}
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
