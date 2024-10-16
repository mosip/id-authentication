package io.mosip.authentication.internal.service.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import static io.mosip.authentication.internal.service.constant.BatchJobConstant.DB_SCHEMA_NAME;
import static io.mosip.authentication.internal.service.constant.BatchJobConstant.DB_TABLE_NAME;

/**
 * The Class PhysicalNamingStrategyResolver - class to resolve table name and
 * schema name dynamically.
 *
 * @author Kamesh Shekhar Prasad
 */
@Component
public class PhysicalNamingStrategyResolver extends PhysicalNamingStrategyStandardImpl {

	/** The env. */
	@Autowired
	private transient Environment env;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl#
	 * toPhysicalSchemaName(org.hibernate.boot.model.naming.Identifier,
	 * org.hibernate.engine.jdbc.env.spi.JdbcEnvironment)
	 */
	@Override
	public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment context) {
		return Identifier.toIdentifier(env.getProperty(env.getProperty(DB_SCHEMA_NAME.getValue()),
				env.getProperty(DB_SCHEMA_NAME.getValue())));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl#
	 * toPhysicalTableName(org.hibernate.boot.model.naming.Identifier,
	 * org.hibernate.engine.jdbc.env.spi.JdbcEnvironment)
	 */
	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
		return Identifier.toIdentifier(
				env.getProperty(env.getProperty(DB_TABLE_NAME.getValue()), env.getProperty(DB_TABLE_NAME.getValue())));
	}
}