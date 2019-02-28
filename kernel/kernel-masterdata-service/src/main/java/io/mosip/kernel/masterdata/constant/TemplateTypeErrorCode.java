package io.mosip.kernel.masterdata.constant;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */

public enum TemplateTypeErrorCode {
	TEMPLATE_TYPE_INSERT_EXCEPTION("KER-MSD-072", "Error occurred while inserting Template Type details into db");
	private final String errorCode;
	private final String errorMessage;

	private TemplateTypeErrorCode(final String errorCode, final String errorMessage) {
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
