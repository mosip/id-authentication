package io.mosip.authentication.config;

import java.security.Provider;
import java.security.Security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class IDAConfig {

	@Value("${mosip.kernel.keymanager.softhsm.config-path}")
	private String configPath;
	
	@Value("${mosip.ida.database.url}")
	private String dbUrl;
	
	@Value("${mosip.ida.database.user}")
	private String dbUser;
	
	@Value("${mosip.ida.database.password}")
	private String dbPassword;
	
	@Value("${mosip.ida.database.driverClassName}")
	private String dbDriverClassName;
	
	@Value("${mosip.ida.database.schema}")
	private String dbSchemaName;

	@Bean
	public DataSource buildDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource(dbUrl);
		dataSource.setUsername(dbUser);
		dataSource.setPassword(dbPassword);
		dataSource.setDriverClassName(dbDriverClassName);
		dataSource.setSchema(dbSchemaName);
		return dataSource;
	}
	

	@Bean
    public Provider getProvider(){		
		Provider provider = Security.getProvider("SunPKCS11");
		provider = provider.configure(configPath);		
        Security.addProvider(provider);
        return provider;
    }
}
