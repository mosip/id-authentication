package io.mosip.authentication.internal.service.constant;

/**
 * The Enum SaltGeneratorConstant - contains constants for SaltGenerator.
 *
 * @author Manoj SP
 */
public enum BatchJobConstant {

	/** The package to scan. */
	PACKAGE_TO_SCAN("io.mosip.authentication.*"),

	/** The db schema name. */
	DB_SCHEMA_NAME("mosip.id.authentication.internal-service.schemaName"),

	/** The db table name. */
	DB_TABLE_NAME("mosip.id.authentication.internal-service.tableName"),

	DATASOURCE_ALIAS("mosip.id.authentication.internal-service.db.key-alias"),

	/** The datasource url. */
	DATASOURCE_URL("%s.url"),

	/** The datasource username. */
	DATASOURCE_USERNAME("%s.username"),

	/** The datasource password. */
	DATASOURCE_PASSWORD("%s.password"),

	/** The datasource driverclassname. */
	DATASOURCE_DRIVERCLASSNAME("%s.driverClassName");

	/** The value. */
	private String value;

	/**
	 * Instantiates a new salt generator constant.
	 *
	 * @param value the value
	 */
	BatchJobConstant(String value) {
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
