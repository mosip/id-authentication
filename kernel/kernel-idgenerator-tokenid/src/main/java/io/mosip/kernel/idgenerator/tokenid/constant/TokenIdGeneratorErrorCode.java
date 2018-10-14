package io.mosip.kernel.idgenerator.tokenid.constant;

public enum TokenIdGeneratorErrorCode {
	UNABLE_TO_CONNECT_TO_DB("KER-PRD-001", "Unable to connect to the database");
	private final String errorCode;
	private final String errorMessage;
    /**
     * sets error code for specific error and also sets message
     * @param errorCode
     * @param errorMessage
     */
	private TokenIdGeneratorErrorCode(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
    /**
     *  getter method for error code
     * @return errorCode
     */
	public String getErrorCode() {
		return errorCode;
	}
    /**
     * getter method for error message
     * 
     * @return errorMessage
     */
	public String getErrorMessage() {
		return errorMessage;
	}
}
