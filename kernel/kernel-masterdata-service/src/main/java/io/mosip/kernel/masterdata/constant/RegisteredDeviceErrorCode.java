package io.mosip.kernel.masterdata.constant;

public enum RegisteredDeviceErrorCode {
	
	
	DEVICE_PROVIDER_NOT_EXIST("ADM-DPM-000","%s Device Provider Id Not exist"),
	REGISTERED_DEVICE_INSERTION_EXCEPTION("ADM-DPM-000","Error occurred while registering Registered Device"),
	SERIALNUM_NOT_EXIST("ADM-DPM-snn","%s Serial Num Not exist"),
	STATUS_CODE_VALIDATION_EXCEPTION("KER-DPM-sss", "Error occured while validating Status Code"),
	CERTIFICATION_LEVEL_VALIDATION_EXCEPTION("KER-DPM-ccc", " Error occured while validating Certification Level"),
    PURPOSEVALIDATION_EXCEPTION("KER-DPM-ppp", " Error occured while validating Purpose Value");
	

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
