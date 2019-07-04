package io.mosip.registration.processor.core.code;

/**
 * The Class SubBioType.
 *
 * @author Ranjitha Siddegowda
 */
public enum BioSubType {
	
	LEFT_INDEX_FINGER("LeftIndexFinger"),
	LEFT_LITTLE_FINGER("LeftLittleFinger"),
	LEFT_MIDDLE_FINGER("LeftMiddleFinger"),
	LEFT_RING_FINGER("LeftRingFinger"),
	RIGHT_INDEX_FINGER("RightIndexFinger"),
	RIGHT_LITTLE_FINGER("RightLittleFinger"),
	RIGHT_MIDDLE_FINGER("RightMiddleFinger"),
	RIGHT_RING_FINGER("RightRingFinger"),
	LEFT_THUMB("LeftThumb"),
	RIGHT_THUMB("RightThumb"),
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
