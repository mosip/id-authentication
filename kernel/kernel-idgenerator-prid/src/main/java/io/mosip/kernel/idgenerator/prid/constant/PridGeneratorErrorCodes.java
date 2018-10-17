package io.mosip.kernel.idgenerator.prid.constant;

/**
 * Error Code for Prid generator
 * 
 * @author M1037462
 * @since 1.0.0
 *
 */
public enum PridGeneratorErrorCodes {
	UNABLE_TO_CONNECT_TO_DB("KER-PRD-001", "Unable to connect to the database");
	private final String errorCode;
	private final String errorMessage;





	private PridGeneratorErrorCodes(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}





	public String getErrorCode() {
		return errorCode;
	}





	public String getErrorMessage() {
		return errorMessage;
	}
}
