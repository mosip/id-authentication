package io.mosip.registration.processor.camel.bridge.statuscode;

/**
 * This enum specifies the fields to be set in header for Processors and Routes.
 *
 * @author Pranav Kumar
 * @since 0.0.1
 */
public enum MessageEnum {

	/** The is valid. */
	IS_VALID("isValid"), 
	/** The retry count. */
	RETRY_COUNT("retryCount"), 
	/** The internal error. */
	INTERNAL_ERROR("internalError");

	/** The parameter. */
	private String parameter;

	/**
	 * Instantiates a new message enum.
	 *
	 * @param abc the abc
	 */
	private MessageEnum(String abc) {
		this.parameter = abc;
	}

	/**
	 * Gets the parameter.
	 *
	 * @return the parameter
	 */
	public String getParameter() {
		return this.parameter;
	}

}
