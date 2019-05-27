package io.mosip.registration.processor.core.code;

/**
 * The Class SubBioType.
 *
 * @author Ranjitha Siddegowda
 */
public enum BioSubType {
	
	LEFT_INDEX_FINGER("Left IndexFinger"),
	LEFT_LITTLE_FINGER("Left LittleFinger"),
	LEFT_MIDDLE_FINGER("Left MiddleFinger"),
	LEFT_RING_FINGER("Left RingFinger"),
	RIGHT_INDEX_FINGER("Right IndexFinger"),
	RIGHT_LITTLE_FINGER("Right LittleFinger"),
	RIGHT_MIDDLE_FINGER("Right MiddleFinger"),
	RIGHT_RING_FINGER("Right RingFinger"),
	LEFT_THUMB("Left Thumb"),
	RIGHT_THUMB("Right Thumb"),
	IRIS_LEFT("Left"),
	IRIS_RIGHT("Right"),
	FACE("Face");

	/** The error message. */
	private final String bioType;

	/**
	 * Instantiates a new platform error messages.
	 *
	 * @param errorCode
	 *            the error code
	 * @param errorMsg
	 *            the error msg
	 */
	private BioSubType(String bioType) {
		this.bioType = bioType;
	}

	public String getBioType() {
		return bioType;
	}
}
