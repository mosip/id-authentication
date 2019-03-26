package io.mosip.registration.processor.virus.scanner.job.exceptions.util;

/**
 * The Class StatusMessage.
 * 
 * @author Ranjitha Siddegowda
 */
public class StatusMessage {

	/**
	 * Instantiates a new status message.
	 */
	private StatusMessage() {

	}
	
	/** The Constant PACKET_UPLOADED_TO_FILESYSTEM. */
	public static final String PACKET_UPLOADED_TO_FILESYSTEM = "Packet successfully uploaded to File System";
	public static final String VIRUS_SCAN_FAILED = "Virus Scan failed, packet uploaded to Virus Scan Retry folder";
}
