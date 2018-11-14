package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Document category
 * 
 * @author Neha
 * @author Uday Kumar
 * @since 1.0.0
 */
public enum DocumentCategoryErrorCode {
	DOCUMENT_CATEGORY_FETCH_EXCEPTION("KER-MSD-036",
			"exception during fatching data from db"), DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION("KER-MSD-038",
					"No documents found for specified document category code and language code"), DOCUMENT_CATEGORY_MAPPING_EXCEPTION(
							"KER-MSD-037", "Error occured while mapping document category");

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
