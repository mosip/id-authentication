package io.mosip.authentication.core.constant;

/**
 * The Enum AuditModules - Contains all the modules in IdAuthentication for Audit purpose.
 *
 * @author Manoj SP
 */
public enum AuditModules {
	
	/** The otp auth. */
	OTP_AUTH("IDA-OTA","OTP Authentication Request", "OTP Authenticator"),
	
	/** The demo auth. */
	DEMO_AUTH("IDA-DEA", "Demographic Authentication Request", "Demographic Authenticator"),
	
	/** The FINGERPRINT_AUTH. */
	FINGERPRINT_AUTH("IDA-FPA", "Fingerprint Authentication Request", "Fingerprint Authenticator"),
	
	/** The IRIS_AUTH. */
	IRIS_AUTH("IDA-ISA", "Iris Authentication Request", "Iris Authenticator"),
	
	/** The FACE_AUTH. */
	FACE_AUTH("IDA-FAA", "Face Authentication Request", "Face Authenticator"),

	TOKEN_AUTH("IDA-TOA","Token Authentication requested", "Token Authenticator"),
	
	/** The e KY C AUTH. */
	EKYC_AUTH("IDA-EKA", "E-KYC Authentication Request", "eKYC Authenticator"),

	KYC_AUTH("IDA-KAT", "KYC Authentication Request", "KYC Authenticator"),

	KYC_EXCHANGE("IDA-KEX", "KYC Exchange Request", "KYC Exchange"),
	
	VCI_EXCHANGE("IDA-VCI", "VCI Exchange Request", "VCI Exchange"),

	IDENTITY_KEY_BINDING("IDA-IKB", "Identity Key Binding Request", "Key Binding"),

	/** The otp request. */
	OTP_REQUEST("IDA-OTR","OTP Request", "OTP Requestor"),
	
	/** The auth type status. */
	AUTH_TYPE_STATUS("IDA-ATS","Auth Type Status Retrieve/Update Request", "Auth Type Status"),
	
	/** The auth transaction history. */
	AUTH_TRANSACTION_HISTORY("IDA-ATH","Auth Transaction History Request", "Auth Transaction History"),
	
	IDENTITY_CACHE("IDA-IDC","IDentity Cache Request", "IDentity Cache"),
	
	/** The pin auth. */
	PIN_AUTH("IDA-MOD-106","Pin Authentication requested", ""), //not applicable for release v1
	
	/**  The Static Pin Storage. */
	STATIC_PIN_STORAGE("IDA-MOD-108","Static Pin Storage requested", ""),//not applicable for release v1
	
	/** The vid generation request. */
	VID_GENERATION_REQUEST("IDA-MOD-109","VID Generation requested", ""),//not applicable for release v1
	
	CREDENTIAL_STORAGE("IDA-MOD-110","Credential Storage websub-callback", ""),//not applicable for release v1

	;

	/** The module id. */
	private final String moduleId;
	
	/**  The Description. */
	private  String desc;
	
	/** The module name. */
	private String moduleName;

	
	
	/**
	 *  Instantiates a new audit contants. 
	 *  
	 *
	 * @param moduleId the module id
	 * @param desc the desc
	 * @param moduleName the module name
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
