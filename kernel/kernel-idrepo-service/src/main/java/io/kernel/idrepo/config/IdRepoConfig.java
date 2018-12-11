package io.kernel.idrepo.config;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.idrepo.spi.ShardDataSourceResolver;

/**
 * The Class IdRepoConfig.
 *
 * @author Manoj SP
 */
@Configuration
@ConfigurationProperties("mosip.idrepo")
@EnableTransactionManagement
public class IdRepoConfig implements WebMvcConfigurer {

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The env. */
	@Autowired
	private Environment env;

	/** The db. */
	private Map<String, Map<String, String>> db;

	/** The status. */
	private Map<String, String> status;

	/** The id. */
	private Map<String, String> id;

	/**
	 * Gets the db.
	 *
	 * @return the db
	 */
	public Map<String, Map<String, String>> getDb() {
		return db;
	}

	/**
	 * Sets the db.
	 *
	 * @param db
	 *            the db
	 */
	public void setDb(Map<String, Map<String, String>> db) {
		this.db = db;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public Map<String, String> getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status
	 *            the status
	 */
	public void setStatus(Map<String, String> status) {
		this.status = status;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Map<String, String> getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the id
	 */
	public void setId(Map<String, String> id) {
		this.id = id;
	}

	/**
	 * Setup.
	 */
	@PostConstruct
	public void setup() {
		mapper.setDateFormat(new SimpleDateFormat(env.getProperty("datetime.pattern")));
		mapper.setTimeZone(TimeZone.getTimeZone(env.getProperty("datetime.timezone")));
	}

	/**
	 * Gets the shard data source resolver.
	 *
	 * @return the shard data source resolver
	 */
	@Bean
	public ShardDataSourceResolver getShardDataSourceResolver() {
		ShardDataSourceResolver resolver = new ShardDataSourceResolver();
		resolver.setLenientFallback(false);
		resolver.setTargetDataSources(db.entrySet().parallelStream()
				.collect(Collectors.toMap(Map.Entry::getKey, value -> buildDataSource(value.getValue()))));
		return resolver;
	}

	/**
	 * Rest template.
	 *
	 * @return the rest template
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	/**
	 * Id.
	 *
	 * @return the map
	 */
	@Bean
	public Map<String, String> id() {
		return Collections.unmodifiableMap(id);
	}

	/**
	 * Status.
	 *
	 * @return the map
	 */
	@Bean
	public Map<String, String> status() {
		return Collections.unmodifiableMap(status);
	}

	/**
	 * Entity manager factory.
	 *
	 * @param dataSource the data source
	 * @return the local container entity manager factory bean
	 */
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource);
		em.setPackagesToScan("io.kernel.idRepo.*");

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());

		return em;
	}

	/**
	 * Transaction manager.
	 *
	 * @param emf the emf
	 * @return the platform transaction manager
	 */
	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		return transactionManager;
	}

	/**
	 * Additional properties.
	 *
	 * @return the properties
	 */
	Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL92Dialect");
		properties.setProperty("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
		properties.setProperty("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());

		return properties;
	}

	/**
	 * Builds the data source.
	 *
	 * @param dataSourceValues
	 *            the data source values
	 * @return the data source
	 */
	private DataSource buildDataSource(Map<String, String> dataSourceValues) {
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		driverManagerDataSource.setUrl(dataSourceValues.get("url"));
		driverManagerDataSource.setUsername(dataSourceValues.get("username"));
		driverManagerDataSource.setPassword(dataSourceValues.get("password"));
		driverManagerDataSource.setDriverClassName(dataSourceValues.get("driverClassName"));
		return driverManagerDataSource;
	}
}
