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
	DATABASE_EXCEPTION("ADM-DPM-009","Error occurred while checking a Device Details %s"),
	DEVICE_PROVIDER_NOT_EXIST("ADM-DPM-003","Device Provider does not exist"),
	DEVICE_PROVIDER_INACTIVE("ADM-DPM-004","Device Provider is marked Inactive or not found"),
	MDS_DOES_NOT_EXIST("ADM-DPM-005","MDS does not exist"),
	MDS_INACTIVE_STATE("ADM-DPM-006","MDS in inactive state"),
	SOFTWARE_VERSION_IS_NOT_A_MATCH("ADM-DPM-007","Software version does not match against the Device Details"),
	PROVIDER_AND_DEVICE_CODE_NOT_MAPPED("ADM-DPM-008","Device code does not match against - %s"),
	PROVIDER_AND_TYPE_MAPPED("ADM-DPM-008","Device code does not match against Type"),
	PROVIDER_AND_SUBTYPE_MAPPED("ADM-DPM-008","Device code does not match against SubType"),
	DEVICE_PROVIDER_INSERTION_EXCEPTION("ADM-DPM-012", "Error occurred while registering Device Provider"),
	DEVICE_PROVIDER_EXIST("ADM-DPM-011","%s Device Provider already exist"),
	DEVICE_PROVIDER_UPDATE_EXCEPTION("ADM-DPM-014", "Error occurred while updating a Device Provider"),
	DEVICE_PROVIDER_DEVICE_CODE_NOT_MAPPED("ADM-DPM-015","Device code not mapped against device provider");

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
