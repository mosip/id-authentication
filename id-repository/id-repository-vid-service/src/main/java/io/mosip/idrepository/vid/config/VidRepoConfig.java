package io.mosip.idrepository.vid.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;

/**
 * The Class Vid Repo Config.
 * 
 * 
 * @author Manoj SP
 * @author Prem Kumar
 *
 */
@Configuration
@ConfigurationProperties("mosip.idrepo.vid")
public class VidRepoConfig extends HibernateDaoConfig {

	/** The env. */
	@Autowired
	private Environment env;

	/** The Interceptor. */
	@Autowired
	private Interceptor interceptor;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.dao.config.BaseDaoConfig#jpaProperties()
	 */
	@Override
	public Map<String, Object> jpaProperties() {
		Map<String, Object> jpaProperties = super.jpaProperties();
		jpaProperties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
		jpaProperties.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
		jpaProperties.put("hibernate.ejb.interceptor", interceptor);
		jpaProperties.replace("hibernate.dialect", "org.hibernate.dialect.PostgreSQL92Dialect");
		return jpaProperties;
	}

	/**
	 * Builds the data source.
	 *
	 * @return the data source
	 */
	@Override
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource(
				env.getProperty(IdRepoConstants.VID_DB_URL.getValue()));
		dataSource.setUsername(env.getProperty(IdRepoConstants.VID_DB_USERNAME.getValue()));
		dataSource.setPassword(env.getProperty(IdRepoConstants.VID_DB_PASSWORD.getValue()));
		dataSource.setDriverClassName(env.getProperty(IdRepoConstants.VID_DB_DRIVER_CLASS_NAME.getValue()));
		return dataSource;
	}
}