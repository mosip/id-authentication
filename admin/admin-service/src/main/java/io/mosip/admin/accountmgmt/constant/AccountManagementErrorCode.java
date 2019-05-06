package io.mosip.admin.accountmgmt.constant;

public enum AccountManagementErrorCode {
   INTERNAL_SERVER_ERROR("ADM-ACM-100","Runtime exception");
	
	private final String errorCode;
	private final String errorMessage;

	private AccountManagementErrorCode(String errorCode, String errorMessage) {
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
