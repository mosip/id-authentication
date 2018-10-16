package io.mosip.registration.core.constant;

/**
 * Error Code for Groupid generator
 * 
 * @author M1037717
 * @since 1.0.0
 *
 */
public enum GroupidGeneratorErrorCodes {
	UNABLE_TO_CONNECT_TO_DB("KER-PRD-001", "Unable to connect to the database");
	private final String errorCode;
	private final String errorMessage;





	private GroupidGeneratorErrorCodes(final String errorCode, final String errorMessage) {
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
