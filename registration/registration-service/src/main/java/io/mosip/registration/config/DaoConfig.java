package io.mosip.registration.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;
import io.mosip.kernel.dataaccess.hibernate.constant.HibernatePersistenceConstant;

public class DaoConfig extends HibernateDaoConfig{
	
	@Autowired
	private Environment environment;
	
	@Override
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(environment.getProperty(HibernatePersistenceConstant.JDBC_DRIVER));
		dataSource.setUrl(environment.getProperty(HibernatePersistenceConstant.JDBC_URL).concat(environment.getProperty("bootPwd").concat("=").concat(environment.getProperty("bootKey"))));
		dataSource.setUsername(environment.getProperty(HibernatePersistenceConstant.JDBC_USER));
		dataSource.setPassword(environment.getProperty(HibernatePersistenceConstant.JDBC_PASS));
		return dataSource;
	}


}
