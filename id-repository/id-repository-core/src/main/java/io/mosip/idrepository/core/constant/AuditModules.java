package io.mosip.idrepository.core.constant;

/**
 * The Enum AuditModules - Contains all the modules in IdAuthentication for
 * Audit purpose.
 *
 * @author Manoj SP
 */
public enum AuditModules {

	/** The create identity. */
	CREATE_IDENTITY("IDR-MOD-101"),

	/** The update identity. */
	UPDATE_IDENTITY("IDR-MOD-102"),

	/** The retrieve identity. */
	RETRIEVE_IDENTITY("IDR-MOD-103"),
	
	CREATE_VID("IDR-MOD-104"),
	
	UPDATE_VID("IDR-MOD-105"),
	
	RETRIEVE_VID("IDR-MOD-106"),
	
	REGENERATE_VID("IDR-MOD-107");

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

	/**
	 * Gets the module name.
	 *
	 * @return the module name
	 */
	public String getModuleName() {
		return this.name();
	}

	/**
	 * Instantiates a new audit contants.
	 *
	 * @param moduleId the moduleId
	 */
	private AuditModules(String moduleId) {
		this.moduleId = moduleId;
	}
}
