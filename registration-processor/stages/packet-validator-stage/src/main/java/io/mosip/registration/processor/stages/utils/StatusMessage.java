package io.mosip.registration.processor.stages.utils;

/**
 * The Class StatusMessage.
 */
public final class StatusMessage {
	
	/**
	 * Instantiates a new status message.
	 */
	private StatusMessage() {
		
	}

	/** The Constant PACKET_CHECKSUM_VALIDATION_FAILURE. */
	public static final String PACKET_CHECKSUM_VALIDATION_FAILURE ="PACKET_CHECKSUM_VALIDATION_FAILURE";
	
	/** The Constant PACKET_FILES_VALIDATION_FAILURE. */
	public static final String PACKET_FILES_VALIDATION_FAILURE ="PACKET_FILES_VALIDATION_FAILURE";
	
	/** The Constant PACKET_STRUCTURAL_VALIDATION_SUCCESS. */
	public static final String PACKET_STRUCTURAL_VALIDATION_SUCCESS ="PACKET_STRUCTURAL_VALIDATION_SUCCESS";
	
	/** The Constant INPUTSTREAM_NOT_READABLE. */
	public static final String INPUTSTREAM_NOT_READABLE = "Unable to read inputstream";
}
