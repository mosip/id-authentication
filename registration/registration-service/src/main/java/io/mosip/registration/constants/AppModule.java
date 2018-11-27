package io.mosip.registration.constants;

/**
 * Enum for Application Modules to be used in Audit
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public enum AppModule {

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
	DEVICE_UN_MAPPING("REG-DVUM-23","device un mapping");
	/**
	 * The constructor
	 */
	private AppModule(String id, String name) {
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
