package io.mosip.authentication.internal.service.constant;

/**
 * The Enum SaltGeneratorConstant - contains constants for SaltGenerator.
 *
 * @author Manoj SP
 */
public enum BatchJobConstant {

	/** The db schema name. */
	DB_SCHEMA_NAME("mosip.id.authentication.internal-service.schemaName"),

	/** The db table name. */
	DB_TABLE_NAME("mosip.id.authentication.internal-service.tableName");

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
