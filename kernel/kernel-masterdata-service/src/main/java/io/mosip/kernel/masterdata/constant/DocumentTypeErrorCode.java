package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Document Type.
 * 
 * @author Ritesh Sinha
 * @author Uday Kumar
 * @since 1.0.0
 */
public enum DocumentTypeErrorCode {
	
	DOCUMENT_TYPE_FETCH_EXCEPTION("KER-MSD-015","Error occured while fetching Document Types"),
	DOCUMENT_TYPE_INSERT_EXCEPTION("KER-MSD-444","Exception during inserting data into db"),
	DOCUMENT_TYPE_NOT_FOUND_EXCEPTION("KER-MSD-118","Document Type not found"),
	DOCUMENT_TYPE_UPDATE_EXCEPTION("KER-MSD-091","Error occur while updating Document Type details"),
	DOCUMENT_TYPE_DELETE_DEPENDENCY_EXCEPTION("KER-MSD-124","Cannot delete dependency found"),
	DOCUMENT_TYPE_DELETE_EXCEPTION("KER-MSD-092","Error occured while deleting Document Type details");

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
