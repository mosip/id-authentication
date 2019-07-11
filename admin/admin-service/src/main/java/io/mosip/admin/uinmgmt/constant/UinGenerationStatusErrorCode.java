package io.mosip.admin.uinmgmt.constant;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public enum UinGenerationStatusErrorCode {
	UIN_GENERATION_STATUS_EXCEPTION("ADM-PKT-002","Unable to fetch Packet Status"), 
	PARSE_EXCEPTION("ADM-PKT-003","Unable to parse the status");

	
	
	
	private final String errorCode;
	private final String errorMessage;

	private UinGenerationStatusErrorCode(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}
}
