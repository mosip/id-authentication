package io.mosip.kernel.uingenerator.config;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePropertySource;
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
@EnableJpaRepositories(basePackages = { "io.mosip.kernel.uingenerator.repository" })
@PropertySource(value = { "classpath:bootstrap.properties" })
@ComponentScan("io.mosip.kernel.uingenerator")
public class UinGeneratorConfiguration implements EnvironmentAware {

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
	 * Loads config server values
	 *
	 * @param env
	 *            env
	 * @return PropertySourcesPlaceholderConfigurer
	 * @throws IOException
	 */
	@Bean
	@Autowired
	public PropertySourcesPlaceholderConfigurer getPropertySourcesPlaceholderConfigurer() throws IOException {

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
		List<String> applicationNames = getAppNames();
		Resource[] appResources = new Resource[applicationNames.size()];
		for (int i = 0; i < applicationNames.size(); i++) {
			String loc = env.getProperty(UinGeneratorConstant.SPRING_CLOUD_CONFIG_URI) + UinGeneratorConstant.KERNEL
					+ env.getProperty(UinGeneratorConstant.SPRING_PROFILES_ACTIVE) + UinGeneratorConstant.FORWARD_SLASH
					+ env.getProperty(UinGeneratorConstant.SPRING_CLOUD_CONFIG_LABEL)
					+ UinGeneratorConstant.FORWARD_SLASH + applicationNames.get(i) + UinGeneratorConstant.DASH
					+ env.getProperty(UinGeneratorConstant.SPRING_PROFILES_ACTIVE) + UinGeneratorConstant.PROPERTIES;
			appResources[i] = resolver.getResources(loc)[0];
			((AbstractEnvironment) env).getPropertySources()
					.addLast(new ResourcePropertySource(applicationNames.get(i), loc));
		}
		pspc.setLocations(appResources);
		return pspc;
	}

	/**
	 * Gets list of application name mentioned in bootstrap.properties
	 * 
	 * @param env
	 *            env
	 * @return AppNames
	 */
	public List<String> getAppNames() {
		String names = env.getProperty(UinGeneratorConstant.SPRING_APPLICATION_NAME);
		return Stream.of(names.split(UinGeneratorConstant.COMMA)).collect(Collectors.toList());
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
	 * @param dataSource
	 *            dataSource
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
	 * @param entityManagerFactory
	 *            entityManagerFactory
	 * @return PlatformTransactionManager
	 */
	@Bean
	@Autowired
	public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory.getObject());
	}
}