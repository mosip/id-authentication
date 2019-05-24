package io.mosip.kernel.uingenerator.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;

/**
 * Configuration class for UinGenerator
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Configuration
@PropertySource({ "classpath:bootstrap.properties" })
@PropertySource(value = "classpath:application-${spring.profiles.active}.properties", ignoreResourceNotFound = true)
@EnableJpaRepositories(basePackages = { "io.mosip.kernel.uingenerator.repository" })
@ComponentScan(basePackages = { "io.mosip.kernel.uingenerator", "io.mosip.kernel.auth.adapter.*",
		"io.mosip.kernel.cryptosignature.*" })
public class UinServiceConfiguration implements EnvironmentAware {

	/**
	 * Field for {@link #env}
	 */
	@Autowired
	private Environment env;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.context.EnvironmentAware#setEnvironment(org.
	 * springframework.core.env.Environment)
	 */
	@Override
	public void setEnvironment(final Environment environment) {
		this.env = environment;
	}

	/**
	 * A factory for connections to the physical data source that this DataSource
	 * object represents.
	 * 
	 * @return dataSource
	 */
	@Bean
	@Autowired
	public DataSource dataSource() {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getProperty(UinGeneratorConstant.JAVAX_PERSISTENCE_JDBC_DRIVER));
		dataSource.setUrl(env.getProperty(UinGeneratorConstant.JAVAX_PERSISTENCE_JDBC_URL));
		dataSource.setUsername(env.getProperty(UinGeneratorConstant.JAVAX_PERSISTENCE_JDBC_USER));
		dataSource.setPassword(env.getProperty(UinGeneratorConstant.JAVAX_PERSISTENCE_JDBC_PASS));
		return dataSource;
	}

	/**
	 * Set up a shared JPA EntityManagerFactory in a Spring application context
	 * 
	 * @param dataSource dataSource
	 * @return LocalContainerEntityManagerFactoryBean
	 */
	@Bean
	@Autowired
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource) {
		final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(Boolean.TRUE);
		vendorAdapter.setShowSql(Boolean.FALSE);
		factory.setDataSource(dataSource);
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("io.mosip.kernel.uingenerator.entity");
		Properties jpaProperties = new Properties();
		jpaProperties.put(UinGeneratorConstant.HIBERNATE_DIALECT,
				env.getProperty(UinGeneratorConstant.HIBERNATE_DIALECT));
		jpaProperties.put(UinGeneratorConstant.HIBERNATE_JDBC_LOB_NON_CONTEXTUAL_CREATION,
				env.getProperty(UinGeneratorConstant.HIBERNATE_JDBC_LOB_NON_CONTEXTUAL_CREATION));
		jpaProperties.put(UinGeneratorConstant.HIBERNATE_CURRENT_SESSION_CONTEXT_CLASS,
				env.getProperty(UinGeneratorConstant.HIBERNATE_CURRENT_SESSION_CONTEXT_CLASS));
		factory.setJpaProperties(jpaProperties);
		return factory;
	}

	/**
	 * This is the central interface in Spring's transaction infrastructure.
	 * 
	 * @param entityManagerFactory entityManagerFactory
	 * @return PlatformTransactionManager
	 */
	@Bean
	@Autowired
	public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory.getObject());
	}

}