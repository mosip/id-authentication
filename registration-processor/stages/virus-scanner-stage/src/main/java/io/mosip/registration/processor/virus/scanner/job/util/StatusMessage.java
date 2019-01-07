package io.mosip.registration.processor.virus.scanner.job.util;

/**
 * The Class StatusMessage.
 */
public class StatusMessage {
	/**
	 * Instantiates a new status message.
	 */
	private StatusMessage() {

	}

	/** The Constant PACKET_DECRYPTION_FAILURE. */
	public static final String PACKET_DECRYPTION_FAILURE = "packet is in status packet for decryption failed";

	/** The Constant PACKET_VIRUS_SCAN_FAILURE. */
	public static final String PACKET_VIRUS_SCAN_FAILURE = "packet is in status PACKET_FOR_VIRUS_SCAN_FAILED";

	/** The Constant PACKET_VIRUS_SCAN_SUCCESS. */
	public static final String PACKET_VIRUS_SCAN_SUCCESS = "Packet virus scan is sucessfull";

}
