package org.mosip.registration.processor.status.code;

/**
 * Valid Status codes for Registration status table.
 *
 */
public enum RegistrationStatusCode {

	PACKET_UPLOADED_TO_LANDING_ZONE, 
	PACKET_FOR_VIRUS_SCAN,
	PACKET_FOR_VIRUS_SCAN_RETRY,
	PACKET_VIRUS_SCAN_DONE, 
	PACKET_UPLOADED_TO_DFS, 
	DUPLICATE_PACKET_RECIEVED, 
	INVALID_PACKET_FORMAT, 
	PACKET_NOT_PRESENT_IN_REQUEST,
	PACKET_SIZE_GREATER_THAN_LIMIT,
}
