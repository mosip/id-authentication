package io.mosip.kernel.masterdata.constant;

/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
public enum DeviceProviderManagementErrorCode {

	DEVICE_DOES_NOT_EXIST("ADM-DPM-001", "Device does not exist"), 
	DEVICE_REVOKED_OR_RETIRED("ADM-DPM-002","Device is revoked/retired"), 
	DATABASE_EXCEPTION("ADM-DPM-009","Error occurred while checking a Device Details"),
	DEVICE_PROVIDER_INACTIVE("ADM-DPM-004","Device Provider is marked Inactive"),
	MDS_DOES_NOT_EXIST("ADM-DPM-005","MDS does not exist"),
	MDS_INACTIVE_STATE("ADM-DPM-006","MDS in inactive state"),
	SOFTWARE_VERSION_IS_NOT_A_MATCH("ADM-DPM-007","Software version does not match against the Service ID"),
	PROVIDER_AND_SERVICE_ID_NOT_MAPPED("ADM-DPM-008","Device Provider ID does not match against the Service ID");

	private final String errorCode;
	private final String errorMessage;

	private DeviceProviderManagementErrorCode(final String errorCode, final String errorMessage) {
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
