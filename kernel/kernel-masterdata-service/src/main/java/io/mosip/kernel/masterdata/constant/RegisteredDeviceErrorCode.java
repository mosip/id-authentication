package io.mosip.kernel.masterdata.constant;

public enum RegisteredDeviceErrorCode {
	
	
	DEVICE_PROVIDER_NOT_EXIST("ADM-DPM-032","Device Provider ID/Name does not exist in the list of Registered Device Providers"),
	REGISTERED_DEVICE_INSERTION_EXCEPTION(" ADM-DPM-035","Error occurred while storing Registered Device Details"),
	SERIALNUM_NOT_EXIST("ADM-DPM-042","%s Cannot register device as it is not a white-listed device"),
	STATUS_CODE_VALIDATION_EXCEPTION("ADM-DPM-028", "Error occured while validating Status Code"),
	TYPE_VALIDATION_EXCEPTION("KER-DPM-sss", "Error occured while validating Type Value"),
	CERTIFICATION_LEVEL_VALIDATION_EXCEPTION("ADM-DPM-034", " Error occured while validating Certification Level"),
    PURPOSE_VALIDATION_EXCEPTION("ADM-DPM-033", "Error occured while validating Purpose Value"),
    DEVICE_TYPE_NOT_EXIST("ADM-DPM-026","%s Device Type does not exist"),
    DEVICE_SUB_TYPE_NOT_EXIST("ADM-DPM-027","%s Device Sub-Type does not exist"),
    FOUNDATIONAL_VALUE("ADM-DPM-030", "Error occured while validating Foundational");
	

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
