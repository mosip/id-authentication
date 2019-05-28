package io.mosip.registration.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auditmanager.config.AuditConfig;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.kernel.dataaccess.hibernate.repository.impl.HibernateRepositoryImpl;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;
import io.mosip.kernel.logger.logback.factory.Logfactory;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * Spring Configuration class for Registration-Service Module
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Configuration
@EnableAspectJAutoProxy
@Import({ DaoConfig.class, AuditConfig.class, PropertiesConfig.class })
@EnableJpaRepositories(basePackages = "io.mosip.registration", repositoryBaseClass = HibernateRepositoryImpl.class)
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {
		"io.mosip.kernel.idobjectvalidator.impl.IdObjectCompositeValidator",
		"io.mosip.kernel.idobjectvalidator.impl.IdObjectMasterDataValidator" }), basePackages = {
				"io.mosip.registration", "io.mosip.kernel.core", "io.mosip.kernel.keygenerator",
				"io.mosip.kernel.idvalidator", "io.mosip.kernel.ridgenerator", "io.mosip.kernel.qrcode",
				"io.mosip.kernel.core.signatureutil", "io.mosip.kernel.crypto", "io.mosip.kernel.jsonvalidator",
				"io.mosip.kernel.idgenerator", "io.mosip.kernel.virusscanner", "io.mosip.kernel.transliteration",
				"io.mosip.kernel.applicanttype", "io.mosip.kernel.cbeffutil", "io.mosip.kernel.core.pdfgenerator.spi",
				"io.mosip.kernel.pdfgenerator.itext.impl", "io.mosip.kernel.cryptosignature",
				"io.mosip.kernel.core.signatureutil", "io.mosip.kernel.idobjectvalidator.impl" })
@PropertySource(value = { "classpath:spring.properties", "classpath:spring-${spring.profiles.active}.properties" })
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@EnableConfigurationProperties
public class AppConfig {

	private static final RollingFileAppender MOSIP_ROLLING_APPENDER = new RollingFileAppender();

	@Autowired
	@Qualifier("dataSource")
	private DataSource datasource;

	static {
		
		MOSIP_ROLLING_APPENDER.setAppend(true);
		MOSIP_ROLLING_APPENDER.setAppenderName("org.apache.log4j.RollingFileAppender");
		MOSIP_ROLLING_APPENDER.setFileName("logs/registration.log");
		MOSIP_ROLLING_APPENDER.setFileNamePattern("logs/registration-%d{yyyy-MM-dd-HH}-%i.log");
		MOSIP_ROLLING_APPENDER.setMaxFileSize("5MB");
		MOSIP_ROLLING_APPENDER.setTotalCap("50MB");
		MOSIP_ROLLING_APPENDER.setMaxHistory(10);
		MOSIP_ROLLING_APPENDER.setImmediateFlush(true);
		MOSIP_ROLLING_APPENDER.setPrudent(true);
	}

	public static Logger getLogger(Class<?> className) {
		return Logfactory.getDefaultRollingFileLogger(MOSIP_ROLLING_APPENDER, className);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public TemplateManagerBuilder getTemplateManagerBuilder() {
		return new TemplateManagerBuilderImpl();
	}

}
