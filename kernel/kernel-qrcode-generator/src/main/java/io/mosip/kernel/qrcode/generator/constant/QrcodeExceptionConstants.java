package io.mosip.kernel.qrcode.generator.constant;

/**
 * Exception constants for QR Code generator
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public enum QrcodeExceptionConstants {

	QRCODE_GENERATION_EXCEPTION("KER-QRG-001", "exception occured while writing QR code "),
	INVALID_INPUT_DATA_NULL("KER-QRG-002", "data can't be null"),
	INVALID_INPUT_DATA_EMPTY("KER-QRG-003", "data can't be empty"),
	INVALID_INPUT_VERSION("KER-QRG-004", "version can't be null");

	/**
	 * Constant {@link Enum} errorCode
	 */
	private final String errorCode;

	/**
	 * Getter for errorMessage
	 * 
	 * @return get errorMessage value
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Constant {@link Enum} errorMessage
	 */
	private final String errorMessage;

	/**
	 * Constructor for this class
	 * 
	 * @param errorCode
	 *            {@link #errorCode}
	 * @param errorMessage
	 *            {@link #errorMessage}
	 */
	private QrcodeExceptionConstants(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for errorCode
	 * 
	 * @return get errorCode value
	 */
	public String getErrorCode() {
		return errorCode;
	}
}
