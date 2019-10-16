package io.mosip.kernel.masterdata.constant;

/**
 * Constants for device Details
 * 
 * @author Megha Tanga
 * @author Neha Sinha
 * @author Ramadurai Pandian
 * @since 1.0.0
 *
 */
public enum FoundationalTrustProviderErrorCode {
	ID_NOT_PRESENT("ADM-DPM-999", "FTP is not present for id"),
	MANDATORY_PARAM_MISSING("ADM-DPM-015", "Mandatory input parameter is missing"),
	FTP_ALREADY_PRESENT("ADM-DPM-016", "Foundational Trust Provider already exist"),
	FTP_REGISTER_ERROR("ADM-DPM-017", "Error occurred while registering a Foundational Trust Provider");

	private final String errorCode;
	private final String errorMessage;

	private FoundationalTrustProviderErrorCode(final String errorCode, final String errorMessage) {
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
