package io.mosip.admin.uinmgmt.constant;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public enum UinGenerationStatusErrorCode {
	UIN_GENERATION_STATUS_EXCEPTION("ADM-xxx-002","Error while fetching uin generation status"), PARSE_EXCEPTION("XX","error while parsing ");

	
	
	
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
