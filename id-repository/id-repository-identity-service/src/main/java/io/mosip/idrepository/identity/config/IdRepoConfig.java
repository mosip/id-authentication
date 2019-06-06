package io.mosip.idrepository.identity.config;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.hibernate.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.AuthenticationException;
import io.mosip.idrepository.core.exception.IdRepoAppUncheckedException;
import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class IdRepoConfig.
 *
 * @author Manoj SP
 */
@Configuration
@ConfigurationProperties("mosip.idrepo.identity")
@EnableTransactionManagement
public class IdRepoConfig implements WebMvcConfigurer {

	/** The mosip logger. */
	Logger mosipLogger = IdRepoLogger.getLogger(IdRepoConfig.class);

	/** The env. */
	@Autowired
	private RestTemplate restTemplate;

	/** The interceptor. */
	@Autowired
	private Interceptor interceptor;

	/** The db. */
	private Map<String, Map<String, String>> db;

	/** The uin Status. */
	private List<String> uinStatus;

	/** The allowed bio types. */
	private List<String> allowedBioAttributes;

	private List<String> bioAttributes;

	private List<String> allowedTypes;

	/** The id. */
	private Map<String, String> id;

	@PostConstruct
	public void init() {
		restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor() {

			@Override
			public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
					throws IOException {
				mosipLogger.debug(IdRepoLogger.getUin(), "Rest Template logs", "Request URL: ",
						request.getURI().toString());
				ClientHttpResponse response = execution.execute(request, body);
				BufferingClientHttpResponseWrapper responseWrapper = new BufferingClientHttpResponseWrapper(response);
				mosipLogger.debug(IdRepoLogger.getUin(), "Rest Template logs", "Resposne: ",
						responseWrapper.getResponseBody());
				return responseWrapper;
			}
		});

		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {

			@Override
			protected void handleError(ClientHttpResponse response, HttpStatus statusCode) throws IOException {
				mosipLogger.error(IdRepoLogger.getUin(), "restTemplate - handleError", "Rest Template logs",
						"Status error : " + response.getRawStatusCode() + " " + response.getStatusCode() + "  "
								+ response.getStatusText());
				if (response.getStatusCode().is4xxClientError()) {
					if (response.getRawStatusCode() == 401 || response.getRawStatusCode() == 403) {
						mosipLogger.error(IdRepoLogger.getUin(), "restTemplate - handleError",
								"request failed with status code :" + response.getRawStatusCode(),
								"\n\n" + new String(super.getResponseBody(response)));
						List<ServiceError> errorList = ExceptionUtils
								.getServiceErrorList(new String(super.getResponseBody(response)));
						mosipLogger.error(IdRepoLogger.getUin(), "restTemplate - handleError",
								"Throwing AuthenticationException", errorList.toString());
						throw new AuthenticationException(errorList.get(0).getErrorCode(),
								errorList.get(0).getMessage(), response.getRawStatusCode());
					} else {
						mosipLogger.error(IdRepoLogger.getUin(), "restTemplate - handleError", "Rest Template logs",
								"Status error - returning RestServiceException - CLIENT_ERROR -- "
										+ new String(super.getResponseBody(response)));
						throw new IdRepoAppUncheckedException(IdRepoErrorConstants.CLIENT_ERROR);
					}
				}
			}
		});
	}

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
	 * Sets the status.
	 *
	 * @param status
	 *            the status
	 */
	public void setUinStatus(List<String> uinStatus) {
		this.uinStatus = uinStatus;
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
	 * Sets the allowed bio types.
	 *
	 * @param allowedBioAttributes
	 *            the new allowed bio types
	 */
	public void setAllowedBioAttributes(List<String> allowedBioAttributes) {
		this.allowedBioAttributes = allowedBioAttributes;
	}

	public void setBioAttributes(List<String> bioAttributes) {
		this.bioAttributes = bioAttributes;
	}

	public void setAllowedTypes(List<String> allowedTypes) {
		this.allowedTypes = allowedTypes;
	}

	// FIXME Need to check for UIN-Reg ID scenario
	// /**
	// * Gets the shard data source resolver.
	// *
	// * @return the shard data source resolver
	// */
	// @Bean
	// public ShardDataSourceResolver getShardDataSourceResolver() {
	// ShardDataSourceResolver resolver = new ShardDataSourceResolver();
	// resolver.setLenientFallback(false);
	// resolver.setTargetDataSources(db.entrySet().parallelStream()
	// .collect(Collectors.toMap(Map.Entry::getKey, value ->
	// buildDataSource(value.getValue()))));
	// return resolver;
	// }

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
	 * Allowed bio types.
	 *
	 * @return the list
	 */
	@Bean
	public List<String> allowedBioAttributes() {
		return Collections.unmodifiableList(allowedBioAttributes);
	}

	@Bean
	public List<String> bioAttributes() {
		return Collections.unmodifiableList(bioAttributes);
	}

	@Bean
	public List<String> allowedTypes() {
		return Collections.unmodifiableList(allowedTypes);
	}

	/**
	 * Status.
	 *
	 * @return the map
	 */
	@Bean
	public List<String> uinStatus() {
		return Collections.unmodifiableList(uinStatus);
	}

	/**
	 * Entity manager factory.
	 *
	 * @param dataSource
	 *            the data source
	 * @return the local container entity manager factory bean
	 */
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource);
		em.setPackagesToScan("io.mosip.idrepository.identity.*");

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaPropertyMap(additionalProperties());

		return em;
	}

	//
	// /**
	// * Transaction manager.
	// *
	// * @param emf the emf
	// * @return the platform transaction manager
	// */
	// @Bean
	// public PlatformTransactionManager transactionManager(EntityManagerFactory
	// emf) {
	// JpaTransactionManager transactionManager = new JpaTransactionManager();
	// transactionManager.setEntityManagerFactory(emf);
	// return transactionManager;
	// }
	//
	/**
	 * Additional properties.
	 *
	 * @return the properties
	 */
	private Map<String, Object> additionalProperties() {
		Map<String, Object> jpaProperties = new HashMap<>();
		jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL92Dialect");
		jpaProperties.put("hibernate.temp.use_jdbc_metadata_defaults", Boolean.FALSE);
		jpaProperties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
		jpaProperties.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
		jpaProperties.put("hibernate.ejb.interceptor", interceptor);

		return jpaProperties;
	}

	/**
	 * Builds the data source.
	 *
	 * @param dataSourceValues
	 *            the data source values
	 * @return the data source
	 */
	private DataSource buildDataSource(Map<String, String> dataSourceValues) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource(dataSourceValues.get("url"));
		dataSource.setUsername(dataSourceValues.get("username"));
		dataSource.setPassword(dataSourceValues.get("password"));
		dataSource.setDriverClassName(dataSourceValues.get("driverClassName"));
		return dataSource;
	}

	@Bean
	public DataSource dataSource() {
		return buildDataSource(db.get("shard"));
	}

}
