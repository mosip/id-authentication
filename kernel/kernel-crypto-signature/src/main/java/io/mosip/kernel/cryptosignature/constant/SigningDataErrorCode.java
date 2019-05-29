package io.mosip.kernel.cryptosignature.constant;

/**
 * @author Srinivasan
 * @author Urvil Joshi
 *
 */
public enum SigningDataErrorCode {

	RESPONSE_PARSE_EXCEPTION("KER-SGN-100", "Error occured while parsing data"),
	REST_CRYPTO_CLIENT_EXCEPTION("KER-SGN-101","Error occured while calling an Keymanger sign API"),
	REST_KM_CLIENT_EXCEPTION("KER-SGN-102","Error occured while fetching Public Key");

	private final String errorCode;
	private final String errorMessage;

	private SigningDataErrorCode(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}
}
