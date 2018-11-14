package io.mosip.kernel.masterdata.constant;

public enum PacketRejectionReasonErrorCode {
 NO_PACKET_REJECTION_REASONS_FOUND("22222222","rejection reason does not exist for the given input"), 
 PACKET_REJECTION_REASONS_MAPPING_EXCEPTION("3333333","Error occured while mapping reasons"), 
 PACKET_REJECTION_REASONS_FETCH_EXCEPTION("4444444", "exception while fetching data from db"),
 PACKET_REJECTION_REASONS_ARGUMENT_NOT_FOUND_EXCEPTION("55555555","arguments either null or empty");

	private final String errorCode;
	private final String errorMessage;

	private PacketRejectionReasonErrorCode(final String errorCode, final String errorMessage) {
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
