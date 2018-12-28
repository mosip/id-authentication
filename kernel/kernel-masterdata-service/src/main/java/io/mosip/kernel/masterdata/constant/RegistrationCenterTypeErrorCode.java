package io.mosip.kernel.masterdata.constant;

public enum RegistrationCenterTypeErrorCode {
	REGISTRATION_CENTER_TYPE_FETCH_EXCEPTION("KER-MSD-013","Error occurred while fetching Registration Center Type details"),
	REGISTRATION_CENTER_TYPE_INSERT_EXCEPTION("KER-MSD-050","Error occurred while inserting Registration Center Type details"),
	REGISTRATION_CENTER_TYPE_NOT_FOUND_EXCEPTION("KER-MSD-120","Registration Center Type not found"),
	REGISTRATION_CENTER_TYPE_UPDATE_EXCEPTION("KER-MSD-109","Error occurred while updating Registration Center Type details"),
	REGISTRATION_CENTER_TYPE_DELETE_EXCEPTION("KER-MSD-110","Error occurred while deleting Registration Center Type details"),
	REGISTRATION_CENTER_TYPE_DELETE_DEPENDENCY_EXCEPTION("KER-MSD-127","Cannot delete dependency found");

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
