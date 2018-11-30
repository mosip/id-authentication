package io.kernel.idrepo.config;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class IdRepoConfig.
 *
 * @author Manoj SP
 */
@Configuration
@ConfigurationProperties("mosip.idrepo")
public class IdRepoConfig {

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
	 * Data sources.
	 *
	 * @return the map
	 */
	@Bean
	public Map<Object, Object> dataSources() {
		Map<String, DataSource> dataSourceMap = db.entrySet().parallelStream()
				.collect(Collectors.toMap(Map.Entry::getKey, value -> buildDataSource(value.getValue())));
		return Collections.unmodifiableMap(dataSourceMap);
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
