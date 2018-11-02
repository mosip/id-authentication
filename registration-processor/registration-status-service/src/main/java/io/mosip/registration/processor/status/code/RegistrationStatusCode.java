package io.mosip.registration.processor.status.code;

/**
 * Valid Status codes for Registration status table.
 *
 */
public enum RegistrationStatusCode {

	/** The packet uploaded to landing zone. */
	PACKET_UPLOADED_TO_LANDING_ZONE, 
	
	/** The packet uploaded to virus scan. */
	PACKET_UPLOADED_TO_VIRUS_SCAN, 
	
	/** The virus scan failed. */
	VIRUS_SCAN_FAILED, 
	
	/** The virus scan successful. */
	VIRUS_SCAN_SUCCESSFUL, 
	
	/** The packet uploaded to filesystem. */
	PACKET_UPLOADED_TO_FILESYSTEM, 
	
	/** The duplicate packet recieved. */
	DUPLICATE_PACKET_RECIEVED, 
	
	/** The invalid packet format. */
	INVALID_PACKET_FORMAT, 
	
	/** The packet not present in request. */
	PACKET_NOT_PRESENT_IN_REQUEST, 
	
	/** The packet size greater than limit. */
	PACKET_SIZE_GREATER_THAN_LIMIT,
	
	/** The packet decryption successful. */
	PACKET_DECRYPTION_SUCCESSFUL,
	
	/** The packet decryption failed. */
	PACKET_DECRYPTION_FAILED, 
	
	/** The packet structural validation successfull. */
	PACKET_STRUCTURAL_VALIDATION_SUCCESSFULL,
	
	/** The packet structural validation failed. */
	PACKET_STRUCTURAL_VALIDATION_FAILED, 
	
	/** The packet not yet sync. */
	PACKET_NOT_YET_SYNC,
	
	/** The packet uploaded to dfs. */
	PACKET_UPLOADED_TO_DFS

}
