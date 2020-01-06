package io.mosip.registration.processor.biodedupe.stage.utils;

/**
 * The Class StatusMessage.
 */
public final class StatusMessage {

	/**
	 * Instantiates a new status message.
	 */
	private StatusMessage() {

	}

	/** The Constant PACKET_BIOMETRIC_INSERTION_TO_ABIS. */
	public static final String PACKET_BIOMETRIC_INSERTION_TO_ABIS = "Packet biometric insertion to ABIS is failed";

	/** The Constant PACKET_BIODEDUPE_SUCCESS. */
	public static final String PACKET_BIODEDUPE_SUCCESS = "Packet biodedupe successful";

	public static final String PACKET_BIODEDUPE_INPROGRESS = "Packet biodedupe Inprogress";

	/** The Constant PACKET_BIOMETRIC_POTENTIAL_MATCH. */
	public static final String PACKET_BIOMETRIC_POTENTIAL_MATCH = "Potential match found while processing bio dedupe";

	public static final String PACKET_BIOMETRIC_LOST_PACKET_NO_MATCH = "No Match was Found for the Given Biometrics";

	public static final String PACKET_BIOMETRIC_LOST_PACKET_UNIQUE_MATCH = "Unique Match was Found for the Given Biometrics";

	public static final String PACKET_BIOMETRIC_LOST_PACKET_MULTIPLE_MATCH = "Multiple Match was Found for the Given Biometrics";

}
