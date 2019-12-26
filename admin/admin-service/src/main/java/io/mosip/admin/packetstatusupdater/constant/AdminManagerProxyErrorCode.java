package io.mosip.admin.packetstatusupdater.constant;

public enum AdminManagerProxyErrorCode {
	ADMIN_FETCH_EXCEPTION("ADM-PKT-000","Admin URL exicution exception");
	
	
	private final String errorCode;
	private final String errorMessage;

	private AdminManagerProxyErrorCode(final String errorCode, final String errorMessage) {
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
