package io.mosip.kernel.masterdata.constant;

public enum ValidDocumentErrorCode {
	
	VALID_DOCUMENT_FETCH_EXCEPTION("xxx","Error occured while fetching Device Specifications"),
	VALID_DOCUMENT_INSERT_EXCEPTION("KER-MSD-444","Exception during inserting data into db"),
	VALID_DOCUMENT_NOT_FOUND_EXCEPTION("xxx","Document Category not found");
	
	private final String errorCode;
	private final String errorMessage;

	private ValidDocumentErrorCode(final String errorCode, final String errorMessage) {
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
