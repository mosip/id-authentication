package io.mosip.kernel.masterdata.constant;
/**
 * Zone Error Codes and Messages
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
public enum ZoneErrorCode {

	ZONE_FETCH_EXCEPTION("KER-MSD-337", "Error occured while fetching zone"),
	USER_ZONE_UNAVAILABLE("KER-MSD-339","No zone found for the logged-in user %s"),
	USER_ZONE_FETCH_EXCEPTION("KER-MSD-338","Error Occured while fetching zone of the user"),
	ZONEUSER_ENTITY_NOT_FOUND("KER-MSD-___","Data Not Found"),
	ZONE_ENTITY_NOT_FOUND("KER-MSD-___","Data Not Found"),
	INTERNAL_SERVER_ERROR("KER-MSD-___","Internal Server Error");

	private final String errorCode;
	private final String errorMessage;

	private ZoneErrorCode(final String errorCode, final String errorMessage) {
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
