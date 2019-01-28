package io.mosip.authentication.core.constant;

/**
 * The Enum AuditModules - Contains all the modules in IdAuthentication for Audit purpose.
 *
 * @author Manoj SP
 */
public enum AuditModules {

	/** The otp auth. */
	OTP_AUTH("IDA-MOD-101"),
	
	/** The demo auth. */
	DEMO_AUTH("IDA-MOD-102"),
	
	/** The FINGERPRINT_AUTH. */
	FINGERPRINT_AUTH("IDA-MOD-103"),
	
	/** The IRIS_AUTH. */
	IRIS_AUTH("IDA-MOD-104"),
	
	/** The FACE_AUTH. */
	FACE_AUTH("IDA-MOD-105"),
	
	/** The pin auth. */
	PIN_AUTH("IDA-MOD-106"),
	
	/** The e KY C AUTH. */
	EKYC_AUTH("IDA-MOD-107"),
	
	/** The otp request. */
	OTP_REQUEST("IDA-MOD-110"),
	
	/** The internal auth request. */
	INTERNAL_AUTH_REQUEST("IDA-MOD-111");

	/** The module id. */
	private final String moduleId;

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

	/**
	 * Instantiates a new audit contants.
	 *
	 * @param moduleId
	 *            the moduleId
	 */
	private AuditModules(String moduleId) {
		this.moduleId = moduleId;
	}
}
