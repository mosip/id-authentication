package io.mosip.registration.config;

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

import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;
import io.mosip.kernel.dataaccess.hibernate.constant.HibernatePersistenceConstant;

public class DaoConfig extends HibernateDaoConfig{
	
	@Autowired
	private Environment environment;
	
	@Override
	@Bean(name="dataSource")
	public DataSource dataSource() {
		
		return dataSourceFor();
	}
	
	@Bean(name="dataSourceFor")
	public static DataSource dataSourceFor() {
		/**
		 * The Database path should come from the outside
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
    @Lazy(false)
    public static PropertiesConfig dbProperties() {
        PropertiesConfig propertiesConfig = new PropertiesConfig(jdbcTemplate());
        
        MutablePropertySources sources = new StandardEnvironment().getPropertySources();
        
        sources.addFirst(new MapPropertySource("DB_PROPS", propertiesConfig.getDBProps()));
        
        return propertiesConfig;
    }
	
	@Bean
	public static PropertyPlaceholderConfigurer properties() {
	    PropertyPlaceholderConfigurer ppc
	      = new PropertyPlaceholderConfigurer();
	    Resource[] resources = new ClassPathResource[ ]
	    	      { new ClassPathResource( "spring.properties" ), 
	    	    		  new ClassPathResource("application.properties") };
	    ppc.setLocations( resources );
	    Properties properties = new Properties();
	    properties.putAll(dbProperties().getDBProps());
	    
	    ppc.setProperties(properties);
	    ppc.setIgnoreUnresolvablePlaceholders( true );
	    return ppc;
	}

}
