package io.mosip.kernel.masterdata.constant;

public enum PacketStatusUpdateErrorCode {

	ADMIN_UNAUTHORIZED("ADM-PKT-001","Admin is not authorized"),
	PACKET_JSON_PARSE_EXCEPTION("ADM-PKT-010","JSON parse exception while parsing response"),
	PACKET_FETCH_EXCEPTION("ADM-PKT-090","Error occured while fetching packet status update");
	
	
	private final String errorCode;
	private final String errorMessage;

	private PacketStatusUpdateErrorCode(final String errorCode, final String errorMessage) {
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
