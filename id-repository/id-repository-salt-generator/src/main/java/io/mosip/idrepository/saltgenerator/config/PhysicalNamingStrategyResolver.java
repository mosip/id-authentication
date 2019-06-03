package io.mosip.idrepository.saltgenerator.config;

import static io.mosip.idrepository.saltgenerator.constant.IdRepoSaltGeneratorConstant.DB_SCHEMA_NAME;
import static io.mosip.idrepository.saltgenerator.constant.IdRepoSaltGeneratorConstant.DB_TABLE_NAME;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author Manoj SP
 *
 */
@Component
public class PhysicalNamingStrategyResolver extends PhysicalNamingStrategyStandardImpl {

	@Autowired
	private transient Environment env;

	private static final long serialVersionUID = 1L;

	@Override
	public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment context) {
		return Identifier.toIdentifier(env.getProperty(DB_SCHEMA_NAME.getValue()));
	}

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
		return Identifier.toIdentifier(env.getProperty(DB_TABLE_NAME.getValue()));
	}
}