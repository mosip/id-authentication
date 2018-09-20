package org.mosip.kernel.idvalidator.vidvalidator.constants;

public enum MosipVidExceptionCodeConstants {
	
	VID_VAL_INVALID_NULL("KER-IDV-VID-001", "Entered VID should not be empty or null."),
	VID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE("KER-IDV-VID-002", "Entered VID should not contain any sequential and repeated block of number for 2 or more than two digits"), 
	VID_VAL_ILLEGAL_LENGTH("KER-IDV-VID-003", "Entered VID length should be 16 digit."), 
	VID_VAL_INVALID_DIGITS("KER-IDV-VID-004", "Entered VID should not contain any alphanumeric characters."), 
	VID_VAL_ILLEGAL_CHECKSUM("KER-IDV-VID-005", "Entered VID should match checksum."),
	VID_VAL_INVALID_ZERO_ONE("KER-IDV-VID-006", "Entered VID should not contain Zero or One as first Digit.");
	
	
	/**
	 * This variable holds the error code.
	 */
	private String errorCode;

	/**
	 * This variable holds the error message.
	 */
	private String errorMessage;

	/**
	 * Constructor for VIDErrorConstants Enum.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 */
	MosipVidExceptionCodeConstants(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for errorCode.
	 * 
	 * @return the error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for errorMessage.
	 * 
	 * @return the error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

}
