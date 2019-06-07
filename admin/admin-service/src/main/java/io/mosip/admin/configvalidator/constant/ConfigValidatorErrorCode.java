package io.mosip.admin.configvalidator.constant;

public enum ConfigValidatorErrorCode {

	CONFIG_FILE_NOT_FOUND("ADM-PFG-002", "Process Flow Configuration not found"), CONFIG_NOT_SUCCESSFULLY_VALIDATED(
			"ADM-PKT-001", "Validation Failed"), REG_CLIENT_PROPERTY_NOT_FOUND("ADM-PEG-004",
					"Document Upload property not found"), REG_PROC_PROPERTY_NOT_FOUND("ADM-PEG-003",
							"Document Validation property not found");

	private final String errorCode;
	private final String errorMessage;

	private ConfigValidatorErrorCode(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String errorMessage() {
		return this.errorMessage;
	}

	public String errorCode() {
		return this.errorCode;
	}

}
