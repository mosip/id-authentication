package io.mosip.kernel.masterdata.constant;

/**
 * Error code for Template
 * 
 * @author Neha
 * @since 1.0.0
 */
public enum TemplateErrorCode {

	TEMPLATE_FETCH_EXCEPTION("KER-TEM-001",
			"Error ocurred while fetching template"), TEMPLATE_MAPPING_EXCEPTION("KER-TEM-002",
					"Error occured while mapping template"), TEMPLATE_NOT_FOUND("KER-TEM-003",
							"No template found.");
	
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
