package io.mosip.registration.config;

import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.Scheduled;

import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;
import io.mosip.kernel.dataaccess.hibernate.constant.HibernatePersistenceConstant;

public class DaoConfig extends HibernateDaoConfig{
	
	PropertyPlaceholderConfigurer ppc;
	
	@Override
	@Bean(name="dataSource")
	public DataSource dataSource() {
		
		return dataSourceFor();
	}
	
	@Bean(name="dataSourceFor")
	public static DataSource dataSourceFor() {
		/**
		 * TODO:The Database path should come from the outside and the Password should come from TPM .
		 * i.e. hard coded the values for embedded driver.
		 */
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
		dataSource.setUrl("jdbc:derby:reg;bootPassword=mosip12345");
		
		return dataSource;
	}
	
	@Bean
    public static JdbcTemplate jdbcTemplate()
    {
        return new JdbcTemplate(dataSourceFor());
    }
	

	@Bean
    public static PropertiesConfig dbProperties() {
        PropertiesConfig propertiesConfig = new PropertiesConfig(jdbcTemplate());
        
        MutablePropertySources sources = new StandardEnvironment().getPropertySources();
        
        sources.addFirst(new MapPropertySource("DB_PROPS", propertiesConfig.getDBProps()));
        
        return propertiesConfig;
    }
	
	@Bean
	@Lazy(false)
	public static PropertyPlaceholderConfigurer properties() {
	    PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
	    
	    Resource[] resources = new ClassPathResource[ ] { new ClassPathResource( "spring.properties" )};
	    ppc.setLocations( resources );
	    
	    Properties properties = new Properties();
	    properties.putAll(dbProperties().getDBProps());
	    
	    ppc.setProperties(properties);
	    return ppc;
	}
	
	@Scheduled(fixedRate = 1000*120)
	public void reload() {
		properties();
	}

}
