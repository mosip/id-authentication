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
	public static final String POTENTIAL_MATCH_FOUND="Potential match found for registration id";

	/** The Constant PACKET_DEMO_DEDUPE_SUCCESSFUL. */
	public static final String DEMO_DEDUPE_SUCCESS = "Packet demo dedupe sucessful";
	
	/** The Constant PACKET_STRUCTURAL_VALIDATION_SUCCESS. */
	public static final String DEMO_DEDUPE_FAILED ="Packet demo dedupe failed";
	
	public static final String POTENTIAL_MATCH_FOUND_IN_ABIS="Potential match found in abis response for registration id :";
	
	public static final String DEMO_DEDUPE_FAILED_IN_ABIS ="Packet demo dedupe failed in abis";

	
}
