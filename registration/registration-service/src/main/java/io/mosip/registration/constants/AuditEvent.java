package io.mosip.registration.constants;

/**
 * Enum for Audit Events
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public enum AuditEvent {

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
	UI_SCHEDULER_STARTED("PKT_SCH", "Scheduler","Scheduler started Sucessfully"),
	//Login and User related
	LOGIN_MODES_FETCH("LGN_MOD","Login Modes","Fetching Login Modes"),
	USER_STATUS_FETCH("USR_STS","User Status","Fetching User Status"),
	VALIDATE_USER_CRED("VLD_USR","Validate User","Validating User credentials"),
	FETCH_USR_DET("USR_DTL","User Detail","Fetching User Details"),
	FETCH_CNTR_NAME("CNT_NME","Center Name","Fetching Center Name"),
	FETCH_CNTR_DET("CNT_DET","Center Details","Fetching Center Details"),
	FETCH_USR_ROLE("USR_RLE","User Roles","Fetching User Roles"),
	FETCH_SCR_AUTH("SCR_ATH","Screen AUthorization","Fetching screens to be authorized"),
	SYNCJOB_INFO_FETCH("SYNC_INFO", "Sync Information", "SyncJobInfo containing the synccontrol list and yettoexportpacket count fetched successfully"),
	SYNC_INFO_VALIDATE("SYNC_INFV", "Sync Information validation", "Validating the sync status ended successfully"),
	SYNC_PKT_COUNT_VALIDATE("SYNC_PKT_CNT", "Sync Packet Count validation", "Validating yet to export packets frequency with the configured limit count"),
	PACKET_RETRIVE("PKT_RETRIVE", "Retiving created packets", "Packets which are in created state for approval are retrived"),
	PACKET_UPDATE("PKT_UPDATE", "Updating created packets status", "Packets which are in created state are updated"),
	SYNC_GEO_VALIDATE("SYNC_INFO", "Geo Information validation", "Validating the geo information ended successfully"),
	DEVICE_MAPPING_SUCCESS("CMD_MAP","Success","device mapped successfully"),
	DEVICE_UN_MAPPING("CMD_UMAP","Success","device is unmapped "),
	
	//Sync Packets
	SYNC_SERVER("SYNC_INFO", "User", "Synchronize the packet status to the server"),
	
	//Packet Upload
	PACKET_UPLOAD("PKT_UPLD", "User", "Upload the local packets to the server"),
	
	// Device Onboarding
	GET_ONBOARDING_DEVICES_TYPES("DVC_TYP", "Get Devices Types", "Get the types of devices for device onboarding"),
	GET_ONBOARDING_DEVICES("GET_DVC", "Get Devices", "Get the available and mapped devices for the requested device type"),
	UPDATE_DEVICES_ONBOARDING("UPD_DVC", "Update Devices", "Devices onboarding updated for the registration client"),
	// Notification Service
	// Notification Service
	NOTIFICATION_STATUS("NOT_SER", "Notification SERVICE", "Notification request status");
	
	/**
	 * The constructor
	 */
	private AuditEvent(String id, String type, String name) {
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
