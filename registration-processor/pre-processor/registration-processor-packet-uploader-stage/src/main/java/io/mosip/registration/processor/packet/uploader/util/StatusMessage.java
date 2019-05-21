package io.mosip.registration.processor.packet.uploader.util;

/**
 * The Class StatusMessage.
 */
public class StatusMessage {
	/**
	 * Instantiates a new status message.
	 */
	private StatusMessage() {

	}

	public static final String VIRUS_SCANNER_SERVICE_FAILED = "Virus Scanner Service Failure";
	
	public static final String VIRUS_SCAN_FAILED = " The Packet is virus infected";
	
	public static final String PACKET_DECRYPTION_FAILED = "The packet decryption failed";

	public static final String PACKET_SYNC_HASH_VALIDATION_FAILED = "The packet sync hash code vaildation failed";
	
	public static final String PACKET_NOT_SYNC = "The packet is not synced in sync table";

}
