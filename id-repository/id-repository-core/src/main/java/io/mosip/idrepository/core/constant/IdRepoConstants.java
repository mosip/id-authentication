package io.mosip.idrepository.core.constant;

/**
 * The Enum IdRepoConstants - contains constants used internally by the
 * application.
 *
 * @author Manoj SP
 */
public enum IdRepoConstants {

	/** The cbeff format. */
	CBEFF_FORMAT("cbeff"),

	/** The identity file name format. */
	FILE_FORMAT_ATTRIBUTE("format"),

	/** The identity file name key. */
	FILE_NAME_ATTRIBUTE("value"),
	
	VID_TYPE_PATH("vidPolicies.*.vidType"),
	
	VID_POLICY_PATH("vidPolicies.*.vidPolicy"),

	/** The root path. */
	ROOT_PATH("identity"),

	/** The version pattern. */
	VERSION_PATTERN("mosip.idrepo.application.version.pattern"),

	/** The datetime timezone. */
	DATETIME_TIMEZONE("mosip.idrepo.datetime.timezone"),

	/** The status registered. */
	ACTIVE_STATUS("mosip.idrepo.identity.uin-status.registered"),

	/** The datetime pattern. */
	DATETIME_PATTERN("mosip.utc-datetime-pattern"),

	/** The application version. */
	APPLICATION_VERSION("mosip.idrepo.identity.application.version"),
	
	/** The application version. */
	APPLICATION_VERSION_VID("mosip.idrepo.vid.application.version"),

	/** The application id. */
	APPLICATION_ID("mosip.idrepo.application.id"),

	/** The application name. */
	APPLICATION_NAME("mosip.idrepo.application.name"),

	/** The mosip primary language. */
	MOSIP_PRIMARY_LANGUAGE("mosip.primary-language"),

	/** The json schema file name. */
	JSON_SCHEMA_FILE_NAME("mosip.idrepo.json-schema-fileName"),
	
	/** The Json path value */
	MOSIP_KERNEL_IDREPO_JSON_PATH("mosip.idrepo.identity.json.path"),
	
	VID_ACTIVE_STATUS("mosip.idrepo.vid.active-status"),
	
	VID_ALLOWED_STATUS("mosip.idrepo.vid.allowedStatus"),
	
	VID_DB_URL("mosip.idrepo.vid.db.url"),
	
	VID_DB_USERNAME("mosip.idrepo.vid.db.username"),
	
	VID_DB_PASSWORD("mosip.idrepo.vid.db.password"),
	
	VID_DB_DRIVER_CLASS_NAME("mosip.idrepo.vid.db.driverClassName"),
	
	VID_POLICY_FILE_URL("mosip.idrepo.vid.policy-file-url"),
	
	VID_POLICY_SCHEMA_URL("mosip.idrepo.vid.policy-schema-url"),
	
	VID_UNLIMITED_TRANSACTION_STATUS("mosip.idrepo.vid.unlimited-txn-status"),
	
	VID_REGENERATE_ALLOWED_STATUS("mosip.idrepo.vid.regenerate.allowed-status"),
	
	VID_REGENERATE_ACTIVE_STATUS("INVALIDATED"),
	
	MODULO_VALUE("mosip.idrepo.modulo-value"),
	
	SPLITTER("_"),
	
	VID_DEACTIVATED("mosip.idrepo.vid.deactive-status"),
	
	VID_REACTIVATED("mosip.idrepo.vid.reactive-status");
	
	/** The value. */
	private final String value;

	/**
	 * Instantiates a new id repo constants.
	 *
	 * @param value the value
	 */
	private IdRepoConstants(String value) {
		this.value = value;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
