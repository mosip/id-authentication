package io.mosip.registration.processor.camel.bridge.statuscode;

/**
 * This enum specifies the fields to be set in header for Processors and Routes
 * 
 * @author Pranav Kumar
 * @since 0.0.1
 */
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
