package io.mosip.kernel.masterdata.constant;

public enum PacketRejectionReasonErrorCode {
	NO_PACKET_REJECTION_REASONS_FOUND("KER-MSD-036", "Reason not found"), PACKET_REJECTION_REASONS_FETCH_EXCEPTION(
			"KER-MSD-035", "Error occured while fetching Reasons");

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
