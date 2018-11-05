package io.mosip.registration.config;

import java.util.ResourceBundle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.auditmanager.config.AuditConfig;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;
import io.mosip.kernel.dataaccess.hibernate.repository.impl.HibernateRepositoryImpl;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;

/**
 * Spring Configuration class for Registration-Service Module
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Configuration
@Import({ HibernateDaoConfig.class, AuditConfig.class })
@EnableJpaRepositories(basePackages = "io.mosip.registration.", repositoryBaseClass = HibernateRepositoryImpl.class)
@ComponentScan("io.mosip.registration.")
@PropertySource("spring.properties")
public class AppConfig {

	private static final MosipRollingFileAppender MOSIP_ROLLING_APPENDER = new MosipRollingFileAppender();

	private static final ResourceBundle applicationProperties = ResourceBundle.getBundle("application");
	
	static {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("log4J");
		MOSIP_ROLLING_APPENDER.setAppenderName(resourceBundle.getString("log4j.appender.Appender"));
		MOSIP_ROLLING_APPENDER.setFileName(resourceBundle.getString("log4j.appender.Appender.file"));
		MOSIP_ROLLING_APPENDER.setFileNamePattern(resourceBundle.getString("log4j.appender.Appender.filePattern"));
		MOSIP_ROLLING_APPENDER.setMaxFileSize(resourceBundle.getString("log4j.appender.Appender.maxFileSize"));
		MOSIP_ROLLING_APPENDER.setTotalCap(resourceBundle.getString("log4j.appender.Appender.totalCap"));
		MOSIP_ROLLING_APPENDER.setMaxHistory(10);
		MOSIP_ROLLING_APPENDER.setImmediateFlush(true);
		MOSIP_ROLLING_APPENDER.setPrudent(true);
	}

	public static MosipLogger getLogger(Class<?> className) {
		return MosipLogfactory.getMosipDefaultRollingFileLogger(MOSIP_ROLLING_APPENDER, className);
	}
	
	public static String getApplicationProperty(String property) {
		return applicationProperties.getString(property);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

}
