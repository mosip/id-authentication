package io.mosip.kernel.pdfgenerator.itext.constant;

/**
 * 
 * @author Uday Kumar
 * 
 * @since 1.0.0
 */

public enum PDFGeneratorExceptionCodeConstant {
	PDF_EXCEPTION("KER-PDG-001", "Pdf generation failed"),
	OWNER_PASSWORD_NULL_EMPTY_EXCEPTION("KER-PDG-002", "Owner Password is null or Empty or not in properties"),
	INPUTSTREAM_NULL_EMPTY_EXCEPTION("KER-PDG-003", "InputStream is null or Empty or not in properties");
	/**
	 * This variable holds the error code.
	 */
	private String errorCode;

	/**
	 * This variable holds the error message.
	 */
	private String errorMessage;

	/**
	 * Constructor for UINErrorConstants Enum.
	 * 
	 * @param errorCode    the error code.
	 * @param errorMessage the error message.
	 */
	PDFGeneratorExceptionCodeConstant(String errorCode, String errorMessage) {
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
