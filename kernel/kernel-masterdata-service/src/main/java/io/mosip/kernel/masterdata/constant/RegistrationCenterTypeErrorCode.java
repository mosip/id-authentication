package io.mosip.kernel.masterdata.constant;

public enum RegistrationCenterTypeErrorCode {
	REGISTRATION_CENTER_TYPE_FETCH_EXCEPTION("KER-MSD-013","Error occured while fetching RegistrationCenterType details"),
	REGISTRATION_CENTER_TYPE_INSERT_EXCEPTION("KER-MSD-051","Error occured while inserting RegistrationCenterType details"),
	REGISTRATION_CENTER_TYPE_NOT_FOUND_EXCEPTION("KER-MSD-014","RegistrationCenterType not found"),
	REGISTRATION_CENTER_TYPE_UPDATE_EXCEPTION("KER-MSD-089","Error occured while updating RegistrationCenterType details"),
	REGISTRATION_CENTER_TYPE_DELETE_EXCEPTION("KER-MSD-090","Error occured while deleting RegistrationCenterType details"),
	REGISTRATION_CENTER_TYPE_DELETE_DEPENDENCY_EXCEPTION("xxx","Cannot delete dependency found");

	private final String errorCode;
	private final String errorMessage;

	private RegistrationCenterTypeErrorCode(final String errorCode, final String errorMessage) {
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
