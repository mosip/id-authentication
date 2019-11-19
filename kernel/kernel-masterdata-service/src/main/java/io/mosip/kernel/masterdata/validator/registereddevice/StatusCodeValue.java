package io.mosip.kernel.masterdata.validator.registereddevice;


/**
 * Filter field value
 * 
 * @author Megha Tanga
 * @since 1.0.0
 */
public enum StatusCodeValue {
	REGISTERED("registered"),
	RETIRED("retired"),
	REVOKED("revoked");
	
	private String type;

	private StatusCodeValue(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}
}

