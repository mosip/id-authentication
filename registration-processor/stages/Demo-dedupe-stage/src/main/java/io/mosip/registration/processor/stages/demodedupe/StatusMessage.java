package io.mosip.registration.processor.stages.demodedupe;

/**
 * The Class StatusMessage.
 */
public final class StatusMessage {
	
	/**
	 * Instantiates a new status message.
	 */
	private StatusMessage() {
		
	}

	/** The Constant PACKET_DEMO_DEDUPE_SUCCESSFUL. */
	public static final String PACKET_DEMO_DEDUPE_SUCCESSFUL = "PACKET_DEMO_DEDUPE_SUCCESSFUL";
	
	/** The Constant PACKET_DEMO_POTENTIAL_MATCH. */
	public static final String PACKET_DEMO_POTENTIAL_MATCH ="PACKET_DEMO_POTENTIAL_MATCH";
	
	/** The Constant PACKET_STRUCTURAL_VALIDATION_SUCCESS. */
	public static final String PACKET_DEMO_DEDUPE_FAILED ="PACKET_DEMO_DEDUPE_FAILED";
	
	/** The Constant INPUTSTREAM_NOT_READABLE. */
	public static final String INPUTSTREAM_NOT_READABLE = "Unable to read inputstream";
}
