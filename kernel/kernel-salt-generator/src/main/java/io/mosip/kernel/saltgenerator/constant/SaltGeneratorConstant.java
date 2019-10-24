package io.mosip.kernel.saltgenerator.constant;

/**
 * The Enum SaltGeneratorConstant - contains constants for SaltGenerator.
 *
 * @author Manoj SP
 */
public enum SaltGeneratorConstant {
	
	/** The package to scan. */
	PACKAGE_TO_SCAN("io.mosip.kernel.saltgenerator.*"),
	
	/** The db schema name. */
	DB_SCHEMA_NAME("mosip.kernel.salt-generator.schemaName"),
	
	/** The db table name. */
	DB_TABLE_NAME("mosip.kernel.salt-generator.tableName"),
	
	/** The chunk size. */
	CHUNK_SIZE("mosip.kernel.salt-generator.chunk-size"),
	
	/** The start seq. */
	START_SEQ("mosip.kernel.salt-generator.start-sequence"),
	
	/** The end seq. */
	END_SEQ("mosip.kernel.salt-generator.end-sequence"),
	
	/** The datasource url. */
	DATASOURCE_URL("mosip.kernel.salt-generator.db.url"),
	
	/** The datasource username. */
	DATASOURCE_USERNAME("mosip.kernel.salt-generator.db.username"),
	
	/** The datasource password. */
	DATASOURCE_PASSWORD("mosip.kernel.salt-generator.db.password"),
	
	/** The datasource driverclassname. */
	DATASOURCE_DRIVERCLASSNAME("mosip.kernel.salt-generator.db.driverClassName");

	/** The value. */
	private String value;
	
	/**
	 * Instantiates a new salt generator constant.
	 *
	 * @param value the value
	 */
	SaltGeneratorConstant(String value) {
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
