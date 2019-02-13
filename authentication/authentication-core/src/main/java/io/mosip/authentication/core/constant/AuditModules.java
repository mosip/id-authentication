package io.mosip.authentication.core.constant;

/**
 * The Enum AuditModules - Contains all the modules in IdAuthentication for Audit purpose.
 *
 * @author Manoj SP
 */
public enum AuditModules {

	
	/** The internal auth request. */
	INTERNAL_AUTH_REQUEST("IDA-MOD-111"),
	/** The otp auth. */
	OTP_AUTH("IDA-MOD-101","OTP Authentication requested"),
	
	/** The demo auth. */
	DEMO_AUTH("IDA-MOD-102","Demo Authentication requested"),
	
	/** The FINGERPRINT_AUTH. */
	FINGERPRINT_AUTH("IDA-MOD-103","Fingerprint Authentication requested"),
	
	/** The IRIS_AUTH. */
	IRIS_AUTH("IDA-MOD-104","Iris Authentication requested"),
	
	/** The FACE_AUTH. */
	FACE_AUTH("IDA-MOD-105","Face Authentication requested"),
	
	/** The pin auth. */
	PIN_AUTH("IDA-MOD-106","Pin Authentication requested"),
	
	/** The e KY C AUTH. */
	EKYC_AUTH("IDA-MOD-107","eKYC Authentication requested"),
	
	/** The Static Pin Storage */
	STATIC_PIN_STORAGE("IDA-MOD-108","Static Pin Storage requested"),
	
	/** The otp request. */
	OTP_REQUEST("IDA-MOD-110","OTP requested");

	/** The module id. */
	private final String moduleId;
	
	/** The Description*/
	private  String desc;

	
	
	/**
	 *  Instantiates a new audit contants. 
	 *  
	 * @param moduleId
	 * @param desc
	 */
	private AuditModules(String moduleId,String desc) {
		this.moduleId=moduleId;
		this.desc=desc;
	}
	/**
	 *  Instantiates a new audit contants. 
	 *  
	 * @param moduleId
	 * @param desc
	 */
	private AuditModules(String moduleId) {
		this.moduleId=moduleId;
	}
	
	public String getDesc() {
		return desc;
	}

	/**
	 * Gets the module id.
	 *
	 * @return the module id
	 */
	public String getModuleId() {
		return moduleId;
	}
	
	public String getModuleName() {
		return this.name();
	}

	
	
}
