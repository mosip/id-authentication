package io.mosip.idrepository.saltgenerator.constant;

/**
 * @author Manoj SP
 *
 */
public enum IdRepoSaltGeneratorConstant {
	
	IDMAP("vid.db"),
	IDREPO("identity.db.shard"),
	PACKAGE_TO_SCAN("io.mosip.idrepository.saltgenerator.*"),
	DB_SCHEMA_NAME("mosip.idrepo.salt-generator.schemaName"),
	DB_TABLE_NAME("mosip.idrepo.salt-generator.tableName"),
	CHUNK_SIZE("mosip.idrepo.salt-generator.chunk-size"),
	START_SEQ("mosip.idrepo.salt-generator.start-sequence"),
	END_SEQ("mosip.idrepo.salt-generator.end-sequence"),
	DATASOURCE_URL("mosip.idrepo.%s.url"),
	DATASOURCE_USERNAME("mosip.idrepo.%s.username"),
	DATASOURCE_PASSWORD("mosip.idrepo.%s.password"),
	DATASOURCE_DRIVERCLASSNAME("mosip.idrepo.%s.driverClassName");

	private String value;
	
	IdRepoSaltGeneratorConstant(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
