package io.mosip.idrepository.vid.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * 
 * @author Prem Kumar
 *
 */
@Configuration
@ConfigurationProperties("mosip.idrepo.vid")
public class VidRepoConfig {
	
	/** The env. */
	@Autowired
	private Environment env;
	
	/** The id. */
	private Map<String, String> id;
	
	/** The status. */
	private List<String> allowedStatus;
	
	/**
	 * Sets the id.
	 *
	 * @param id the id
	 */
	public void setId(Map<String, String> id) {
		this.id = id;
	}
	

	/**
	 * Sets the status.
	 *
	 * @param status the status
	 */
	public void setAllowedStatus(List<String> status) {
		this.allowedStatus = status;
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
	public List<String> allowedStatus() {
		return Collections.unmodifiableList(allowedStatus);
	}
	
	/**
	 * Builds the data source.
	 *
	 * @param dataSourceValues the data source values
	 * @return the data source
	 */
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource(env.getProperty("mosip.idrepo.db.vid.url"));
		dataSource.setUsername(env.getProperty("mosip.idrepo.db.vid.username"));
		dataSource.setPassword(env.getProperty("mosip.idrepo.db.vid.password"));
		dataSource.setDriverClassName(env.getProperty("mosip.idrepo.db.vid.driverClassName"));
		return dataSource;
	}
}