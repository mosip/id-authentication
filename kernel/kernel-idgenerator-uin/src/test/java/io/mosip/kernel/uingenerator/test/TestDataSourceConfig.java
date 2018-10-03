package io.mosip.kernel.uingenerator.test;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Configuration
class TestDataSourceConfig {

	@Bean
	public DataSource dataSource() throws ClassNotFoundException {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).build();
	}
}