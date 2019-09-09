package io.mosip.kernel.masterdata.constant;

/**
 * 
 * @author Megha Tanga
 *
 */
public enum RegistrationCenterUserErrorCode {
	
	REGISTRATION_CENTER_USER_ALREADY_UNMAPPED_EXCEPTION("KER-MSD-xxx", "User is already unmapped from the received Registration Center"),
	USER_AND_REG_CENTER_MAPPING_NOT_FOUND_EXCEPTION("KER-MSD-xxx","User Id %s - Registration Center Id %s Mapping does not exist"),
	INVALIDE_ZONE("KER-MSD-xxx", "User or Registration center does not belong to user"),
	REGISTRATION_CENTER_USER_FETCH_EXCEPTION("KER-MSD-xxX","Error occurred while fetching Center Device details");
	
	private final String errorCode;
	private final String errorMessage;

	private RegistrationCenterUserErrorCode(final String errorCode, final String errorMessage) {
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
