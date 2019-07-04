package io.mosip.authentication.core.constant;

/**
 * The Enum AuditModules - Contains all the modules in IdAuthentication for Audit purpose.
 *
 * @author Manoj SP
 */
public enum AuditModules {
	
	/** The otp auth. */
	OTP_AUTH("IDA-OTA","Request/Response, UIN/VID to be masked", "OTP Authenticator"),
	
	/** The demo auth. */
	DEMO_AUTH("IDA-DEA", "Request/Response, UIN/VID to be masked", "Demographic Authenticator"),
	
	/** The FINGERPRINT_AUTH. */
	FINGERPRINT_AUTH("IDA-FPA", "Request/Response, UIN/VID to be masked", "Fingerprint Authenticator"),
	
	/** The IRIS_AUTH. */
	IRIS_AUTH("IDA-ISA", "Request/Response, UIN/VID to be masked", "Iris Authenticator"),
	
	/** The FACE_AUTH. */
	FACE_AUTH("IDA-FAA", "Request/Response, UIN/VID to be masked", "Face Authenticator"),
	
	/** The e KY C AUTH. */
	EKYC_AUTH("IDA-EKA", "Request/Response, UIN/VID to be masked", "eKYC Authenticator"),
	
	/** The otp request. */
	OTP_REQUEST("IDA-OTR","Request/Response, UIN/VID to be masked", "Otp Requestor"),
	
	/** The pin auth. */
	PIN_AUTH("IDA-MOD-106","Pin Authentication requested", ""), //not applicable for release v1
	
	/** The Static Pin Storage */
	STATIC_PIN_STORAGE("IDA-MOD-108","Static Pin Storage requested", ""),//not applicable for release v1
	
	/** The vid generation request. */
	VID_GENERATION_REQUEST("IDA-MOD-109","VID Generation requested", "");//not applicable for release v1

	/** The module id. */
	private final String moduleId;
	
	/** The Description*/
	private  String desc;
	
	/** The module name. */
	private String moduleName;

	
	
	/**
	 *  Instantiates a new audit contants. 
	 *  
	 * @param moduleId
	 * @param desc
	 */
	private AuditModules(String moduleId, String desc, String moduleName) {
		this.moduleId=moduleId;
		this.desc=desc;
		this.moduleName=moduleName;
	}

	
	/**
	 * Gets the desc.
	 *
	 * @return the desc
	 */
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
	
	/**
	 * Gets the module name.
	 *
	 * @return the module name
	 */
	public String getModuleName() {
		return this.moduleName;
	}

	
	
}
