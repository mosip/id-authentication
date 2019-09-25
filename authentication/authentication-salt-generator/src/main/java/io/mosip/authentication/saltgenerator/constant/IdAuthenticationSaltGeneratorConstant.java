package io.mosip.authentication.saltgenerator.constant;

/**
 * @author Manoj SP
 *
 */
public enum IdAuthenticationSaltGeneratorConstant {
	
	PACKAGE_TO_SCAN("io.mosip.authentication.saltgenerator.*"),
	DB_SCHEMA_NAME("mosip.authentication.salt-generator.schemaName"),
	DB_TABLE_NAME("mosip.authentication.salt-generator.tableName"),
	CHUNK_SIZE("mosip.authentication.salt-generator.chunk-size"),
	START_SEQ("mosip.authentication.salt-generator.start-sequence"),
	END_SEQ("mosip.authentication.salt-generator.end-sequence"),
	DATASOURCE_URL("javax.persistence.jdbc.url"),
	DATASOURCE_USERNAME("javax.persistence.jdbc.user"),
	DATASOURCE_PASSWORD("javax.persistence.jdbc.password"),
	DATASOURCE_DRIVERCLASSNAME("javax.persistence.jdbc.driver");

	private String value;
	
	IdAuthenticationSaltGeneratorConstant(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
