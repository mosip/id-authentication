/**
 * 
 */
package io.mosip.kernel.auth.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author Ramadurai Saravana Pandian
 * @author Raj Jha 
 *
 */
@Configuration
public class SpringDataSourceConfig {

	@Autowired
	private Environment env;

	@Value("${hikari.maximumPoolSize:25}")
	private int maximumPoolSize;
	@Value("${hikari.validationTimeout:3000}")
	private int validationTimeout;
	@Value("${hikari.connectionTimeout:60000}")
	private int connectionTimeout;
	@Value("${hikari.idleTimeout:200000}")
	private int idleTimeout;
	@Value("${hikari.minimumIdle:0}")
	private int minimumIdle;

	@Bean
	public DataSource dataSource() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName(env.getProperty("iam.datasource.driverClassName"));
		hikariConfig.setJdbcUrl(env.getProperty("iam.datasource.url"));
		hikariConfig.setUsername(env.getProperty("iam.datasource.username"));
		hikariConfig.setPassword(env.getProperty("iam.datasource.password"));
		hikariConfig.setMaximumPoolSize(maximumPoolSize);
		hikariConfig.setValidationTimeout(validationTimeout);
		hikariConfig.setConnectionTimeout(connectionTimeout);
		hikariConfig.setIdleTimeout(idleTimeout);
		hikariConfig.setMinimumIdle(minimumIdle);
		HikariDataSource dataSource = new HikariDataSource(hikariConfig);
		return dataSource;
	}

}
