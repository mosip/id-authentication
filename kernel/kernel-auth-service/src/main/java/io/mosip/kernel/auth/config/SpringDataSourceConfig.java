/**
 * 
 */
package io.mosip.kernel.auth.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author Ramadurai Pandian
 *
 */
@Configuration
public class SpringDataSourceConfig {

	@Autowired
	private Environment env;

	@Bean
	public DataSource dataSource() {
		HikariConfig hikariConfig = new HikariConfig();
	    hikariConfig.setDriverClassName(env.getProperty("iam.datasource.driverClassName"));
	    hikariConfig.setJdbcUrl(env.getProperty("iam.datasource.url")); 
	    hikariConfig.setUsername(env.getProperty("iam.datasource.username"));
	    hikariConfig.setPassword(env.getProperty("iam.datasource.password"));
	    hikariConfig.setMaximumPoolSize(20);
	    hikariConfig.setConnectionTimeout(60000);
	    hikariConfig.setIdleTimeout(180000);
	    hikariConfig.setMinimumIdle(10);
	    hikariConfig.setValidationTimeout(2000);
	    HikariDataSource dataSource = new HikariDataSource(hikariConfig);
		return dataSource;

	}

}
