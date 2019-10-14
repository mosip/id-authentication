package io.mosip.kernel.masterdata.constant;

public enum MOSIPDeviceServiceErrorCode {
	
	REG_DEVICE_TYPE_NOT_FOUND("ADM-DPM-040", "Reg Device Type Code not found in the list of Reg Device Types"),
	REG_DEVICE_SUB_TYPE_NOT_FOUND("ADM-DPM-041", "Reg Device Sub Type Code not found in the list of Reg Device Sub Types"),
	DEVICE_PROVIDER_NOT_FOUND("ADM-DPM-041", "Device Provider Id not found in the list of Device Providers"),
	MDS_EXIST("ADM-DPM-021","%s MOSIP-Device-Service already exist"),
	MDS_INSERTION_EXCEPTION("ADM-DPM-022","Error occurred while storing MDS Details");

	private final String errorCode;
	private final String errorMessage;

	private MOSIPDeviceServiceErrorCode(final String errorCode, final String errorMessage) {
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
