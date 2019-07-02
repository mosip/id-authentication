package io.mosip.registration.processor.packet.receiver.constants;

public class PacketReceiverConstant {
	/**
	 * Private Constructor for this class
	 */
	private PacketReceiverConstant() {

	}

	public static final String IOEXCEPTION_IN_PACKET_RECIVER = "IOException in packet receiver for registrationId";
	public static final String DATA_ACCESS_EXCEPTION_IN_PACKET_RECIVER = "DataAccessException in packet receiver for registrationId";
	public static final String ERROR_IN_PACKET_RECIVER = "Error while updating status : ";
	public static final String PACKETNOTSYNC_IN_PACKET_RECIVER = "PacketNotSync exception in packet receiver for registartionId ";
	public static final String PACKET_SUCCESS_UPLOADED_IN_PACKET_RECIVER = "Packet sucessfully uploaded for registrationId ";
	public static final String PACKET_VIRUS_SCAN_FAILED_PR = "Packet virus scan failed  in packet receiver for registrationId ::";
	public static final String UNEQUAL_PACKET_HASH_PR ="The Registration Packet HashSequence is not equal as synced packet HashSequence";
	public static final String API_RESOURCE_UNAVAILABLE ="API resource not accessible : ";
	public static final String PACKET_DECRYPTION_FAILED ="Packet decryption failed for registrationId ";
	public static final String PACKET_RECEIVER_VALIDATION_SUCCESS ="Packet receiver validation success for registrationId ";

	
	
}
