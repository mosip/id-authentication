package io.mosip.admin.configvalidator.constant;

public enum ConfigValidatorErrorCode {

	CONFIG_FILE_NOT_FOUND("ADM-xxx-002", "Process Flow Configuration not found"), CONFIG_NOT_SUCCESSFULLY_VALIDATED(
			"ADM-xxx-004",
			"Unable to validate Process Flow Configurations as they do not match"), REG_CLIENT_PROPERTY_NOT_FOUND("ADM-xxx-xxx",
					"Reg client property not found"), REG_PROC_PROPERTY_NOT_FOUND("ADM-xxx-xxx", "Reg proc property not found");

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
