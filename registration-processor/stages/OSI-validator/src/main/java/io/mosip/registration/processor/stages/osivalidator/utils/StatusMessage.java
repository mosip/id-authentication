package io.mosip.registration.processor.stages.osivalidator.utils;

public class StatusMessage {

	/**
	 * Instantiates a new status message.
	 */
	private StatusMessage() {

	}

	public static final String OPERATOR = "OPERATOR";
	public static final String SUPERVISOR = "SUPERVISOR";
	public static final String INTRODUCER = "INTRODUCER";
	public static final String FINGER_PRINT = "FINGER_PRINT_VALIDATION_FAILED";
	public static final String IRIS = "IRIS_VALIDATION_FAILED";
	public static final String FACE = "FACE_VALIDATION_FAILED";
	public static final String PIN = "PIN_VALIDATION_FAILED";
	public static final String VALIDATION_DETAILS = "ALL_THE_VALIDATION_DETAILS_ARE_NULL";
	public static final String OSI_VALIDATION_SUCCESS = "OSI_VALIDATION_SUCCESS";

}
