package io.mosip.kernel.masterdata.constant;

public enum MachineSpecificationErrorCode {

	MACHINE_SPECIFICATION_INSERT_EXCEPTION("KER-APP-444",
			"Error occurred while inserting Machine Specification details");

	private final String errorCode;
	private final String errorMessage;

	private MachineSpecificationErrorCode(final String errorCode, final String errorMessage) {
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
