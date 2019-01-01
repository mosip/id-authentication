package io.mosip.kernel.masterdata.constant;

/**
 * Error code for TemplateFileFormat
 * 
 * @author Neha Sinha
 * @since 1.0.0
 */
public enum TemplateFileFormatErrorCode {

	TEMPLATE_FILE_FORMAT_INSERT_EXCEPTION("KER-MSD-055",
			"Error occurred while inserting Template File Format details"), TEMPLATE_FILE_FORMAT_NOT_FOUND(
					"KER-MSD-046", "Template not found."), TEMPLATE_FILE_FORMAT_UPDATE_EXCEPTION("KER-MSD-093",
							"Error occured while updating Template"), TEMPLATE_FILE_FORMAT_DELETE_EXCEPTION(
									"KER-MSD-094",
									"Error occured while deleting Template"), TEMPLATE_FILE_FORMAT_DELETE_DEPENDENCY_EXCEPTION(
											"KER-MSD-125", "Cannot delete dependency found.");

	private final String errorCode;
	private final String errorMessage;

	private TemplateFileFormatErrorCode(final String errorCode, final String errorMessage) {
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
