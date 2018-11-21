package io.mosip.kernel.masterdata.constant;

public enum TemplateFileFormatErrorCode {
	
	TEMPLATE_FILE_FORMAT_INSERT_EXCEPTION("KER-TFF-001", "Error ocurred while inserting application details.");

	private final String errorCode;
	
	private final String errorMessage;
	
	private TemplateFileFormatErrorCode(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	public String getErrorCode() {
		return this.errorCode;
	}
	
	public String getErrorMessage() {
		return this.errorMessage;
	}
}
