package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Document category
 * 
 * @author Neha
 * @author Uday Kumar
 * @since 1.0.0
 */
public enum DocumentTypeErrorCode {
	DOCUMENT_TYPE_FETCH_EXCEPTION("KER-MSD-015","Error occured while fetching Document Types"),
	DOCUMENT_TYPE_INSERT_EXCEPTION("KER-MSD-444","Exception during inserting data into db"),
	DOCUMENT_TYPE_NOT_FOUND_EXCEPTION("KER-MSD-016","Document Type not found");

	private final String errorCode;
	private final String errorMessage;

	private DocumentTypeErrorCode(final String errorCode, final String errorMessage) {
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
