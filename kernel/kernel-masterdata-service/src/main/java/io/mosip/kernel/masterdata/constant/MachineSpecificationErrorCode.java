package io.mosip.kernel.masterdata.constant;

public enum MachineSpecificationErrorCode {

	
	MACHINE_SPECIFICATION_NOT_FOUND_EXCEPTION("KER-APP-000","Machine Specification not Found"),
	MACHINE_SPECIFICATION_INSERT_EXCEPTION("KER-APP-444",
			"Error occurred while inserting Machine Specification details"),
	MACHINE_SPECIFICATION_UPDATE_EXCEPTION("KER-APP-001",
			"Error occurred while updating Machine Specification details"),
	
	MACHINE_SPECIFICATION_DELETE_EXCEPTION("KER-APP-003","Error occurred while deleteding Machine Specification details"),
	
	MACHINE_DELETE_EXCEPTION("KER-APP-002","Error occurred while deleting Machine Specification Beacuse Machine Dependency is there");

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
