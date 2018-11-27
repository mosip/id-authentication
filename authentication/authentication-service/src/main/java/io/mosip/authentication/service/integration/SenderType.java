package io.mosip.authentication.service.integration;

/**
 * 
 * @author Dinesh Karuppiah.T
 */
public enum SenderType {

	AUTH("auth"), OTP("otp");

	private String name;

	SenderType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
