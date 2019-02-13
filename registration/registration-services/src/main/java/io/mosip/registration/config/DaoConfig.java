package io.mosip.registration.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;

/**
 * 
 * Data source and properties loading from the Database.
 * 
 * @author Omsai Eswar M
 *
 */
public class DaoConfig extends HibernateDaoConfig {

	private static DataSource dataSource;
	
	static {
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		driverManagerDataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
		driverManagerDataSource.setUrl("jdbc:derby:reg;bootPassword=mosip12345");

		dataSource = driverManagerDataSource;
	}

	@Autowired
	private ApplicationContext applicationContext;

	@Value("${IRIS_THRESHOLD}")
	private String irisThreshold;

	@Override
	@Bean(name = "dataSource")
	public DataSource dataSource() {
		return dataSource;
	}

	@Bean
	public static JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource);
	}

	@Bean(name = "propertiesConfig")
	public static PropertiesConfig propertiesConfig() {
		return new PropertiesConfig(jdbcTemplate());
	}

	@Bean
	@Lazy(false)
	public static PropertyPlaceholderConfigurer properties() {
		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();

		Resource[] resources = new ClassPathResource[] { new ClassPathResource("spring.properties") };
		ppc.setLocations(resources);

		Properties properties = new Properties();
		System.out.println(propertiesConfig().getDBProps());
		properties.putAll(propertiesConfig().getDBProps());

		ppc.setProperties(properties);

		return ppc;
	}

	public void reload() {
		PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = applicationContext
				.getBean(PropertyPlaceholderConfigurer.class);
		PropertiesConfig propertiesConfig = applicationContext.getBean("propertiesConfig", PropertiesConfig.class);

		Properties properties = new Properties();
		properties.putAll(propertiesConfig.getDBProps());

		propertyPlaceholderConfigurer.setProperties(properties);
		System.out.println("Refresh called");
	}

}
