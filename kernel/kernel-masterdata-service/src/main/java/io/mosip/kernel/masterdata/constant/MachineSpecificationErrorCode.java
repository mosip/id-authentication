package io.mosip.kernel.masterdata.constant;

public enum MachineSpecificationErrorCode {

	MACHINE_SPECIFICATION_NOT_FOUND_EXCEPTION("KER-MSD-117", "Machine Specification not Found"),
	MACHINE_SPECIFICATION_INSERT_EXCEPTION("KER-MSD-062",
			"Error occurred while inserting Machine Specification details"),
	MACHINE_SPECIFICATION_UPDATE_EXCEPTION("KER-MSD-085",
			"Error occurred while updating Machine Specification details"),
	MACHINE_SPECIFICATION_FETCH_EXCEPTION("KER-MSD-087",
			"Error occurred while fetching Machine Specification details"),
	MACHINE_SPECIFICATION_DELETE_EXCEPTION("KER-MSD-086",
			"Error occurred while deleteding Machine Specification details"),

	MACHINE_DELETE_DEPENDENCY_EXCEPTION("KER-MSD-122", "Cannot delete dependency found"),
	MACHINE_ID_NOT_FOUND_FOR_NAME_EXCEPTION("KER-MSD-349","No Machine exist for Machine Type name:%s");

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
