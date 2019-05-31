package io.mosip.preregistration.generateqrcode.error;

/**
 * @author Sanober Noor
 *@since 1.0.0
 */
public enum ErrorMessages {

	
	/**
	 * @param code
	 * ErrorMessage for PRG_QRC_001
	 */
	
	INPUT_OUTPUT_EXCEPTION("File input output exception"),
	
	/**
	 * ErrorMessage for PRG_QRC_002
	 */
	QRCODE_FAILED_TO_GENERATE("Failed to generate QR code");
	

	
	private ErrorMessages(String code) {
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
