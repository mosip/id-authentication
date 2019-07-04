package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Machine Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public enum MachineErrorCode {
	MACHINE_FETCH_EXCEPTION("KER-MSD-029", "Error occured while fetching Machines"),
	MACHINE_NOT_FOUND_EXCEPTION("KER-MSD-030", "Machine not Found"),
	MACHINE_INSERT_EXCEPTION("KER-MSD-063", "Error occurred while inserting Machine details"),
	MACHINE_UPDATE_EXCEPTION("KER-MSD-087", "Error occurred while updating Machine details"),
	MACHINE_DELETE_EXCEPTION("KER-MSD-088", "Error occurred while deleting Machine details"),
	DEPENDENCY_EXCEPTION("KER-MSD-148", "Cannot delete as dependency found"),
	REGISTRATION_CENTER_MACHINE_FETCH_EXCEPTION("KER-MSD-XXX",
			"Error occurred while fetching a Machine details mapped with the given Registration Center"),
	MACHINE_REGISTRATION_CENTER_NOT_FOUND_EXCEPTION("KER-MSD-XXX", "Registration Center and Machine Not Found"),
	MAPPED_MACHINE_ID_NOT_FOUND_EXCEPTION("xxx","No Machine id mapped found"),
	MACHINE_ID_ALREADY_MAPPED_EXCEPTION("xxx","All Machine Id are mapped"),
	INVALID_MACHINE_FILTER_VALUE_EXCEPTION("xxx","Invalid filter value"),
	MACHINE_ID_NOT_FOUND_FOR_NAME_EXCEPTION("xxx","No Machine Id found for name:%s"),
	NO_DATA_FOR_FILTER_VALUES("KER-MSD-___","No Data Found for the given filter column"),
	MACHINE_SPECIFICATION_ID_NOT_FOUND_FOR_NAME_EXCEPTION("xxx","No Machine specification id found for name:%s");

	private final String errorCode;
	private final String errorMessage;

	private MachineErrorCode(final String errorCode, final String errorMessage) {
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
