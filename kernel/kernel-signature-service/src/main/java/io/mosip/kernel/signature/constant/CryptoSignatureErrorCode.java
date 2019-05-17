package io.mosip.kernel.signature.constant;

/**
 * Constants for CryptoSignaure 
 * 
 * @author Uday Kumarl
 * @since 1.0.0
 *
 */
public enum CryptoSignatureErrorCode {
	
	INTERNAL_SERVER_ERROR("KER-CSS-500", "Internal server error");

	private final String errorCode;
	private final String errorMessage;

	private CryptoSignatureErrorCode(final String errorCode, final String errorMessage) {
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
