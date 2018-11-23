package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Document category
 * 
 * @author Neha
 * @author Uday Kumar
 * @since 1.0.0
 */
public enum DocumentCategoryErrorCode {
	DOCUMENT_CATEGORY_FETCH_EXCEPTION("KER-MSD-013","Error occured while fetching Device Specifications"),
	DOCUMENT_CATEGORY_INSERT_EXCEPTION("KER-MSD-444","Exception during inserting data into db"),
	DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION("KER-MSD-014","Document Category not found");

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
