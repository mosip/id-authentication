package io.mosip.idrepository.vid.config;

import java.util.Collections;
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
@ConfigurationProperties("mosip.vid")
public class VidRepoConfig {
	
	/** The env. */
	@Autowired
	private Environment env;
	
	/** The id. */
	private Map<String, String> id;
	
	/**
	 * Sets the id.
	 *
	 * @param id the id
	 */
	public void setId(Map<String, String> id) {
		this.id = id;
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