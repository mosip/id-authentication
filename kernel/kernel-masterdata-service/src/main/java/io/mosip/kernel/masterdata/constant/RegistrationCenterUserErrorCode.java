package io.mosip.kernel.masterdata.constant;

/**
 * 
 * @author Megha Tanga
 *
 */
public enum RegistrationCenterUserErrorCode {
	
	REGISTRATION_CENTER_USER_ALREADY_UNMAPPED_EXCEPTION("KER-MSD-423", "User Id %s - Center Id %s mapping does not exist"),
	USER_AND_REG_CENTER_MAPPING_NOT_FOUND_EXCEPTION("KER-MSD-423","User Id %s - Center Id %s mapping does not exist"),
	REGISTRATION_CENTER_USER_MAPPING_EXCEPTION("KER-MSD-424","Error occurred while mapping User to Registration Center "),
	REGISTRATION_CENTER_USER_UNMAPPING_EXCEPTION("KER-MSD-425","Error occurred while unmapping User to Registration Center "),
	REGISTRATION_CENTER_USER_ALREADY_MAPPED_EXCEPTION("KER-MSD-426", "Registration Center-User mapping already exist"),
	REGISTRATION_CENTER_USER_INSERT_EXCEPTION("KER-MSD-424","Error occurred while mapping User to Registration Center"),
	REGISTRATION_CENTER_NOT_FOUND ("KER-MSD-428", "Registration Center is Decommission state or deoes not exist"),
	INVALIDE_USER_ZONE("KER-MSD-429", "Admin not authorized to map/un-map this User"),
	INVALIDE_CENTER_ZONE("KER-MSD-430", "Admin not authorized to map/unmap to this Registration Center"),
	INVALIDE_CENTER_USER_ZONE("KER-MSD-431", "User cannot be mapped/un-mappped to the Center as Center and User does not belong to the same Administrative Zone"),
	USER_NOT_FOUND ("KER-MSD-427", "User is not map to any Zone"),
	USER_MAPPED_REGISTRATION_CENTER("KER-MSD-432", "Cannot map the User as it is mapped to another Registration Center");
	
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
