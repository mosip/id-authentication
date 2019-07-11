package io.mosip.admin.masterdata.constant;

/**
 * Masterdata card error constants
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
public enum MasterDataErrorConstant {

	DATANOTFOUND("ADM-MSTR-001", "The data is not found for the passed language code");

	private final String errorCode;
	private final String errorMessage;

	private MasterDataErrorConstant(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String errorCode() {
		return this.errorCode;
	}

	public String errorMessage() {
		return this.errorMessage;
	}
}
