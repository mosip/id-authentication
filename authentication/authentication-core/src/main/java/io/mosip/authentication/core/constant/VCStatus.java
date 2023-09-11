package io.mosip.authentication.core.constant;

public enum VCStatus {
	
	/** */	
	ACTIVE("ACTIVE"),

	/** */	
	INACTIVE("INACTIVE"),

	/** */	
	REVOKED("REVOKED");

	private String status;

	private VCStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}
	
}
