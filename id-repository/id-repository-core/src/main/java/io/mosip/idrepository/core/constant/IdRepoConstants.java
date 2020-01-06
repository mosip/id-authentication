package io.mosip.idrepository.core.constant;

/**
 * The Enum IdRepoConstants - contains constants used internally by the
 * application.
 *
 * @author Manoj SP
 */
public class IdRepoConstants {

	/** The cbeff format. */
	public static final String CBEFF_FORMAT = "cbeff";

	/** The identity file name format. */
	public static final String FILE_FORMAT_ATTRIBUTE = "format";

	/** The identity file name key. */
	public static final String FILE_NAME_ATTRIBUTE = "value";
	
	public static final String VID_TYPE_PATH = "vidPolicies.*.vidType";
	
	public static final String VID_POLICY_PATH = "vidPolicies.*.vidPolicy";

	/** The root path. */
	public static final String ROOT_PATH = "identity";

	/** The version pattern. */
	public static final String VERSION_PATTERN = "mosip.idrepo.application.version.pattern";

	/** The datetime timezone. */
	public static final String DATETIME_TIMEZONE = "mosip.idrepo.datetime.timezone";

	/** The status registered. */
	public static final String ACTIVE_STATUS = "mosip.idrepo.identity.uin-status.registered";

	/** The datetime pattern. */
	public static final String DATETIME_PATTERN = "mosip.utc-datetime-pattern";

	/** The application version. */
	public static final String APPLICATION_VERSION = "mosip.idrepo.identity.application.version";
	
	/** The application version. */
	public static final String APPLICATION_VERSION_VID = "mosip.idrepo.vid.application.version";

	/** The application id. */
	public static final String APPLICATION_ID = "mosip.idrepo.application.id";

	/** The application name. */
	public static final String APPLICATION_NAME = "mosip.idrepo.application.name";

	/** The mosip primary language. */
	public static final String MOSIP_PRIMARY_LANGUAGE = "mosip.primary-language";

	/** The json schema file name. */
	public static final String JSON_SCHEMA_FILE_NAME = "mosip.idrepo.json-schema-fileName";
	
	/** The Json path value */
	public static final String MOSIP_KERNEL_IDREPO_JSON_PATH = "mosip.idrepo.identity.json.path";
	
	public static final String VID_ACTIVE_STATUS = "mosip.idrepo.vid.active-status";
	
	public static final String VID_ALLOWED_STATUS = "mosip.idrepo.vid.allowedStatus";
	
	public static final String VID_DB_URL = "mosip.idrepo.vid.db.url";
	
	public static final String VID_DB_USERNAME = "mosip.idrepo.vid.db.username";
	
	public static final String VID_DB_PASSWORD = "mosip.idrepo.vid.db.password";
	
	public static final String VID_DB_DRIVER_CLASS_NAME = "mosip.idrepo.vid.db.driverClassName";
	
	public static final String VID_POLICY_FILE_URL = "mosip.idrepo.vid.policy-file-url";
	
	public static final String VID_POLICY_SCHEMA_URL = "mosip.idrepo.vid.policy-schema-url";
	
	public static final String VID_UNLIMITED_TRANSACTION_STATUS = "mosip.idrepo.vid.unlimited-txn-status";
	
	public static final String VID_REGENERATE_ALLOWED_STATUS = "mosip.idrepo.vid.regenerate.allowed-status";
	
	public static final String VID_REGENERATE_ACTIVE_STATUS = "INVALIDATED";
	
	public static final String MODULO_VALUE = "mosip.idrepo.modulo-value";
	
	public static final String SPLITTER = "_";
	
	public static final String VID_DEACTIVATED = "mosip.idrepo.vid.deactive-status";
	
	public static final String VID_REACTIVATED = "mosip.idrepo.vid.reactive-status";
	
	public static final String FMR_ENABLED = "mosip.fingerprint.fmr.enabled";
	
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
