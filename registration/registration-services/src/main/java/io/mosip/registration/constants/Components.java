package io.mosip.registration.constants;

/**
 * Enum for Application Modules to be used in Audit
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public enum Components {

	LOGIN("REG-MOD-101", "Login"),
	NAVIGATION("REG-MOD-102", "Navigation"),
	REG_DEMO_DETAILS("REG-MOD-103", "Registration: DemographicsDetails"),
	REG_DOCUMENTS("REG-MOD-104", "Registration: Documents"),
	REG_BIOMETRICS("REG-MOD-105", "Registration: Biometrics"),
	REG_PREVIEW("REG-MOD-106", "Registration: Preview"),
	REG_OS_AUTH("REG-MOD-107", "Registration: OS Authentication"),
	REG_APPROVAL("REG-MOD-108", "Approve Registration"),
	SYNC_PACKET("REG-MOD-109", "Sync Packets"),
	UPLOAD_PACKET("REG-MOD-110", "Upload Packets"),
	VIRUS_SCAN("REG-MOD-111", "Virus Scan"),
	GEO_LOCATION("REG-MOD-112", "Geo-Location"),
	ON_BOARD_USER("REG-MOD-113", "On-board user"),
	SYNC_SERVER_TO_CLIENT("REG-MOD-114", "Server to Client Sync"),
	SYNC_CLIENT_TO_SERVER("REG-MOS-115", "Client to Server Sync"),
	EXPORT_REG_PACKETS("REG-MOD-116", "Client to Server Sync"),

	PACKET_HANDLER("REG-MOD-117", "Packet Handler"),
	PACKET_VALIDATOR("REG-MOD-118", "Packet Validator"),
	PACKET_CREATOR("REG-MOD-119", "Packet Creator"),
	PACKET_ENCRYPTOR("REG-MOD-120","Packet Encryptor"),
	PACKET_AES_ENCRYPTOR("REG-MOD-121","Packet AES Encryptor"),
	UI_SCHEDULER("REG-MOD-122","Scheduler Util"),

	//Login and User related
	LOGIN_MODES("REG-MOD-123","Login Modes"),
	USER_STATUS("REG-MOD-124","User Status"),
	VALIDATE_USER("REG-MOD-125","Validate User"),
	USER_DETAIL("REG-MOD-126","User Detail"),
	CENTER_NAME("REG-MOD-127","Center Name"),
	CENTER_DETAIL("REG-MOD-128","Center Detail"),
	USER_ROLE("REG-MOD-129","User Role"),
	SCREEN_AUTH("REG-MOD-130","Screen AUthorization"), 
	PACKET_RETRIVE("REG-MOD-131","Packet Retrive for Approval"), 
	PACKET_UPDATE("REG-MOD-132","Update Packet"), 
	SYNC_VALIDATE("REG-MOD-133","Sync service"),
		
	//Packet Upload
	PACKET_UPLOAD("REG-MOD-135", "Packet upload"),
	
	//Packet Sync
	PACKET_SYNC("REG-MOD-136", "Sync Packets"),

	//Notification Service
	NOTIFICATION_SERVICE("REG-MOD-137", "Notification"),
	
	DEVICE_MAPPING("REG-MOD-138","device mapping"),
	DEVICE_UN_MAPPING("REG-MOD-139","device un mapping"),
	
	//Registration Controller
	REGISTRATION_CONTROLLER("REG-MOD-140","Registration initialization"),
	//Machine is re mapped
	CENTER_MACHINE_REMAP("REG-MOD-141","Center Machine is Remapped"),
	PACKET_SYNCHED("REG-MOD-142","packet statuses are synched from server to client"),
	PACKET_STATUS_SYNCHED("REG-MOD-143","packet are synched from client to server"),
	CLEAN_UP("REG-MOD-144","all the data is cleaned up"),
	PACKETS_UPLOADED("REG-MOD-145","all the pakets are uploaded"),
	//Scheduler
	REFRESH_TIMEOUT("REG-MOD-146","refresh timeout"),
	SESSION_TIMEOUT("REG-MOD-147","session timeout"),
	

	MDM_CAPTURE_FAIELD("REG-MOD-148","capture failed"),
	MDM_CAPTURE_SUCESS("REG-MOD-149","capture successfull"),
	MDM_NO_DEVICE_AVAILABLE("REG-MOD-150","no device found"),
	MDM_DEVICE_FOUND("REG-MOD-151","Device found");

	/**
	 * The constructor
	 */
	private Components(String id, String name) {
		this.id = id;
		this.name = name;
	}

	private final String id;
	private final String name;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
