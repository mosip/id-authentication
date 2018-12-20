package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Document category
 * 
 * @author Neha
 * @author Ritesh Sinha
 * @since 1.0.0
 */
public enum DocumentCategoryErrorCode {

	DOCUMENT_CATEGORY_FETCH_EXCEPTION("KER-MSD-013","Error occured while fetching Document Category"),
	DOCUMENT_CATEGORY_INSERT_EXCEPTION("KER-MSD-113","Error occured while inserting Document Category"),
	DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION("KER-MSD-014","Document Category not found"),
	DOCUMENT_CATEGORY_UPDATE_EXCEPTION("xxx","Error occured while updating Document Category"),
	DOCUMENT_CATEGORY_DELETE_EXCEPTION("xxx","Error occured while deleting Document Category");

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
