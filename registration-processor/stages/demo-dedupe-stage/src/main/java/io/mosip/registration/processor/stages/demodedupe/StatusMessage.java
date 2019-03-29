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
	public static final String POTENTIAL_MATCH_FOUND="Potential match found in DB";

	/** The Constant PACKET_DEMO_DEDUPE_SUCCESSFUL. */
	public static final String DEMO_DEDUPE_SUCCESS = "Packet demo dedupe sucessful";
	
	/** The Constant PACKET_DEMO_POTENTIAL_MATCH. */
	public static final String DEMO_POTENTIAL_MATCH ="Potential match found while processing demo dedupe";
	
	/** The Constant PACKET_STRUCTURAL_VALIDATION_SUCCESS. */
	public static final String DEMO_DEDUPE_FAILED ="Packet demo dedupe failed";
	
	/** The Constant INPUTSTREAM_NOT_READABLE. */
	public static final String INPUTSTREAM_NOT_READABLE = "Unable to read inputstream";
}
