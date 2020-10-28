package io.mosip.authentication.core.constant;

public enum AuthTokenType {
	RANDOM("Random"), PARTNER("Partner"), POLICY("Policy"), POLICY_GROUP("Policy Group");

	private final String type;

	private AuthTokenType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
}
