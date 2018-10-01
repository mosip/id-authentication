package org.mosip.kernel.dataaccess.config.impl;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.mosip.kernel.core.spi.dataaccess.config.BaseDaoConfig;
import org.mosip.kernel.dataaccess.constant.HibernatePersistenceConstants;
import org.mosip.kernel.dataaccess.repository.impl.HibernateRepositoryImpl;
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
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
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
@EnableJpaRepositories(basePackages = HibernatePersistenceConstants.MOSIP_PACKAGE, repositoryBaseClass = HibernateRepositoryImpl.class)
public class HibernateDaoConfig implements BaseDaoConfig {

	/**
	 * Field for interface representing the environment in which the current
	 * application is running.
	 */
	@Autowired
	private Environment environment;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.kernel.core.dao.config.BaseDaoConfig#dataSource()
	 */
	@Override
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(environment.getProperty(HibernatePersistenceConstants.JDBC_DRIVER));
		dataSource.setUrl(environment.getProperty(HibernatePersistenceConstants.JDBC_URL));
		dataSource.setUsername(environment.getProperty(HibernatePersistenceConstants.JDBC_USER));
		dataSource.setPassword(environment.getProperty(HibernatePersistenceConstants.JDBC_PASS));
		return dataSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.kernel.core.dao.config.BaseDaoConfig#entityManagerFactory()
	 */
	@Override
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setDataSource(dataSource());
		entityManagerFactory.setPackagesToScan(HibernatePersistenceConstants.MOSIP_PACKAGE);
		entityManagerFactory.setPersistenceUnitName(HibernatePersistenceConstants.HIBERNATE);
		entityManagerFactory.setJpaPropertyMap(jpaProperties());
		entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter());
		entityManagerFactory.setJpaDialect(jpaDialect());
		return entityManagerFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.kernel.core.dao.config.BaseDaoConfig#jpaVendorAdapter()
	 */
	@Override
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(true);
		vendorAdapter.setShowSql(true);
		return vendorAdapter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.kernel.core.dao.config.BaseDaoConfig#jpaDialect()
	 */
	@Override
	@Bean
	public JpaDialect jpaDialect() {
		return new HibernateJpaDialect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.kernel.core.dao.config.BaseDaoConfig#transactionManager(javax.
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
	 * @see org.mosip.kernel.core.dao.config.BaseDaoConfig#jpaProperties()
	 */
	@Override
	public Map<String, Object> jpaProperties() {
		HashMap<String, Object> jpaProperties = new HashMap<>();
		getProperty(jpaProperties, HibernatePersistenceConstants.HIBERNATE_HBM2DDL_AUTO,
				HibernatePersistenceConstants.UPDATE);
		getProperty(jpaProperties, HibernatePersistenceConstants.HIBERNATE_DIALECT,
				HibernatePersistenceConstants.MY_SQL5_DIALECT);
		getProperty(jpaProperties, HibernatePersistenceConstants.HIBERNATE_SHOW_SQL,
				HibernatePersistenceConstants.TRUE);
		getProperty(jpaProperties, HibernatePersistenceConstants.HIBERNATE_FORMAT_SQL,
				HibernatePersistenceConstants.TRUE);
		getProperty(jpaProperties, HibernatePersistenceConstants.HIBERNATE_CONNECTION_CHAR_SET,
				HibernatePersistenceConstants.UTF8);
		getProperty(jpaProperties, HibernatePersistenceConstants.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE,
				HibernatePersistenceConstants.FALSE);
		getProperty(jpaProperties, HibernatePersistenceConstants.HIBERNATE_CACHE_USE_QUERY_CACHE,
				HibernatePersistenceConstants.FALSE);
		getProperty(jpaProperties, HibernatePersistenceConstants.HIBERNATE_CACHE_USE_STRUCTURED_ENTRIES,
				HibernatePersistenceConstants.FALSE);
		getProperty(jpaProperties, HibernatePersistenceConstants.HIBERNATE_GENERATE_STATISTICS,
				HibernatePersistenceConstants.FALSE);
		getProperty(jpaProperties, HibernatePersistenceConstants.HIBERNATE_NON_CONTEXTUAL_CREATION,
				HibernatePersistenceConstants.FALSE);
		getProperty(jpaProperties, HibernatePersistenceConstants.HIBERNATE_CURRENT_SESSION_CONTEXT,
				HibernatePersistenceConstants.JTA);
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
