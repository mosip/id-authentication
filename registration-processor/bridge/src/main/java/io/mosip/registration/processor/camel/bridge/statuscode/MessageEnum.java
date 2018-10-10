package io.mosip.registration.processor.camel.bridge.statuscode;

public enum MessageEnum {
	IS_VALID("isValid"), RETRY_COUNT("retryCount"), INTERNAL_ERROR("internalError");

	private String parameter;

	private MessageEnum(String abc) {
		this.parameter = abc;
	}

	public String getParameter() {
		return this.parameter;
	}

}
