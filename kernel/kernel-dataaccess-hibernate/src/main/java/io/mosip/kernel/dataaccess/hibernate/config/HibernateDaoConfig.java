package io.mosip.kernel.dataaccess.hibernate.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
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

import io.mosip.kernel.core.dataaccess.spi.config.BaseDaoConfig;
import io.mosip.kernel.dataaccess.hibernate.constant.HibernatePersistenceConstant;
import io.mosip.kernel.dataaccess.hibernate.repository.impl.HibernateRepositoryImpl;

/**
 * This class declares the @Bean methods related to data access using hibernate
 * and will be processed by the Spring container to generate bean definitions
 * and service requests for those beans at runtime
 * 
 * @author Dharmesh Khandelwal
 * @author Shashank Agrawal
 * @author Bal Vikash Sharma
 * @since 1.0.0
 * 
 *
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = HibernatePersistenceConstant.MOSIP_PACKAGE, repositoryBaseClass = HibernateRepositoryImpl.class)
public class HibernateDaoConfig implements BaseDaoConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(HibernateDaoConfig.class);

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
		dataSource.setDriverClassName(environment.getProperty(HibernatePersistenceConstant.JDBC_DRIVER));
		dataSource.setUrl(environment.getProperty(HibernatePersistenceConstant.JDBC_URL));
		dataSource.setUsername(environment.getProperty(HibernatePersistenceConstant.JDBC_USER));
		dataSource.setPassword(environment.getProperty(HibernatePersistenceConstant.JDBC_PASS));
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
		entityManagerFactory.setPackagesToScan(HibernatePersistenceConstant.MOSIP_PACKAGE);
		entityManagerFactory.setPersistenceUnitName(HibernatePersistenceConstant.HIBERNATE);
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
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
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
		return new HibernateJpaDialect();
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
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_HBM2DDL_AUTO,
				HibernatePersistenceConstant.UPDATE);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_DIALECT,
				HibernatePersistenceConstant.MY_SQL5_DIALECT);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_SHOW_SQL, HibernatePersistenceConstant.TRUE);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_FORMAT_SQL,
				HibernatePersistenceConstant.TRUE);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_CONNECTION_CHAR_SET,
				HibernatePersistenceConstant.UTF8);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE,
				HibernatePersistenceConstant.FALSE);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_CACHE_USE_QUERY_CACHE,
				HibernatePersistenceConstant.FALSE);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_CACHE_USE_STRUCTURED_ENTRIES,
				HibernatePersistenceConstant.FALSE);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_GENERATE_STATISTICS,
				HibernatePersistenceConstant.FALSE);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_NON_CONTEXTUAL_CREATION,
				HibernatePersistenceConstant.FALSE);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_CURRENT_SESSION_CONTEXT,
				HibernatePersistenceConstant.JTA);
		getProperty(jpaProperties, HibernatePersistenceConstant.HIBERNATE_EJB_INTERCEPTOR,
				HibernatePersistenceConstant.EMPTY_INTERCEPTOR);
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
		/**
		 * if property found in properties file then add that interceptor to the jpa
		 * properties.
		 */
		if (property.equals(HibernatePersistenceConstant.HIBERNATE_EJB_INTERCEPTOR)) {
			try {
				if (environment.containsProperty(property)) {
					jpaProperties.put(property,
							BeanUtils.instantiateClass(Class.forName(environment.getProperty(property))));
				}
				/**
				 * We can add a default interceptor whenever we require here. 
				 */
			} catch (BeanInstantiationException | ClassNotFoundException e) {
				LOGGER.error("Error while configuring Interceptor.");
			}
		} else {
			jpaProperties.put(property,
					environment.containsProperty(property) ? environment.getProperty(property) : defaultValue);
		}
		return jpaProperties;
	}

}
