package io.mosip.registration.processor.packet.receiver.util;

/**
 * The Class StatusMessage.
 */
public class StatusMessage {
	/**
	 * Instantiates a new status message.
	 */
	private StatusMessage() {

	}

	/** The Constant PACKET_UPLOADED_VIRUS_SCAN. */
	public static final String PACKET_UPLOADED_TO_LANDING_ZONE = "Packet is in PACKET_UPLOADED_TO_LANDING_ZONE status";

	public static final String PACKET_RECEIVED = "Packet is in PACKET_RECEIVED status";
	
	public static final String VIRUS_SCANNER_SERVICE_FAILED = "Virus Scanner Service Failure";
	
	public static final String VIRUS_SCAN_FAILED = " The Packet is virus infected";
	
	public static final String PACKET_DECRYPTION_FAILED = "The packet decryption failed";

}
