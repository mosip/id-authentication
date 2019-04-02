package io.mosip.kernel.core.idrepo.constant;

/**
 * The Enum IdRepoConstants.
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
	
	/** The root path. */
	ROOT_PATH("identity"),
	
	/** The version pattern. */
	VERSION_PATTERN("^\\d+(\\.\\d+)?$"),
	
	/** The datetime timezone. */
	DATETIME_TIMEZONE("mosip.kernel.idrepo.datetime.timezone"),
	
	/** The status registered. */
	ACTIVE_STATUS("mosip.kernel.idrepo.status.registered"),
	
	/** The datetime pattern. */
	DATETIME_PATTERN("mosip.utc-datetime-pattern"),
	
	/** The application version. */
	APPLICATION_VERSION("mosip.kernel.idrepo.application.version"),
	
	/** The application id. */
	APPLICATION_ID("mosip.kernel.idrepo.application.id"),
	
	/** The application name. */
	APPLICATION_NAME("mosip.kernel.idrepo.application.name"),
	
	/** The mosip primary language. */
	MOSIP_PRIMARY_LANGUAGE("mosip.primary-language"),
	
	/** The json schema file name. */
	JSON_SCHEMA_FILE_NAME("mosip.kernel.idrepo.json-schema-fileName");
	
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
