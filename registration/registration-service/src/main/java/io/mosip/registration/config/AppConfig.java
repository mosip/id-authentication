package io.mosip.registration.config;

import java.util.ResourceBundle;

import org.quartz.JobListener;
import org.quartz.TriggerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.auditmanager.config.AuditConfig;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;
import io.mosip.kernel.dataaccess.hibernate.repository.impl.HibernateRepositoryImpl;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;
import io.mosip.kernel.logger.logback.factory.Logfactory;
import io.mosip.registration.jobs.JobProcessListener;
import io.mosip.registration.jobs.JobTriggerListener;

/**
 * Spring Configuration class for Registration-Service Module
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Configuration
@Import({ HibernateDaoConfig.class, AuditConfig.class })
@EnableJpaRepositories(basePackages = "io.mosip.registration", repositoryBaseClass = HibernateRepositoryImpl.class)
@ComponentScan({"io.mosip.registration", "io.mosip.kernel"})
@PropertySource("spring.properties")
public class AppConfig {

	private static final RollingFileAppender MOSIP_ROLLING_APPENDER = new RollingFileAppender();

	private static final ResourceBundle applicationProperties = ResourceBundle.getBundle("application");
	
	private static final ResourceBundle messageProperties = ResourceBundle.getBundle("messages");

	/**
	 * Job processor
	 */
	@Autowired
	private JobProcessListener jobProcessListener;

	/**
	 * Job Trigger
	 */
	@Autowired
	private JobTriggerListener commonTriggerListener;

	static {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("log4j");
		MOSIP_ROLLING_APPENDER.setAppenderName(resourceBundle.getString("log4j.appender.Appender"));
		MOSIP_ROLLING_APPENDER.setFileName(resourceBundle.getString("log4j.appender.Appender.file"));
		MOSIP_ROLLING_APPENDER.setFileNamePattern(resourceBundle.getString("log4j.appender.Appender.filePattern"));
		MOSIP_ROLLING_APPENDER.setMaxFileSize(resourceBundle.getString("log4j.appender.Appender.maxFileSize"));
		MOSIP_ROLLING_APPENDER.setTotalCap(resourceBundle.getString("log4j.appender.Appender.totalCap"));
		MOSIP_ROLLING_APPENDER.setMaxHistory(10);
		MOSIP_ROLLING_APPENDER.setImmediateFlush(true);
		MOSIP_ROLLING_APPENDER.setPrudent(true);
	}

	public static Logger getLogger(Class<?> className) {
		return Logfactory.getDefaultRollingFileLogger(MOSIP_ROLLING_APPENDER, className);
	}

	public static String getApplicationProperty(String property) {
		return applicationProperties.getString(property);
	}
	
	public static String getMessageProperty(String property) {
		return messageProperties.getString(property);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	/**
	 * scheduler factory bean used to shedule the batch jobs
	 * 
	 * @return scheduler factory which includes job detail and trigger detail
	 */
	@Bean(name = "schedulerFactoryBean")
	public SchedulerFactoryBean getSchedulerFactoryBean() {
		SchedulerFactoryBean schFactoryBean = new SchedulerFactoryBean();
		schFactoryBean.setGlobalTriggerListeners(new TriggerListener[] { commonTriggerListener });
		schFactoryBean.setGlobalJobListeners(new JobListener[] { jobProcessListener });
		return schFactoryBean;
	}
}
