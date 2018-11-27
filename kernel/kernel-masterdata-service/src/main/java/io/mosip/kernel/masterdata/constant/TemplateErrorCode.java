package io.mosip.kernel.masterdata.constant;

/**
 * Error code for Template
 * 
 * @author Neha
 * @since 1.0.0
 */
public enum TemplateErrorCode {

	TEMPLATE_FETCH_EXCEPTION("KER-TEM-045", "Error ocurred while fetching Templates"), TEMPLATE_NOT_FOUND("KER-TEM-046",
			"Template not found.");

	private final String errorCode;
	private final String errorMessage;

	private TemplateErrorCode(final String errorCode, final String errorMessage) {
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
