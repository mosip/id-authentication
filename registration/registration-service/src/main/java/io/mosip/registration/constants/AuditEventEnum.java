package io.mosip.registration.constants;

/**
 * Enum for Audit Events
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public enum AuditEventEnum {

	PACKET_CREATION_SUCCESS("PKT_CRT", "Success", "Packet Succesfully Created"),
	PACKET_ENCRYPTED("PKT_ENC", "Encrypted","Packet Encrypted Sucessfully"),
	PACKET_UPLOADED("PKT_UPL", "Uploaded","Packet Uploaded Successfully"),
	PACKET_SYNCED_TO_SERVER("PKT_SYN", "Synced to Server","Packet Synced to Server Sucesfully"),
	PACKET_DELETED("PKT_DLT", "Deleted","Packet Deleted Successfully"),
	PACKET_APPROVED("PKT_APR", "Approved","Packet Approved Successfully"),
	PACKET_REJECTED("PKT_RJC", "Rejected","Packet Rejected Successfully"),
	PACKET_HOLDED("PKT_HLD", "Holded","Packet Holded for particular reason"),
	PACKET_INTERNAL_ERROR("PKT_INE", "Internal Error", "Packet Creation Error"),
	PACKET_INTERNAL_ZIP("PKT_ZIP", "Internal Packet Zip", "Packet internally zipped successfully"),
	PACKET_DEMO_JSON_CREATED("PKT_DGJ", "Demographic JSON", "Packet Demographic JSON created successfully"),
	PACKET_HMAC_FILE_CREATED("PKT_HMF", "HMAC File", "Packet HMAC File created successfully"),
	PACKET_META_JSON_CREATED("PKT_MDJ", "Packet Meta Data JSON", "Packet Meta-Data JSON created successfully"),
	PACKET_AUDIT_JSON_CREATED("PKT_AUD", "Packet Audit JSON", "Packet Audit JSON created successfully"),
	PACKET_AES_ENCRYPTED("PKT_AES", "AES Encrypted","Packet Encrypted Sucessfully"),
	UI_SCHEDULER_STARTED("PKT_SCH", "Scheduler","Scheduler started Sucessfully");
	
	/**
	 * The constructor
	 */
	private AuditEventEnum(String id, String type, String name) {
		this.id = id;
		this.type = type;
		this.name = name;
	}
	
	private final String id;
	private final String type;
	private final String name;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
