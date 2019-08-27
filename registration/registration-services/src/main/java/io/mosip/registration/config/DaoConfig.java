package io.mosip.registration.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.tpm.spi.TPMUtil;
import io.mosip.registration.update.SoftwareUpdateHandler;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

/**
 * 
 * Data source and properties loading from the Database.
 * 
 * @author Omsai Eswar M
 *
 */
public class DaoConfig extends HibernateDaoConfig {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(DaoConfig.class);
	
	private static final String DRIVER_CLASS_NAME = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final String DB_PATH_VAR = "mosip.reg.dbpath";
	private static final String URL = "jdbc:derby:";
	private static final String DB_AUTHENITICATION = ";bootPassword=";
	private static final String DB_AUTH_FILE_PATH = "mosip.reg.db.key";
	private static final String MOSIP_CLIENT_TPM_AVAILABILITY = "mosip.reg.client.tpm.availability";

	/**
	 * instance of datasource
	 */
	private static DataSource dataSource;

	/**
	 * connection of datasource
	 */
	static {
		try (InputStream keyStream = DaoConfig.class.getClassLoader().getResourceAsStream("spring.properties")) {
			DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
			driverManagerDataSource.setDriverClassName(DRIVER_CLASS_NAME);

			Properties keys = new Properties();
			keys.load(keyStream);

			String dbConnectionURL = URL + keys.getProperty(DB_PATH_VAR) + DB_AUTHENITICATION;
			
			if (keys.containsKey(MOSIP_CLIENT_TPM_AVAILABILITY)
					&& RegistrationConstants.ENABLE.equalsIgnoreCase(keys.getProperty(MOSIP_CLIENT_TPM_AVAILABILITY))) {
				driverManagerDataSource.setUrl(dbConnectionURL + new String(TPMUtil.asymmetricDecrypt(Base64
						.decodeBase64(keys.getProperty(RegistrationConstants.MOSIP_REGISTRATION_DB_KEY).getBytes()))));
				ApplicationContext.map().put(RegistrationConstants.TPM_AVAILABILITY, RegistrationConstants.ENABLE);
			} else {
				driverManagerDataSource.setUrl(dbConnectionURL + new String(Base64
						.decodeBase64(keys.getProperty(RegistrationConstants.MOSIP_REGISTRATION_DB_KEY).getBytes())));
				ApplicationContext.map().put(RegistrationConstants.TPM_AVAILABILITY, RegistrationConstants.DISABLE);
			}

			dataSource = driverManagerDataSource;
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - DAO Config - DB", APPLICATION_NAME, APPLICATION_ID,
					"Unable to connect to DB -->" + ExceptionUtils.getStackTrace(ioException));
		} catch (RegBaseUncheckedException uncheckedException) {
			LOGGER.error("REGISTRATION - DAO Config - DB", APPLICATION_NAME, APPLICATION_ID,
					"Unable to connect to DB -->" + ExceptionUtils.getStackTrace(uncheckedException));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig#dataSource()
	 */
	@Override
	@Bean(name = "dataSource")
	public DataSource dataSource() {
		return dataSource;
	}

	/**
	 * setting datasource to jdbcTemplate
	 * 
	 * @return JdbcTemplate
	 */
	@Bean
	public static JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource);
	}

	/**
	 * setting jdbcTemplate to PropertiesConfig
	 * 
	 * @return PropertiesConfig
	 */
	@Bean(name = "propertiesConfig")
	public static PropertiesConfig propertiesConfig() {
		return new PropertiesConfig(jdbcTemplate());
	}

	
	/**
	 * setting profile for spring properties
	 * 
	 * @return the {@link PropertyPlaceholderConfigurer} after setting the properties
	 */
	@Bean
	@Lazy(false)
	public static PropertyPlaceholderConfigurer properties() {
		
		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		Resource[] resources = new ClassPathResource[] {new ClassPathResource("spring.properties")};
		ppc.setLocations(resources);

		Properties properties = new Properties();
		properties.putAll(propertiesConfig().getDBProps());

		ppc.setProperties(properties);
		ppc.setTrimValues(true);

		return ppc;
	}
}
