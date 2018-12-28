package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Document category
 * 
 * @author Neha
 * @author Ritesh Sinha
 * @since 1.0.0
 */
public enum DocumentCategoryErrorCode {

	DOCUMENT_CATEGORY_FETCH_EXCEPTION("KER-MSD-013","Error occured while fetching Document Category details"),
	DOCUMENT_CATEGORY_INSERT_EXCEPTION("KER-MSD-051","Error occured while inserting Document Category details"),
	DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION("KER-MSD-014","Document Category not found"),
	DOCUMENT_CATEGORY_UPDATE_EXCEPTION("KER-MSD-089","Error occured while updating Document Category details"),
	DOCUMENT_CATEGORY_DELETE_EXCEPTION("KER-MSD-090","Error occured while deleting Document Category details"),
	DOCUMENT_CATEGORY_DELETE_DEPENDENCY_EXCEPTION("KER-MSD-123","Cannot delete dependency found");

	private final String errorCode;
	private final String errorMessage;

	private DocumentCategoryErrorCode(final String errorCode, final String errorMessage) {
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
