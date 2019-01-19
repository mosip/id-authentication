package io.mosip.kernel.masterdata.constant;

public enum PacketRejectionReasonErrorCode {
	NO_PACKET_REJECTION_REASONS_FOUND("KER-MSD-036", "Reason not found"), 
	PACKET_REJECTION_REASONS_FETCH_EXCEPTION("KER-MSD-035", "Error occured while fetching Reasons"),
	PACKET_REJECTION_REASONS_CATEGORY_INSERT_EXCEPTION("KER-MSD-057","Error occure while inserting reason details "),
	PACKET_REJECTION_REASONS_LIST_INSERT_EXCEPTION("KER-MSD-058","Error occure while inserting reason details ");
	
  
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
