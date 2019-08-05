package io.mosip.kernel.masterdata.constant;

public enum LocationErrorCode {
	LOCATION_FETCH_EXCEPTION("KER-MSD-025", "Error occured while fetching Location Hierarchy"),
	LOCATION_NOT_FOUND_EXCEPTION("KER-MSD-026", "Location not found"),
	LOCATION_LEVEL_FETCH_EXCEPTION("KER-MSD-027", "Error occured while fetching Location Hierarchy Levels"),
	LOCATION_INSERT_EXCEPTION("KER-MSD-064", "Error occured while inserting location hierarchy details"),
	LOCATION_UPDATE_EXCEPTION("KER-MSD-097", "Error occured wihile updating location hierarchy details"),
	LOCATION_DELETE_EXCEPTION("KER-MSD-098", "Error occured wihile deleting location hierarchy details"),
	LOCATION_LEVEL_NOT_FOUND_EXCEPTION("KER-MSD-028", "Location Hierarchy Level not found"),
	INVALID_LANG_CODE("KER-MSD_386","Invalid Language Code :"),
	INVALID_DIFF_HIERARCY_LEVEL("KER-MSD-389","Location hierarchy level should not be different in different languages"),
	DATA_IN_PRIMARY_LANG_MISSING("KER-MSD-388","Location data is not present in the primary language :"),
	DIFFERENT_LOC_CODE("KER-MSD-387","Location Code should not be different for a Location in different languages"),
	UNABLE_TO_ACTIVATE("KER-MSD-384","Cannot Activate the Location as data is not present in all the required languages"),
	LOCATION_ALREDAY_EXIST_UNDER_HIERARCHY("KER-MSD-385","Location %s already exist under the hierarchy"),
	LOCATION_CHILD_STATUS_EXCEPTION("KER-MSD-300", "Cannot deactivate the Location as active child Location are mapped"), NO_DATA_FOR_FILTER_VALUES("KER-MSD-___","No Data Found for the given filter column");

	private String errorCode;
	private String errorMessage;

	private LocationErrorCode(final String errorCode, final String errorMessage) {
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
