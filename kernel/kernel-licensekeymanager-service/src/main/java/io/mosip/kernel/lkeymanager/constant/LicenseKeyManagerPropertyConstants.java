package io.mosip.kernel.lkeymanager.constant;

public enum LicenseKeyManagerPropertyConstants {
	TIME_ZONE("UTC"), 
	DEFAULT_CREATED_BY("defaultadmin@mosip.io"), 
	MAPPED_STATUS("Mapped License with the permissions");
	
	private String value;

	private LicenseKeyManagerPropertyConstants(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

}
