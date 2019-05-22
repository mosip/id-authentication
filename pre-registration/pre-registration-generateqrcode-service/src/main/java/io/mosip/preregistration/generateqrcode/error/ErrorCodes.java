package io.mosip.preregistration.generateqrcode.error;

/**
 * @author Sanober Noor
 * @since 1.0.0
 */
public enum ErrorCodes {

	
	/**
	 * INPUT_OUTPUT_EXCEPTION
	 */
	PRG_QRC_001("PRG_QRC_001"),

	/**
	 * QRCODE_FAILED_TO_GENERATE
	 */
	PRG_QRC_002("PRG_QRC_002"),
	
	/**
	 * CONFIG_FILE_NOT_FOUND_EXCEPTION
	 */
	PRG_QRC_003("PRG_QRC_003");

	/**
	 * @param code
	 */
	private ErrorCodes(String code) {
		this.code = code;
	}

	private final String code;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

}
