package io.mosip.registration.processor.status.code;
	
/**
 * Valid Status codes for Registration status table.
 * @author Girish Yarru
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
    
    /** The structure validation success. */
    STRUCTURE_VALIDATION_SUCCESS,
    
    /** The structure validation failed. */
    STRUCTURE_VALIDATION_FAILED,
    
    /** The packet not yet sync. */
    PACKET_NOT_YET_SYNC,
    
    /** The packet data store successful. */
    PACKET_DATA_STORE_SUCCESSFUL,
    
    /** The packet data store failed. */
    PACKET_DATA_STORE_FAILED,
    
    /** The packet osi validation successful. */
    PACKET_OSI_VALIDATION_SUCCESSFUL,
    
    /** The packet osi validation failed. */
    PACKET_OSI_VALIDATION_FAILED,
    
    /** The packet demo dedupe successful. */
    PACKET_DEMO_DEDUPE_SUCCESSFUL,
    
    /** The packet demo potential match. */
    PACKET_DEMO_POTENTIAL_MATCH,
    
    /** The packet demo dedupe failed. */
    PACKET_DEMO_DEDUPE_FAILED,
    
    /** The packet bio dedupe successful. */
    PACKET_BIO_DEDUPE_SUCCESSFUL,
    
    /** The packet bio potential match. */
    PACKET_BIO_POTENTIAL_MATCH,
    
    /** The packet bio dedupe failed. */
    PACKET_BIO_DEDUPE_FAILED,
    
    /** The uin generated. */
    UIN_GENERATED,
    
    /** The manual adjudication success. */
    MANUAL_ADJUDICATION_SUCCESS,
    
    /** The manual adjudication failed. */
    MANUAL_ADJUDICATION_FAILED

}
