package io.mosip.registration.constants;

/**
 * Enum for Application Modules to be used in Audit
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public enum Components {

	PACKET_HANDLER("REG-PRO-01", "Packet Handler"),
	PACKET_VALIDATOR("REG-PRO-02", "Packet Validator"),
	PACKET_CREATOR("REG-PRO-03", "Packet Creator"),
	PACKET_ENCRYPTOR("REG-PRO-04","Packet Encryptor"),
	PACKET_AES_ENCRYPTOR("REG-PRO-05","Packet AES Encryptor"),
	UI_SCHEDULER("REG-PRO-06","Scheduler Util"),
	//Login and User related
	LOGIN_MODES("REG-LGN-07","Login Modes"),
	USER_STATUS("REG-USR-08","User Status"),
	VALIDATE_USER("REG-VLD-09","Validate User"),
	USER_DETAIL("REG-DTL-10","User Detail"),
	CENTER_NAME("REG-CTR-11","Center Name"),
	CENTER_DETAIL("REG-CTR-12","Center Detail"),
	USER_ROLE("REG-USR-13","User Role"),
	SCREEN_AUTH("REG-SCR_14","Screen AUthorization"), 
	PACKET_RETRIVE("REG-PRO-15","Packet Retrive for Approval"), 
	PACKET_UPDATE("REG-PRO-16","Update Packet"), 
	SYNC_VALIDATE("REG-PRO-17","Sync service"),
	
	// Device Onboarding
	DEVICE_ONBOARD("REG-UI-18", "Device Onboarding"),
	
	//Packet Upload
	PACKET_UPLOAD("REG-CTR-19", "Packet upload"),
	
	//Packet Sync
	PACKET_SYNC("REG-CTR-20", "Sync Packets"),
	//Notification Service
	NOTIFICATION_SERVICE("REG-NOT-21", "Notification"),
	
	DEVICE_MAPPING("REG-DVM-22","device mapping"),
	DEVICE_UN_MAPPING("REG-DVUM-23","device un mapping"),
	
	//Registration Controller
	REGISTRATION_CONTROLLER("REG-DVM-24","Registration initialization"),

	LOGIN("REG-LOG", "Login"),
	NAVIGATION("REG-NAV", "Navigation"),
	REG_DEMO_DETAILS("REG-DEMO", "Registration: DemographicsDetails"),
	REG_DOCUMENTS("REG-DOC", "Registration: Documents"),
	REG_BIOMETRICS("REG-BIO", "Registration: Biometrics"),
	REG_PREVIEW("REG-PVW", "Registration: Preview"),
	REG_OS_AUTH("REG-OSA", "Registration: OS Authentication"),
	REG_APPROVAL("REG-APP", "Approve Registration"),
	SYNC_PACKET("REG-SYN", "Sync Packets"),
	UPLOAD_PACKET("REG-UPL", "Upload Packets"),
	VIRUS_SCAN("REG-VIR", "Virus Scan"),
	GEO_LOCATION("REG-GEO", "Geo-Location"),
	ON_BOARD_USER("REG-USR", "On-board user"),
	SYNC_SERVER_TO_CLIENT("REG-SSC", "Server to Client Sync"),
	SYNC_CLIENT_TO_SERVER("REG-SCS", "Client to Server Sync"),
	EXPORT_REG_PACKETS("REG-SCS", "Client to Server Sync");

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
