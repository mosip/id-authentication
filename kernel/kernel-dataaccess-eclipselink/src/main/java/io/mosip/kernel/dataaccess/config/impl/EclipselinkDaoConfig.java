package io.mosip.kernel.dataaccess.config.impl;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.logging.SessionLog;
import io.mosip.kernel.core.dataaccess.spi.config.BaseDaoConfig;
import io.mosip.kernel.dataaccess.constants.EclipselinkPersistenceConstants;
import io.mosip.kernel.dataaccess.repository.impl.EclipselinkRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * This class declares the @Bean methods related to data access using hibernate
 * and will be processed by the Spring container to generate bean definitions
 * and service requests for those beans at runtime
 * 
 * @author Dharmesh Khandelwal
 * @author Shashank Agrawal
 * @since 1.0.0
 * 
 *
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = EclipselinkPersistenceConstants.MOSIP_PACKAGE, repositoryBaseClass = EclipselinkRepositoryImpl.class)
public class EclipselinkDaoConfig implements BaseDaoConfig {

	/**
	 * Field for interface representing the environment in which the current
	 * application is running.
	 */
	@Autowired
	private Environment environment;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.dao.config.BaseDaoConfig#dataSource()
	 */
	@Override
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(environment.getProperty(PersistenceUnitProperties.JDBC_DRIVER));
		dataSource.setUrl(environment.getProperty(PersistenceUnitProperties.JDBC_URL));
		dataSource.setUsername(environment.getProperty(PersistenceUnitProperties.JDBC_USER));
		dataSource.setPassword(environment.getProperty(PersistenceUnitProperties.JDBC_PASSWORD));
		return dataSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.dao.config.BaseDaoConfig#entityManagerFactory()
	 */
	@Override
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setDataSource(dataSource());
		entityManagerFactory.setPackagesToScan(EclipselinkPersistenceConstants.MOSIP_PACKAGE);
		entityManagerFactory.setPersistenceUnitName(EclipselinkPersistenceConstants.ECLIPSELINK);
		entityManagerFactory.setJpaPropertyMap(jpaProperties());
		entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter());
		entityManagerFactory.setJpaDialect(jpaDialect());
		return entityManagerFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.dao.config.BaseDaoConfig#jpaVendorAdapter()
	 */
	@Override
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		EclipseLinkJpaVendorAdapter vendorAdapter = new EclipseLinkJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(true);
		vendorAdapter.setShowSql(true);
		return vendorAdapter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.dao.config.BaseDaoConfig#jpaDialect()
	 */
	@Override
	@Bean
	public JpaDialect jpaDialect() {
		return new EclipseLinkJpaDialect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.dao.config.BaseDaoConfig#transactionManager(javax.
	 * persistence.EntityManagerFactory)
	 */
	@Override
	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager(entityManagerFactory);
		jpaTransactionManager.setDataSource(dataSource());
		jpaTransactionManager.setJpaDialect(jpaDialect());
		return jpaTransactionManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.dao.config.BaseDaoConfig#jpaProperties()
	 */
	@Override
	public Map<String, Object> jpaProperties() {
		HashMap<String, Object> jpaProperties = new HashMap<>();
		getProperty(jpaProperties, PersistenceUnitProperties.WEAVING, EclipselinkPersistenceConstants.STATIC);
		getProperty(jpaProperties, PersistenceUnitProperties.DDL_GENERATION,
				PersistenceUnitProperties.CREATE_OR_EXTEND);
		getProperty(jpaProperties, PersistenceUnitProperties.DDL_GENERATION_MODE,
				PersistenceUnitProperties.DDL_BOTH_GENERATION);
		getProperty(jpaProperties, PersistenceUnitProperties.LOGGING_LEVEL, SessionLog.FINEST_LABEL);
		getProperty(jpaProperties, PersistenceUnitProperties.CATEGORY_LOGGING_LEVEL_ + SessionLog.JPA,
				SessionLog.FINEST_LABEL);
		getProperty(jpaProperties, PersistenceUnitProperties.CATEGORY_LOGGING_LEVEL_ + SessionLog.SQL,
				SessionLog.FINEST_LABEL);
		getProperty(jpaProperties, PersistenceUnitProperties.LOGGING_CONNECTION, EclipselinkPersistenceConstants.TRUE);
		getProperty(jpaProperties, PersistenceUnitProperties.LOGGING_PARAMETERS, EclipselinkPersistenceConstants.TRUE);
		return jpaProperties;
	}

	/**
	 * Function to associate the specified value with the specified key in the map.
	 * If the map previously contained a mapping for the key, the old value is
	 * replaced.
	 * 
	 * @param jpaProperties
	 *            The map of jpa properties
	 * @param property
	 *            The property whose value is to be set
	 * @param defaultValue
	 *            The default value to set
	 * @return The map of jpa properties with properties set
	 */
	private HashMap<String, Object> getProperty(HashMap<String, Object> jpaProperties, String property,
			String defaultValue) {
		jpaProperties.put(property,
				environment.containsProperty(property) ? environment.getProperty(property) : defaultValue);
		return jpaProperties;
	}

}
