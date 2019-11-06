package io.mosip.kernel.masterdata.constant;

public enum RegisteredDeviceErrorCode {
	
	
	DEVICE_PROVIDER_NOT_EXIST("ADM-DPM-000","%s Device Provider Id Not exist"),
	REGISTERED_DEVICE_INSERTION_EXCEPTION("ADM-DPM-000","Error occurred while registering Registered Device");
	

	private final String errorCode;
	private final String errorMessage;

	private RegisteredDeviceErrorCode(final String errorCode, final String errorMessage) {
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
