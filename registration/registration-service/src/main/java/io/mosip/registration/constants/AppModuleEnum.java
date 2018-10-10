package io.mosip.registration.constants;

/**
 * Enum for Application Modules to be used in Audit
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public enum AppModuleEnum {

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
	USER_DETAIL("REG-DTL-010","User Detail"),
	CENTER_NAME("REG-CTR-011","Center Name"),
	CENTER_DETAIL("REG-CTR-012","Center Detail"),
	USER_ROLE("REG-USR-013","User Role"),
	SCREEN_AUTH("REG-SCR_014","Screen AUthorization"); 

	/**
	 * The constructor
	 */
	private AppModuleEnum(String id, String name) {
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
